package com.example.snapdata.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.data.DocumentEntity
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.OperationState
import com.example.snapdata.sample.SampleDocumentRepository
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.*
import com.example.snapdata.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val savedDocs by viewModel.savedDocuments.collectAsState()
    val totalCount by viewModel.totalDocumentCount.collectAsState()
    val isOperationLoading = uiState.appOperationState is OperationState.Loading
    val context = LocalContext.current

    var selectedDocForMenu by remember { mutableStateOf<DocumentEntity?>(null) }
    var showMenuForDocId by remember { mutableStateOf<Long?>(null) }
    var showUploadModal by remember { mutableStateOf(false) }

    // Media and Camera Launchers
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
            }
        } else {
            Toast.makeText(context, "Camera permission needed to scan", Toast.LENGTH_SHORT).show()
        }
    }

    fun startCamera() {
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
    }

    val userName = viewModel.currentUser?.displayName?.takeIf { it.isNotBlank() }
        ?: viewModel.currentUser?.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
        ?: "Aarav"

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            SnapDataTopHeader(
                onNotificationClick = {
                    Toast.makeText(context, "All AI processing systems operational", Toast.LENGTH_SHORT).show()
                },
                onSettingsClick = { viewModel.navigateTo(AppScreen.SETTINGS) }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isTabletWide = maxWidth >= 600.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isTabletWide) 24.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Operation Loading Indicator
                if (isOperationLoading) {
                    item {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .testTag("home_operation_loading"),
                            color = SnapDataRed,
                            trackColor = Color(0xFFFCD5D7)
                        )
                    }
                }

                // 1. Hero Greeting Card
                item {
                    SnapDataHeroCard(
                        userName = userName,
                        onExploreClick = { viewModel.navigateTo(AppScreen.ACQUISITION) }
                    )
                }

                // 2. Main Actions (Camera Scan & Upload Document)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        SnapDataActionCard(
                            title = "Camera Scan",
                            subtitle = "Scan documents\nusing your camera",
                            icon = Icons.Filled.PhotoCamera,
                            isRedIcon = true,
                            onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                            modifier = Modifier.weight(1f),
                            testTag = "home_action_camera_scan"
                        )

                        SnapDataActionCard(
                            title = "Upload Document",
                            subtitle = "PDF, Image\nor Files",
                            icon = Icons.Outlined.UploadFile,
                            isRedIcon = false,
                            onClick = { showUploadModal = true },
                            modifier = Modifier.weight(1f),
                            testTag = "home_action_upload_doc"
                        )
                    }
                }

                // 3. Overview Metrics Section
                item {
                    SnapDataOverviewSection(
                        totalDocuments = totalCount,
                        accuracyPercent = "98.4%",
                        exportsCount = totalCount
                    )
                }

                // 4. Instant Sample Documents Carousel
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Sample Documents",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SnapDataBlack,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Instant Test",
                                style = MaterialTheme.typography.bodySmall,
                                color = SnapDataRed,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(SampleDocumentRepository.samples) { sample ->
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .shadow(1.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
                                        .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                                        .clickable { viewModel.selectSampleDocument(sample) }
                                        .testTag("sample_doc_${sample.id}"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            SnapDataDocumentTypeBadge(docType = sample.type)
                                            Icon(
                                                imageVector = Icons.Default.PlayCircleOutline,
                                                contentDescription = "Test Sample",
                                                tint = SnapDataRed,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = sample.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SnapDataBlack,
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = sample.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            lineHeight = 15.sp,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Recent Documents Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Documents",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 18.sp
                        )

                        TextButton(
                            onClick = { viewModel.navigateTo(AppScreen.HISTORY) },
                            modifier = Modifier.testTag("home_view_all_btn")
                        ) {
                            Text(
                                text = "View all",
                                color = SnapDataRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // 6. Recent Documents List or Clean Empty State
                if (savedDocs.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x08000000))
                                .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
                                .testTag("home_empty_state_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite)
                        ) {
                            SnapDataEmptyState(
                                title = "No Documents Yet",
                                subtitle = "Scan or upload your first document\nto get started.",
                                actionLabel = "Scan First Document",
                                onAction = { viewModel.navigateTo(AppScreen.ACQUISITION) }
                            )
                        }
                    }
                } else {
                    items(savedDocs.take(6)) { doc ->
                        val dateFormatted = formatDocumentDate(doc.createdAt)
                        Box {
                            SnapDataDocumentCard(
                                title = doc.title,
                                dateFormatted = dateFormatted,
                                docType = doc.getTypedDocType(),
                                onClick = { viewModel.reopenDocument(doc) },
                                onMenuClick = { showMenuForDocId = doc.id },
                                testTag = "recent_doc_${doc.id}"
                            )

                            // Dropdown More Menu for Document Actions
                            DropdownMenu(
                                expanded = showMenuForDocId == doc.id,
                                onDismissRequest = { showMenuForDocId = null },
                                modifier = Modifier
                                    .background(CardWhite)
                                    .border(1.dp, LightBorder, RoundedCornerShape(8.dp))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Open & Review", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Outlined.Visibility, contentDescription = null, tint = SnapDataBlack) },
                                    onClick = {
                                        showMenuForDocId = null
                                        viewModel.reopenDocument(doc)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export Document", fontWeight = FontWeight.Medium) },
                                    leadingIcon = { Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = SnapDataBlack) },
                                    onClick = {
                                        showMenuForDocId = null
                                        viewModel.reopenDocument(doc)
                                        viewModel.navigateTo(AppScreen.EXPORT)
                                    }
                                )
                                Divider(color = LightBorder)
                                DropdownMenuItem(
                                    text = { Text("Delete", color = SnapDataRed, fontWeight = FontWeight.Bold) },
                                    leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null, tint = SnapDataRed) },
                                    onClick = {
                                        showMenuForDocId = null
                                        viewModel.deleteDocument(doc)
                                        Toast.makeText(context, "Document deleted", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    // Modal Sheet / Dialog for Upload Document
    if (showUploadModal) {
        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "Upload Document",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Choose a source file to extract structured fields and tables:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    SnapDataSecondaryButton(
                        text = "Photo from Gallery",
                        icon = Icons.Outlined.PhotoLibrary,
                        onClick = {
                            showUploadModal = false
                            imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "upload_gallery_btn"
                    )

                    SnapDataSecondaryButton(
                        text = "PDF / Document File",
                        icon = Icons.Outlined.PictureAsPdf,
                        onClick = {
                            showUploadModal = false
                            pdfPickerLauncher.launch("application/pdf")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "upload_pdf_btn"
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) {
                    Text("Cancel", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

/**
 * Format timestamp nicely into "Today, 10:30 AM", "Yesterday, 04:20 PM", or "21 May 2024, 09:15 AM"
 */
private fun formatDocumentDate(timestamp: Long): String {
    val now = Calendar.getInstance()
    val docCal = Calendar.getInstance().apply { timeInMillis = timestamp }

    val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val fullDateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    val isSameDay = now.get(Calendar.YEAR) == docCal.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == docCal.get(Calendar.DAY_OF_YEAR)

    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val isYesterday = yesterday.get(Calendar.YEAR) == docCal.get(Calendar.YEAR) &&
            yesterday.get(Calendar.DAY_OF_YEAR) == docCal.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay -> "Today, ${timeFormatter.format(Date(timestamp))}"
        isYesterday -> "Yesterday, ${timeFormatter.format(Date(timestamp))}"
        else -> fullDateFormatter.format(Date(timestamp))
    }
}
