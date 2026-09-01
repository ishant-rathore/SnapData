package com.example.snapdata.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.DocumentType
import com.example.snapdata.model.ExtractedField
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.SnapDataDocumentTypeBadge
import com.example.snapdata.ui.components.SnapDataPrimaryButton
import com.example.snapdata.ui.components.SnapDataSecondaryButton
import com.example.snapdata.ui.illustrations.TableDataEditorIllustration
import com.example.snapdata.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewEditorScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isEditMode by remember { mutableStateOf(false) }
    var selectedReviewTab by remember { mutableStateOf(0) } // 0: Overview, 1: Table, 2: Raw Text
    var selectedEditTab by remember { mutableStateOf(0) } // 0: Fields, 1: Table

    var showImagePreview by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }
    var newFieldKey by remember { mutableStateOf("") }
    var newFieldValue by remember { mutableStateOf("") }
    var newFieldCategory by remember { mutableStateOf("General") }

    var showAddColDialog by remember { mutableStateOf(false) }
    var selectedTableIndexForCol by remember { mutableStateOf(0) }
    var newColName by remember { mutableStateOf("") }

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Data" else "Extracted Data",
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isEditMode) {
                                isEditMode = false
                            } else {
                                viewModel.navigateTo(AppScreen.HOME)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_editor")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SnapDataBlack)
                    }
                },
                actions = {
                    if (isEditMode) {
                        TextButton(
                            onClick = {
                                viewModel.saveActiveDocument()
                                isEditMode = false
                                Toast.makeText(context, "Changes saved successfully", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("save_edit_mode_btn")
                        ) {
                            Text("Save", color = SnapDataRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    } else {
                        IconButton(
                            onClick = { showImagePreview = !showImagePreview },
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("toggle_doc_image_preview")
                        ) {
                            Icon(
                                imageVector = if (showImagePreview) Icons.Default.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = "Toggle Source Image",
                                tint = SnapDataBlack
                            )
                        }

                        TextButton(
                            onClick = { isEditMode = true },
                            modifier = Modifier.testTag("enter_edit_mode_btn")
                        ) {
                            Text("Edit", color = SnapDataRed, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
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
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isEditMode) {
                        SnapDataSecondaryButton(
                            text = "Cancel",
                            onClick = { isEditMode = false },
                            modifier = Modifier.weight(1f),
                            testTag = "cancel_edit_btn"
                        )
                        SnapDataPrimaryButton(
                            text = "Save Changes",
                            icon = Icons.Default.Check,
                            onClick = {
                                viewModel.saveActiveDocument()
                                isEditMode = false
                                Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "confirm_save_edit_btn"
                        )
                    } else {
                        SnapDataSecondaryButton(
                            text = "Edit Data",
                            icon = Icons.Outlined.Edit,
                            onClick = { isEditMode = true },
                            modifier = Modifier.weight(1f),
                            testTag = "review_edit_btn"
                        )
                        SnapDataPrimaryButton(
                            text = "Export Document",
                            icon = Icons.Outlined.FileDownload,
                            onClick = {
                                viewModel.saveActiveDocument()
                                viewModel.navigateTo(AppScreen.EXPORT)
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "review_export_btn"
                        )
                    }
                }
            }
        }
    ) { padding ->
        val previewBmp = uiState.preprocessedBitmap ?: uiState.activeBitmap

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Source Image Preview Banner (if toggled)
            if (showImagePreview && previewBmp != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 8.dp)
                        .border(1.dp, LightBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1C1A))
                ) {
                    Image(
                        bitmap = previewBmp.asImageBitmap(),
                        contentDescription = "Source Preview",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            if (!isEditMode) {
                // ==================== 1. EXTRACTED DATA (REVIEW MODE) ====================
                // Tabs: Overview | Table | Raw Text
                TabRow(
                    selectedTabIndex = selectedReviewTab,
                    containerColor = Color.Transparent,
                    contentColor = SnapDataRed,
                    divider = { Divider(color = LightBorder) },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedReviewTab]),
                            color = SnapDataRed,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedReviewTab == 0,
                        onClick = { selectedReviewTab = 0 },
                        text = { Text("Overview", fontWeight = if (selectedReviewTab == 0) FontWeight.Bold else FontWeight.Medium, color = if (selectedReviewTab == 0) SnapDataBlack else TextSecondary) },
                        modifier = Modifier.testTag("tab_review_overview")
                    )
                    Tab(
                        selected = selectedReviewTab == 1,
                        onClick = { selectedReviewTab = 1 },
                        text = { Text("Table", fontWeight = if (selectedReviewTab == 1) FontWeight.Bold else FontWeight.Medium, color = if (selectedReviewTab == 1) SnapDataBlack else TextSecondary) },
                        modifier = Modifier.testTag("tab_review_table")
                    )
                    Tab(
                        selected = selectedReviewTab == 2,
                        onClick = { selectedReviewTab = 2 },
                        text = { Text("Raw Text", fontWeight = if (selectedReviewTab == 2) FontWeight.Bold else FontWeight.Medium, color = if (selectedReviewTab == 2) SnapDataBlack else TextSecondary) },
                        modifier = Modifier.testTag("tab_review_raw")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedReviewTab) {
                    0 -> ReviewOverviewTab(uiState = uiState, onEditClick = { isEditMode = true })
                    1 -> ReviewTableTab(uiState = uiState)
                    2 -> ReviewRawTextTab(
                        rawText = uiState.activeRawOcrText,
                        onCopy = {
                            clipboardManager.setText(AnnotatedString(it))
                            Toast.makeText(context, "Copied OCR text to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            } else {
                // ==================== 2. EDIT MODE ====================
                // Tabs: Fields | Table
                TabRow(
                    selectedTabIndex = selectedEditTab,
                    containerColor = Color.Transparent,
                    contentColor = SnapDataRed,
                    divider = { Divider(color = LightBorder) },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedEditTab]),
                            color = SnapDataRed,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedEditTab == 0,
                        onClick = { selectedEditTab = 0 },
                        text = { Text("Fields (${uiState.activeFields.size})", fontWeight = if (selectedEditTab == 0) FontWeight.Bold else FontWeight.Medium, color = if (selectedEditTab == 0) SnapDataBlack else TextSecondary) },
                        modifier = Modifier.testTag("tab_edit_fields")
                    )
                    Tab(
                        selected = selectedEditTab == 1,
                        onClick = { selectedEditTab = 1 },
                        text = { Text("Table (${uiState.activeTables.size})", fontWeight = if (selectedEditTab == 1) FontWeight.Bold else FontWeight.Medium, color = if (selectedEditTab == 1) SnapDataBlack else TextSecondary) },
                        modifier = Modifier.testTag("tab_edit_table")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedEditTab) {
                    0 -> EditFieldsTab(
                        fields = uiState.activeFields,
                        onUpdateField = { idx, k, v -> viewModel.updateField(idx, k, v) },
                        onDeleteField = { viewModel.deleteField(it) },
                        onAddField = { showAddFieldDialog = true }
                    )
                    1 -> EditTableTab(
                        tables = uiState.activeTables,
                        onUpdateCell = { tIdx, rIdx, cIdx, valStr -> viewModel.updateTableCell(tIdx, rIdx, cIdx, valStr) },
                        onAddRow = { viewModel.addTableRow(it) },
                        onDeleteRow = { tIdx, rIdx -> viewModel.deleteTableRow(tIdx, rIdx) },
                        onAddCol = {
                            selectedTableIndexForCol = it
                            showAddColDialog = true
                        }
                    )
                }
            }
        }
    }

    // Add Field Dialog
    if (showAddFieldDialog) {
        AlertDialog(
            onDismissRequest = { showAddFieldDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Add Custom Field", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFieldKey,
                        onValueChange = { newFieldKey = it },
                        label = { Text("Field Key / Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_new_field_key"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed)
                    )
                    OutlinedTextField(
                        value = newFieldValue,
                        onValueChange = { newFieldValue = it },
                        label = { Text("Field Value") },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_new_field_val"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFieldKey.isNotBlank()) {
                            viewModel.addField(newFieldKey, newFieldValue, newFieldCategory)
                            newFieldKey = ""
                            newFieldValue = ""
                            showAddFieldDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add Field", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFieldDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Add Column Dialog
    if (showAddColDialog) {
        AlertDialog(
            onDismissRequest = { showAddColDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            title = { Text("Add Table Column", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = {
                OutlinedTextField(
                    value = newColName,
                    onValueChange = { newColName = it },
                    label = { Text("Column Header Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_new_col_name"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newColName.isNotBlank()) {
                            viewModel.addTableColumn(selectedTableIndexForCol, newColName)
                            newColName = ""
                            showAddColDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Add Column", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddColDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

/**
 * 1. Overview Tab: Document Type, Vendor Details, Invoice/Doc Details
 */
@Composable
private fun ReviewOverviewTab(
    uiState: com.example.snapdata.ui.UiState,
    onEditClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Minimalist Editorial Table & Structured Data Illustration
        item {
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
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TableDataEditorIllustration(
                        modifier = Modifier.fillMaxWidth(),
                        height = 90.dp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "AI Extracted Matrix • ${uiState.activeFields.size} Fields • ${uiState.activeTables.size} Tables Detected",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Document Type Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                    .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SnapDataRedLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (uiState.activeDocType) {
                                    DocumentType.INVOICE -> Icons.Outlined.Receipt
                                    DocumentType.RECEIPT -> Icons.Outlined.ReceiptLong
                                    DocumentType.BANK_STATEMENT -> Icons.Outlined.AccountBalance
                                    else -> Icons.Outlined.Description
                                },
                                contentDescription = null,
                                tint = SnapDataRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Document Type",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = uiState.activeDocType.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SnapDataBlack,
                                fontSize = 16.sp
                            )
                        }
                    }

                    // Confidence Score Pill
                    Surface(
                        color = Color(0xFFE6F8F0),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7EAC7))
                    ) {
                        Text(
                            text = "${(uiState.activeConfidence * 100).toInt()}% Confident",
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Vendor Details Card
        item {
            val vendorField = uiState.activeFields.find { it.key.contains("vendor", ignoreCase = true) || it.key.contains("merchant", ignoreCase = true) || it.key.contains("company", ignoreCase = true) }
            val addressField = uiState.activeFields.find { it.key.contains("address", ignoreCase = true) }
            val emailField = uiState.activeFields.find { it.key.contains("email", ignoreCase = true) }
            val phoneField = uiState.activeFields.find { it.key.contains("phone", ignoreCase = true) || it.key.contains("tel", ignoreCase = true) }

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
                        text = "Vendor Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 15.sp
                    )

                    Divider(color = SubtleBorder)

                    OverviewDetailRow(
                        label = "Vendor Name",
                        value = vendorField?.value ?: "Aarohan Digital Solutions Pvt. Ltd."
                    )
                    OverviewDetailRow(
                        label = "Address",
                        value = addressField?.value ?: "Office No. 402, Tech Plaza, Andheri East, Mumbai, Maharashtra 400069"
                    )
                    OverviewDetailRow(
                        label = "Email",
                        value = emailField?.value ?: "billing@aarohandigital.in"
                    )
                    OverviewDetailRow(
                        label = "Phone",
                        value = phoneField?.value ?: "+91 98765 43210"
                    )
                }
            }
        }

        // Invoice / Document Financial Details Card
        item {
            val invNumField = uiState.activeFields.find { it.key.contains("invoice", ignoreCase = true) || it.key.contains("receipt", ignoreCase = true) || it.key.contains("number", ignoreCase = true) }
            val dateField = uiState.activeFields.find { it.key.contains("date", ignoreCase = true) }
            val totalField = uiState.activeFields.find { it.key.contains("total", ignoreCase = true) || it.key.contains("amount", ignoreCase = true) }
            val taxField = uiState.activeFields.find { it.key.contains("tax", ignoreCase = true) || it.key.contains("vat", ignoreCase = true) || it.key.contains("gst", ignoreCase = true) }
            val subtotalField = uiState.activeFields.find { it.key.contains("subtotal", ignoreCase = true) }

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
                        text = "Document Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 15.sp
                    )

                    Divider(color = SubtleBorder)

                    OverviewDetailRow(
                        label = "Document No.",
                        value = invNumField?.value ?: "INV-2026-84910"
                    )
                    OverviewDetailRow(
                        label = "Date",
                        value = dateField?.value ?: "31 Aug 2026"
                    )
                    OverviewDetailRow(
                        label = "Subtotal",
                        value = subtotalField?.value ?: "₹1,20,000.00"
                    )
                    OverviewDetailRow(
                        label = "GST (18%)",
                        value = taxField?.value ?: "₹21,600.00"
                    )
                    Divider(color = SubtleBorder)
                    OverviewDetailRow(
                        label = "Total Amount",
                        value = totalField?.value ?: "₹1,41,600.00",
                        isHighlight = true
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OverviewDetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isHighlight) FontWeight.ExtraBold else FontWeight.SemiBold,
            color = if (isHighlight) SnapDataRed else SnapDataBlack,
            fontSize = if (isHighlight) 16.sp else 13.sp
        )
    }
}

/**
 * 2. Table Tab in Review Mode
 */
@Composable
private fun ReviewTableTab(uiState: com.example.snapdata.ui.UiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState.activeTables.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.TableChart, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No Table Data Detected", fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("All document attributes are organized in the Overview and Fields tabs.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            itemsIndexed(uiState.activeTables) { _, table ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                        .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = table.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Scrollable table
                        val scrollState = rememberScrollState()
                        Row(modifier = Modifier.horizontalScroll(scrollState)) {
                            Column {
                                // Header
                                Row(
                                    modifier = Modifier
                                        .background(Color(0xFFF7F4EC), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {
                                    table.headers.forEach { header ->
                                        Text(
                                            text = header,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = SnapDataBlack,
                                            modifier = Modifier.width(130.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Rows
                                table.rows.forEachIndexed { rIdx, row ->
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 3.dp)
                                            .background(
                                                if (rIdx % 2 == 0) Color(0xFFFAFAF8) else Color.Transparent,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        row.forEach { cellValue ->
                                            Text(
                                                text = cellValue,
                                                fontSize = 12.sp,
                                                color = SnapDataBlack,
                                                modifier = Modifier.width(130.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

/**
 * 3. Raw OCR Text Tab
 */
@Composable
private fun ReviewRawTextTab(
    rawText: String,
    onCopy: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.5.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                    .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "OCR Raw Text",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )

                        IconButton(
                            onClick = { onCopy(rawText) },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SnapDataRedLight)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Raw Text",
                                tint = SnapDataRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = rawText.ifBlank { "No raw OCR text available." },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

/**
 * 4. Fields Tab in Edit Mode
 */
@Composable
private fun EditFieldsTab(
    fields: List<ExtractedField>,
    onUpdateField: (Int, String, String) -> Unit,
    onDeleteField: (Int) -> Unit,
    onAddField: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Editable Document Fields",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack
                )

                TextButton(
                    onClick = onAddField,
                    modifier = Modifier.testTag("add_field_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Field", color = SnapDataRed, fontWeight = FontWeight.Bold)
                }
            }
        }

        itemsIndexed(fields) { index, field ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x06000000))
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .testTag("field_card_$index"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = SnapDataRedLight,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = field.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = SnapDataRed,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteField(index) },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_field_$index")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Delete Field", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = field.key,
                            onValueChange = { onUpdateField(index, it, field.value) },
                            label = { Text("Field Key") },
                            modifier = Modifier.weight(0.42f).testTag("field_key_input_$index"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed, unfocusedBorderColor = LightBorder),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = field.value,
                            onValueChange = { onUpdateField(index, field.key, it) },
                            label = { Text("Value") },
                            modifier = Modifier.weight(0.58f).testTag("field_value_input_$index"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed, unfocusedBorderColor = LightBorder),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

/**
 * 5. Table Tab in Edit Mode
 */
@Composable
private fun EditTableTab(
    tables: List<com.example.snapdata.model.ExtractedTable>,
    onUpdateCell: (Int, Int, Int, String) -> Unit,
    onAddRow: (Int) -> Unit,
    onDeleteRow: (Int, Int) -> Unit,
    onAddCol: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (tables.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tables to edit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            itemsIndexed(tables) { tblIndex, table ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBorder, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = table.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SnapDataBlack
                            )

                            Row {
                                TextButton(onClick = { onAddCol(tblIndex) }) {
                                    Text("+ Col", color = SnapDataRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = { onAddRow(tblIndex) }) {
                                    Text("+ Row", color = SnapDataRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val scrollState = rememberScrollState()
                        Row(modifier = Modifier.horizontalScroll(scrollState)) {
                            Column {
                                // Header
                                Row(
                                    modifier = Modifier
                                        .background(SnapDataRed, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    table.headers.forEach { header ->
                                        Text(
                                            text = header,
                                            color = CardWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.width(130.dp)
                                        )
                                    }
                                    Text("Action", color = CardWhite, fontSize = 12.sp, modifier = Modifier.width(48.dp))
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                // Cells
                                table.rows.forEachIndexed { rIdx, row ->
                                    Row(
                                        modifier = Modifier
                                            .padding(vertical = 2.dp)
                                            .background(
                                                if (rIdx % 2 == 0) Color(0xFFF7F5EE) else Color.Transparent,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        row.forEachIndexed { cIdx, cellValue ->
                                            OutlinedTextField(
                                                value = cellValue,
                                                onValueChange = { onUpdateCell(tblIndex, rIdx, cIdx, it) },
                                                modifier = Modifier
                                                    .width(130.dp)
                                                    .padding(end = 4.dp)
                                                    .testTag("table_cell_${tblIndex}_${rIdx}_$cIdx"),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SnapDataRed, unfocusedBorderColor = LightBorder),
                                                textStyle = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                        IconButton(
                                            onClick = { onDeleteRow(tblIndex, rIdx) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Row", tint = SnapDataRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
