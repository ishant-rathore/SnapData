package com.example.snapdata.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
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
import com.example.snapdata.ui.components.SnapDataPrimaryButton
import com.example.snapdata.ui.theme.*

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
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = { Text("Enhance & Configure", fontWeight = FontWeight.Bold, color = SnapDataBlack, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.ACQUISITION) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_prep")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back", tint = SnapDataBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCreamBackground)
            )
        },
        bottomBar = {
            Surface(
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, LightBorder),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                        .navigationBarsPadding()
                ) {
                    SnapDataPrimaryButton(
                        text = "Extract Structured Data",
                        icon = Icons.Default.AutoAwesome,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.updateTitle(currentTitle)
                            viewModel.updateDocType(selectedType)
                            viewModel.updateProcessingOptions(options)
                            viewModel.startProcessingPipeline()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "start_processing_pipeline_btn"
                    )
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
                // Wide layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
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
                // Standard Phone Layout
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
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
            .border(1.dp, Color(0xFFF5C2C4), RoundedCornerShape(12.dp))
            .testTag("multi_page_pdf_banner"),
        colors = CardDefaults.cardColors(containerColor = SnapDataRedLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SnapDataRed),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = CardWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Multi-Page PDF ($pageCount Pages)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataRed
                )
                Text(
                    text = "All pages will be processed and tables merged automatically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDark
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
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp))
            .testTag("prep_document_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Document Preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF9F7F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Description,
                            contentDescription = null,
                            modifier = Modifier.size(54.dp),
                            tint = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Document Ready for Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack
                        )
                        Text(
                            text = "Pre-processing filters active",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Quick Rotation Overlay Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onRotateLeft,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardWhite.copy(alpha = 0.95f))
                        .border(1.dp, LightBorder, CircleShape)
                        .testTag("rotate_left_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                        contentDescription = "Rotate Left 90°",
                        tint = SnapDataBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onRotateRight,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardWhite.copy(alpha = 0.95f))
                        .border(1.dp, LightBorder, CircleShape)
                        .testTag("rotate_right_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.RotateRight,
                        contentDescription = "Rotate Right 90°",
                        tint = SnapDataBlack,
                        modifier = Modifier.size(20.dp)
                    )
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Document Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack,
                fontSize = 15.sp
            )

            // Document Title
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Document Title") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("doc_title_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SnapDataRed,
                    unfocusedBorderColor = LightBorder,
                    focusedLabelColor = SnapDataRed
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDoneAction() })
            )

            // Document Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = onExpandedChange,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Document Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("doc_type_dropdown"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SnapDataRed,
                        unfocusedBorderColor = LightBorder,
                        focusedLabelColor = SnapDataRed
                    )
                )

                ExposedDropdownMenu(
                    expanded = typeMenuExpanded,
                    onDismissRequest = { onExpandedChange(false) },
                    modifier = Modifier.background(CardWhite)
                ) {
                    DocumentType.entries.forEach { docType ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = docType.displayName,
                                    fontWeight = if (selectedType == docType) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedType == docType) SnapDataRed else TextDark
                                )
                            },
                            onClick = {
                                onTypeChange(docType)
                                onExpandedChange(false)
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Extraction Enhancements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack,
                fontSize = 15.sp
            )

            OptionSwitchRow(
                title = "Auto-Contrast & Shadow Removal",
                subtitle = "Enhances faint ink and receipts",
                checked = options.enhanceContrast,
                onCheckedChange = { onOptionsChange(options.copy(enhanceContrast = it)) }
            )

            OptionSwitchRow(
                title = "Perspective Deskewing",
                subtitle = "Straightens angled photo captures",
                checked = options.deskew,
                onCheckedChange = { onOptionsChange(options.copy(deskew = it)) }
            )

            OptionSwitchRow(
                title = "Detect Tables & Line Items",
                subtitle = "Parses tabular data and invoice rows",
                checked = options.detectTables,
                onCheckedChange = { onOptionsChange(options.copy(detectTables = it)) }
            )
        }
    }
}

@Composable
private fun PrivacyEngineCard(
    options: ProcessingOptions,
    onOptionsChange: (ProcessingOptions) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
            .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Privacy & Processing Engine",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack,
                fontSize = 15.sp
            )

            OptionSwitchRow(
                title = "100% Offline On-Device OCR",
                subtitle = "Never sends data outside device",
                checked = options.forceOfflineAi,
                onCheckedChange = { onOptionsChange(options.copy(forceOfflineAi = it)) }
            )

            OptionSwitchRow(
                title = "PII Redaction Engine",
                subtitle = "Masks Aadhaar, PAN, cards & phone numbers",
                checked = options.enablePiiRedaction,
                onCheckedChange = { onOptionsChange(options.copy(enablePiiRedaction = it)) }
            )
        }
    }
}

@Composable
private fun OptionSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextDark)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CardWhite,
                checkedTrackColor = SnapDataRed,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = SubtleBorder
            )
        )
    }
}
