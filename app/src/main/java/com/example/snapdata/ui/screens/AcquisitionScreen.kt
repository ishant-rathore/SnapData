package com.example.snapdata.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.sample.SampleDocument
import com.example.snapdata.sample.SampleDocumentRepository
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.*

enum class ScanMode { SINGLE, BATCH }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcquisitionScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var isFlashOn by remember { mutableStateOf(false) }
    var selectedScanMode by remember { mutableStateOf(ScanMode.SINGLE) }
    var gridEnabled by remember { mutableStateOf(true) }
    var showBoundingBoxes by remember { mutableStateOf(true) }
    var showLaserSweep by remember { mutableStateOf(true) }
    var showConfidenceBadges by remember { mutableStateOf(true) }
    var isLiveScanningActive by remember { mutableStateOf(true) }
    var selectedBlockId by remember { mutableStateOf<String?>(null) }

    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    var showCameraUnavailableDialog by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.onCameraCaptureResult(success)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        }
    }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setPdfUri(uri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.prepareCameraTempUri()
            if (uri != null) {
                takePictureLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Unable to initialize capture storage", Toast.LENGTH_SHORT).show()
            }
        } else {
            val activity = context as? Activity
            if (activity != null && !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                showPermanentlyDeniedDialog = true
            } else {
                Toast.makeText(context, "Camera permission required for document scanning", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun startCameraCapture() {
        if (!isCameraAvailable(context)) {
            showCameraUnavailableDialog = true
            return
        }

        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = viewModel.prepareCameraTempUri()
            if (uri != null) {
                takePictureLauncher.launch(uri)
            }
        } else {
            val activity = context as? Activity
            if (activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                showRationaleDialog = true
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    val activeSample = remember(uiState.activeTitle) {
        SampleDocumentRepository.samples.find { it.title.equals(uiState.activeTitle, ignoreCase = true) }
    }
    val detectedBlocks = remember(activeSample, uiState.activeBitmap) {
        generateDetectedBlocks(activeSample, uiState.activeBitmap)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0E0D))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Top Controls Bar: Close (X), Flash toggle, Gallery Picker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button (X)
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .testTag("nav_back_from_acquisition")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Scanner",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Title pill
                Surface(
                    color = Color(0x33FFFFFF),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SnapDataRed)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Auto-Detecting Edges",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Action icons: Flash & Gallery
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { isFlashOn = !isFlashOn },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isFlashOn) SnapDataRed else Color(0x33FFFFFF))
                            .testTag("toggle_flash_btn")
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Flash",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .testTag("import_gallery_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoLibrary,
                            contentDescription = "Import from Gallery",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // 2. Camera Viewfinder Area with Corner Brackets
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF191816))
                    .testTag("scanner_viewfinder")
            ) {
                // Live Real-Time Text Detection Visual Overlay
                CameraTextDetectionOverlay(
                    detectedBlocks = detectedBlocks,
                    isScanningActive = isLiveScanningActive,
                    showBoundingBoxes = showBoundingBoxes,
                    showLaserSweep = showLaserSweep,
                    showConfidenceBadges = showConfidenceBadges,
                    selectedBlockId = selectedBlockId,
                    onBlockSelected = { selectedBlockId = it?.id },
                    modifier = Modifier.fillMaxSize()
                )

                // Grid lines if enabled
                if (gridEnabled) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val w = size.width
                        val h = size.height
                        val gridColor = Color(0x22FFFFFF)
                        drawLine(gridColor, Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(w * 2 / 3, 0f), Offset(w * 2 / 3, h), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(0f, h / 3), Offset(w, h / 3), strokeWidth = 1.5f)
                        drawLine(gridColor, Offset(0f, h * 2 / 3), Offset(w, h * 2 / 3), strokeWidth = 1.5f)
                    }
                }

                // Reference style Corner Edge Detection Brackets in Red/White
                Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    val w = size.width
                    val h = size.height
                    val cornerLength = 36.dp.toPx()
                    val strokeW = 3.5.dp.toPx()

                    // Top Left Corner Bracket
                    drawLine(SnapDataRed, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth = strokeW)
                    drawLine(SnapDataRed, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth = strokeW)

                    // Top Right Corner Bracket
                    drawLine(SnapDataRed, Offset(w, 0f), Offset(w - cornerLength, 0f), strokeWidth = strokeW)
                    drawLine(SnapDataRed, Offset(w, 0f), Offset(w, cornerLength), strokeWidth = strokeW)

                    // Bottom Left Corner Bracket
                    drawLine(SnapDataRed, Offset(0f, h), Offset(cornerLength, h), strokeWidth = strokeW)
                    drawLine(SnapDataRed, Offset(0f, h), Offset(0f, h - cornerLength), strokeWidth = strokeW)

                    // Bottom Right Corner Bracket
                    drawLine(SnapDataRed, Offset(w, h), Offset(w - cornerLength, h), strokeWidth = strokeW)
                    drawLine(SnapDataRed, Offset(w, h), Offset(w, h - cornerLength), strokeWidth = strokeW)
                }

                // Extract Fields Quick Button
                Button(
                    onClick = {
                        if (activeSample != null) {
                            viewModel.startProcessingPipeline()
                        } else {
                            startCameraCapture()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(24.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .height(44.dp)
                        .testTag("viewfinder_extract_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Extract ${detectedBlocks.size} Detected Fields",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            // 3. Quick Sample Selector Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SampleDocumentRepository.samples) { sample ->
                    Surface(
                        color = if (uiState.activeTitle == sample.title) SnapDataRed else Color(0x22FFFFFF),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { viewModel.selectSampleDocument(sample) }
                            .testTag("scan_sample_${sample.id}")
                    ) {
                        Text(
                            text = sample.title,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // 4. Mode Switcher: SINGLE | BATCH
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0x22FFFFFF),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // SINGLE Mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedScanMode == ScanMode.SINGLE) SnapDataRed else Color.Transparent)
                                .clickable { selectedScanMode = ScanMode.SINGLE }
                                .padding(horizontal = 18.dp, vertical = 6.dp)
                                .testTag("scan_mode_single"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SINGLE",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // BATCH Mode
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (selectedScanMode == ScanMode.BATCH) SnapDataRed else Color.Transparent)
                                .clickable { selectedScanMode = ScanMode.BATCH }
                                .padding(horizontal = 18.dp, vertical = 6.dp)
                                .testTag("scan_mode_batch"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BATCH",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 5. Bottom Shutter Control Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: PDF / File Picker button
                IconButton(
                    onClick = { pdfPickerLauncher.launch("application/pdf") },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0x22FFFFFF))
                        .testTag("import_pdf_btn")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = "Import PDF",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Center: Large Circular White Shutter Button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                        .padding(6.dp)
                        .clickable { startCameraCapture() }
                        .testTag("shutter_capture_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(CardWhite)
                            .border(3.dp, SnapDataRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SnapDataRed)
                        )
                    }
                }

                // Right: Grid / Alignment Guide Toggle
                IconButton(
                    onClick = { gridEnabled = !gridEnabled },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (gridEnabled) SnapDataRedContainer else Color(0x22FFFFFF))
                        .testTag("toggle_grid_btn")
                ) {
                    Icon(
                        imageVector = if (gridEnabled) Icons.Default.GridOn else Icons.Default.GridOff,
                        contentDescription = "Toggle Grid",
                        tint = if (gridEnabled) SnapDataRed else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Permission & Error Dialogs
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = SnapDataRed) },
            title = { Text("Camera Permission Needed", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = {
                Text("SnapData requires camera access so you can scan physical receipts, invoices, and documents directly into the OCR pipeline.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRationaleDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Grant Permission", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("Not Now", color = TextSecondary)
                }
            }
        )
    }

    if (showPermanentlyDeniedDialog) {
        AlertDialog(
            onDismissRequest = { showPermanentlyDeniedDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = SnapDataRed) },
            title = { Text("Camera Access Required", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = {
                Text("Camera access is currently disabled for SnapData. To enable scanning with your camera, please open App Settings and grant Camera permission.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermanentlyDeniedDialog = false
                        openAppSettings(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Open Settings", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermanentlyDeniedDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (showCameraUnavailableDialog) {
        AlertDialog(
            onDismissRequest = { showCameraUnavailableDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.NoPhotography, contentDescription = null, tint = SnapDataRed) },
            title = { Text("Camera Unavailable", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = {
                Text("No camera hardware was found on this device or emulator. You can import sample documents or pick images/PDFs from storage.")
            },
            confirmButton = {
                Button(
                    onClick = { showCameraUnavailableDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

private fun isCameraAvailable(context: Context): Boolean {
    val pm = context.packageManager
    val hasHardware = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ||
            pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    if (!hasHardware) return false

    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    return captureIntent.resolveActivity(pm) != null
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        AppLogger.e(AppLogger.LogDomain.CAMERA, "Unable to open settings: ${e.localizedMessage}", e)
        Toast.makeText(context, "Unable to open application settings", Toast.LENGTH_SHORT).show()
    }
}

private fun generateDetectedBlocks(
    sample: SampleDocument?,
    activeBitmap: Bitmap?
): List<DetectedTextBlock> {
    if (sample != null) {
        val blocks = mutableListOf<DetectedTextBlock>()
        blocks.add(
            DetectedTextBlock(
                id = "blk_title",
                text = sample.title.uppercase(),
                category = "Header",
                confidence = 0.98f,
                relativeLeft = 0.08f,
                relativeTop = 0.08f,
                relativeRight = 0.92f,
                relativeBottom = 0.16f,
                isKeyData = true
            )
        )
        blocks.add(
            DetectedTextBlock(
                id = "blk_type",
                text = "DOCUMENT TYPE: ${sample.type.displayName}",
                category = "Category",
                confidence = 0.95f,
                relativeLeft = 0.08f,
                relativeTop = 0.18f,
                relativeRight = 0.50f,
                relativeBottom = 0.24f
            )
        )
        if (sample.fields.isNotEmpty()) {
            val f1 = sample.fields[0]
            blocks.add(
                DetectedTextBlock(
                    id = "blk_field_0",
                    text = "${f1.key}: ${f1.value}",
                    category = "Key-Value",
                    confidence = 0.94f,
                    relativeLeft = 0.08f,
                    relativeTop = 0.26f,
                    relativeRight = 0.92f,
                    relativeBottom = 0.33f,
                    isKeyData = true
                )
            )
        }
        return blocks
    }

    return listOf(
        DetectedTextBlock(
            id = "blk_1",
            text = "TAX INVOICE / RECEIPT",
            category = "Header",
            confidence = 0.98f,
            relativeLeft = 0.12f,
            relativeTop = 0.09f,
            relativeRight = 0.88f,
            relativeBottom = 0.16f,
            isKeyData = true
        ),
        DetectedTextBlock(
            id = "blk_2",
            text = "INV-2026-84910",
            category = "Invoice Number",
            confidence = 0.96f,
            relativeLeft = 0.10f,
            relativeTop = 0.18f,
            relativeRight = 0.52f,
            relativeBottom = 0.25f,
            isKeyData = true
        ),
        DetectedTextBlock(
            id = "blk_3",
            text = "TOTAL DUE: $1,416.00",
            category = "Amount",
            confidence = 0.99f,
            relativeLeft = 0.30f,
            relativeTop = 0.56f,
            relativeRight = 0.90f,
            relativeBottom = 0.65f,
            isKeyData = true
        )
    )
}
