package com.example.snapdata.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ProcessingOptions
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreprocessingScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var options by remember { mutableStateOf(uiState.processingOptions) }
    var currentTitle by remember { mutableStateOf(uiState.activeTitle) }
    var selectedType by remember { mutableStateOf(uiState.activeDocType) }
    var typeMenuExpanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Enhance & Configure", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_prep")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.updateTitle(currentTitle)
                            viewModel.updateDocType(selectedType)
                            viewModel.updateProcessingOptions(options)
                            viewModel.startProcessingPipeline()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_processing_pipeline_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Extract Structured Data",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            val isTabletOrLandscape = maxWidth >= 650.dp

            if (isTabletOrLandscape) {
                // Wide layout: Image preview on Left, Options on Right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Left Image & Multi-Page preview
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.activePdfPageCount > 1) {
                            MultiPagePdfBanner(uiState.activePdfPageCount)
                        }
                        DocumentPreviewCard(
                            bitmap = uiState.activeBitmap,
                            onRotateLeft = { viewModel.rotateActiveBitmap(-90f) },
                            onRotateRight = { viewModel.rotateActiveBitmap(90f) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Right Form Inputs & Filter Options
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DocumentMetadataForm(
                            title = currentTitle,
                            onTitleChange = { currentTitle = it },
                            selectedType = selectedType,
                            onTypeChange = { selectedType = it },
                            typeMenuExpanded = typeMenuExpanded,
                            onExpandedChange = { typeMenuExpanded = it },
                            onDoneAction = { focusManager.clearFocus() }
                        )

                        EnhancementFiltersCard(
                            options = options,
                            onOptionsChange = { options = it }
                        )

                        PrivacyEngineCard(
                            options = options,
                            onOptionsChange = { options = it }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            } else {
                // Standard Compact Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.activePdfPageCount > 1) {
                        MultiPagePdfBanner(uiState.activePdfPageCount)
                    }

                    DocumentPreviewCard(
                        bitmap = uiState.activeBitmap,
                        onRotateLeft = { viewModel.rotateActiveBitmap(-90f) },
                        onRotateRight = { viewModel.rotateActiveBitmap(90f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    )

                    DocumentMetadataForm(
                        title = currentTitle,
                        onTitleChange = { currentTitle = it },
                        selectedType = selectedType,
                        onTypeChange = { selectedType = it },
                        typeMenuExpanded = typeMenuExpanded,
                        onExpandedChange = { typeMenuExpanded = it },
                        onDoneAction = { focusManager.clearFocus() }
                    )

                    EnhancementFiltersCard(
                        options = options,
                        onOptionsChange = { options = it }
                    )

                    PrivacyEngineCard(
                        options = options,
                        onOptionsChange = { options = it }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun MultiPagePdfBanner(pageCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("multi_page_pdf_banner"),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Multi-Page PDF ($pageCount Pages)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                Text(
                    text = "All $pageCount pages will be rendered and OCR'd in sequence. Fields and table matrices will be unified across pages.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DocumentPreviewCard(
    bitmap: Bitmap?,
    onRotateLeft: () -> Unit,
    onRotateRight: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (bitmap != null) {
        Card(
            modifier = modifier.clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Document Preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                // Rotation Action Overlay (Minimum 48dp touch targets)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color(0x99000000), RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onRotateLeft,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("rotate_left_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.RotateLeft,
                            contentDescription = "Rotate Left 90 degrees",
                            tint = Color.White
                        )
                    }
                    Text("Rotate Image", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    IconButton(
                        onClick = onRotateRight,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("rotate_right_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = "Rotate Right 90 degrees",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocumentMetadataForm(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedType: DocumentType,
    onTypeChange: (DocumentType) -> Unit,
    typeMenuExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDoneAction: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Document Title") },
            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDoneAction() }),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("doc_title_input")
        )

        ExposedDropdownMenuBox(
            expanded = typeMenuExpanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = selectedType.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Target Document Schema") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    .testTag("doc_type_dropdown")
            )
            ExposedDropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                DocumentType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text("${type.displayName} (${type.category})") },
                        onClick = {
                            onTypeChange(type)
                            onExpandedChange(false)
                        },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EnhancementFiltersCard(
    options: ProcessingOptions,
    onOptionsChange: (ProcessingOptions) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Image Enhancement & Preprocessing Filters",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Adaptive Contrast Enhancement", fontWeight = FontWeight.SemiBold)
                        Text("Boosts text edges and clarity for highest OCR precision", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = options.enhanceContrast,
                        onCheckedChange = { onOptionsChange(options.copy(enhanceContrast = it)) },
                        modifier = Modifier.testTag("toggle_contrast")
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Crop & Margin Straightening", fontWeight = FontWeight.SemiBold)
                        Text("Trims excess background edges and scanner borders", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = options.autoCrop,
                        onCheckedChange = { onOptionsChange(options.copy(autoCrop = it)) },
                        modifier = Modifier.testTag("toggle_autocrop")
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Shadow & Glare Removal", fontWeight = FontWeight.SemiBold)
                        Text("Equalizes lighting gradient across paper page", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = options.removeShadows,
                        onCheckedChange = { onOptionsChange(options.copy(removeShadows = it)) },
                        modifier = Modifier.testTag("toggle_shadows")
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyEngineCard(
    options: ProcessingOptions,
    onOptionsChange: (ProcessingOptions) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Privacy & Processing Engine",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (options.enableCloudAi && !options.forceOfflineAi) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (options.enableCloudAi && !options.forceOfflineAi) Icons.Default.CloudQueue else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (options.enableCloudAi && !options.forceOfflineAi) PrimaryBlue else Color(0xFF10B981),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (options.enableCloudAi && !options.forceOfflineAi) "Cloud AI Enhancement" else "100% On-Device Mode",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = if (options.enableCloudAi && !options.forceOfflineAi)
                                "Transmits image to Gemini / Enterprise Backend for multimodal semantic reasoning."
                            else
                                "Strict offline local parsing via ML Kit OCR. Zero network data transfer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = options.enableCloudAi && !options.forceOfflineAi,
                        onCheckedChange = { isChecked ->
                            onOptionsChange(
                                options.copy(
                                    enableCloudAi = isChecked,
                                    forceOfflineAi = !isChecked
                                )
                            )
                        },
                        modifier = Modifier.testTag("toggle_cloud_ai")
                    )
                }
            }
        }
    }
}
