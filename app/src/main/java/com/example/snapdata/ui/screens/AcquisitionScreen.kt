package com.example.snapdata.ui.screens

import android.Manifest
import com.example.snapdata.logging.AppLogger
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
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
import com.example.snapdata.sample.SampleDocumentRepository
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcquisitionScreen(viewModel: SnapDataViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var gridEnabled by remember { mutableStateOf(true) }

    // Dialog state for permissions and hardware availability
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    var showCameraUnavailableDialog by remember { mutableStateOf(false) }

    // Launcher for taking full resolution photo via system Camera app
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        viewModel.onCameraCaptureResult(success)
    }

    // Modern Photo Picker Launcher (zero storage permissions required)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setImageUri(uri)
        }
    }

    // PDF Picker Launcher
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.setPdfUri(uri)
        }
    }

    // Launcher for camera permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.prepareCameraTempUri()
            if (uri != null) {
                takePictureLauncher.launch(uri)
            }
        } else {
            val activity = context as? Activity
            val shouldShowRationale = activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)
            if (!shouldShowRationale) {
                showPermanentlyDeniedDialog = true
            } else {
                Toast.makeText(context, "Camera permission is needed to scan documents directly", Toast.LENGTH_SHORT).show()
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

    val cameraSupported = remember(context) { isCameraAvailable(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan / Import Document", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_acquisition")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { gridEnabled = !gridEnabled },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("toggle_grid")
                    ) {
                        Icon(
                            imageVector = if (gridEnabled) Icons.Default.GridOn else Icons.Default.GridOff,
                            contentDescription = if (gridEnabled) "Disable Alignment Grid" else "Enable Alignment Grid"
                        )
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val isLandscapeOrTablet = maxWidth >= 600.dp

            if (isLandscapeOrTablet) {
                // Wide / Tablet side-by-side layout
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Viewfinder
                    ViewfinderCard(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight(),
                        gridEnabled = gridEnabled,
                        cameraSupported = cameraSupported,
                        onCaptureClick = { startCameraCapture() }
                    )

                    // Right Controls Panel
                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Sample Carousel
                        SampleSelectionSection(viewModel = viewModel)

                        // Shutter & Actions Bar
                        ShutterActionBar(
                            onGalleryClick = {
                                imagePickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            onShutterClick = { startCameraCapture() },
                            onPdfClick = { pdfPickerLauncher.launch("application/pdf") }
                        )
                    }
                }
            } else {
                // Standard Compact Vertical Layout
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    ViewfinderCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        gridEnabled = gridEnabled,
                        cameraSupported = cameraSupported,
                        onCaptureClick = { startCameraCapture() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SampleSelectionSection(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(16.dp))

                    ShutterActionBar(
                        onGalleryClick = {
                            imagePickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onShutterClick = { startCameraCapture() },
                        onPdfClick = { pdfPickerLauncher.launch("application/pdf") }
                    )
                }
            }

            // Camera Permission Rationale Dialog
            if (showRationaleDialog) {
                AlertDialog(
                    onDismissRequest = { showRationaleDialog = false },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryBlue) },
                    title = { Text("Camera Permission Needed", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("SnapData requires camera access so you can scan physical receipts, invoices, and documents directly into the OCR pipeline.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showRationaleDialog = false
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            modifier = Modifier.testTag("permission_rationale_grant_btn")
                        ) {
                            Text("Grant Permission")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showRationaleDialog = false },
                            modifier = Modifier.testTag("permission_rationale_dismiss_btn")
                        ) {
                            Text("Not Now")
                        }
                    }
                )
            }

            // Permanently Denied / Open Settings Dialog
            if (showPermanentlyDeniedDialog) {
                AlertDialog(
                    onDismissRequest = { showPermanentlyDeniedDialog = false },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    title = { Text("Camera Access Required", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("Camera access is currently disabled for SnapData. To enable scanning with your camera, please open App Settings and grant Camera permission.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermanentlyDeniedDialog = false
                                openAppSettings(context)
                            },
                            modifier = Modifier.testTag("open_settings_btn")
                        ) {
                            Text("Open Settings")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showPermanentlyDeniedDialog = false },
                            modifier = Modifier.testTag("cancel_settings_dialog_btn")
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Camera Unavailable Dialog
            if (showCameraUnavailableDialog) {
                AlertDialog(
                    onDismissRequest = { showCameraUnavailableDialog = false },
                    icon = { Icon(Icons.Default.NoPhotography, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Camera Unavailable", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("No camera hardware or compatible camera app was found on this device. You can still import images and PDF documents from your storage.")
                    },
                    confirmButton = {
                        Button(
                            onClick = { showCameraUnavailableDialog = false },
                            modifier = Modifier.testTag("dismiss_camera_unavailable_btn")
                        ) {
                            Text("OK")
                        }
                    }
                )
            }

            // Acquisition / PDF Import Error Dialog
            val errorMessage = uiState.acquisitionError ?: uiState.pdfError
            errorMessage?.let { errorMsg ->
                AlertDialog(
                    onDismissRequest = { viewModel.dismissAcquisitionError() },
                    icon = { Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    title = { Text("Document Import Error", fontWeight = FontWeight.Bold) },
                    text = { Text(errorMsg) },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.dismissAcquisitionError() },
                            modifier = Modifier.testTag("dismiss_acquisition_error_btn")
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ViewfinderCard(
    modifier: Modifier = Modifier,
    gridEnabled: Boolean,
    cameraSupported: Boolean,
    onCaptureClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .semantics {
                role = Role.Button
                contentDescription = if (cameraSupported) "Launch camera scanner" else "Select document to import"
            }
            .clickable { onCaptureClick() }
            .testTag("scanner_viewfinder"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B111E)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            // Grid & Edge guides overlay
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                if (gridEnabled) {
                    val gridColor = Color(0x22FFFFFF)
                    drawLine(gridColor, Offset(w / 3, 0f), Offset(w / 3, h), strokeWidth = 2f)
                    drawLine(gridColor, Offset(w * 2 / 3, 0f), Offset(w * 2 / 3, h), strokeWidth = 2f)
                    drawLine(gridColor, Offset(0f, h / 3), Offset(w, h / 3), strokeWidth = 2f)
                    drawLine(gridColor, Offset(0f, h * 2 / 3), Offset(w, h * 2 / 3), strokeWidth = 2f)
                }

                // Corner Target Reticles
                val cornerColor = PrimaryBlue
                val strokeW = 8f
                val cornerLen = 45f

                // Top Left
                drawLine(cornerColor, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
                drawLine(cornerColor, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

                // Top Right
                drawLine(cornerColor, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeW)
                drawLine(cornerColor, Offset(w, 0f), Offset(w, cornerLen), strokeW)

                // Bottom Left
                drawLine(cornerColor, Offset(0f, h), Offset(cornerLen, h), strokeW)
                drawLine(cornerColor, Offset(0f, h), Offset(0f, h - cornerLen), strokeW)

                // Bottom Right
                drawLine(cornerColor, Offset(w, h), Offset(w - cornerLen, h), strokeW)
                drawLine(cornerColor, Offset(w, h), Offset(w, h - cornerLen), strokeW)
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = Color(0x330066FF),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (cameraSupported) AccentGreen else Color(0xFFF59E0B))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (cameraSupported) "Camera & OCR Ready" else "Storage Import Mode",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.DocumentScanner,
                    contentDescription = null,
                    tint = Color(0x66FFFFFF),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (cameraSupported) "Tap to Launch Camera" else "Select File to Import",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Or choose an image / PDF document below",
                    color = Color(0xFF94A3B8),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SampleSelectionSection(viewModel: SnapDataViewModel) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Quick Test Sample Documents:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SampleDocumentRepository.samples) { sample ->
                ElevatedFilterChip(
                    selected = false,
                    onClick = { viewModel.selectSampleDocument(sample) },
                    label = { Text(sample.title) },
                    leadingIcon = {
                        Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("acquisition_sample_${sample.id}")
                )
            }
        }
    }
}

@Composable
private fun ShutterActionBar(
    onGalleryClick: () -> Unit,
    onShutterClick: () -> Unit,
    onPdfClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Import from Gallery Button
        OutlinedIconButton(
            onClick = onGalleryClick,
            modifier = Modifier
                .size(56.dp)
                .testTag("import_gallery_btn"),
            shape = CircleShape
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Import from Gallery", tint = PrimaryBlue)
        }

        // Camera Shutter Button (76dp touch target)
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.2f))
                .semantics {
                    role = Role.Button
                    contentDescription = "Capture Document Photo"
                }
                .clickable { onShutterClick() }
                .padding(6.dp)
                .testTag("shutter_capture_btn"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // PDF Import Button
        OutlinedIconButton(
            onClick = onPdfClick,
            modifier = Modifier
                .size(56.dp)
                .testTag("import_pdf_btn"),
            shape = CircleShape
        ) {
            Icon(Icons.Default.PictureAsPdf, contentDescription = "Import PDF Document", tint = PrimaryBlue)
        }
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
        AppLogger.e(AppLogger.LogDomain.CAMERA, "Unable to open application settings: ${e.localizedMessage}", e)
        Toast.makeText(context, "Unable to open application settings", Toast.LENGTH_SHORT).show()
    }
}
