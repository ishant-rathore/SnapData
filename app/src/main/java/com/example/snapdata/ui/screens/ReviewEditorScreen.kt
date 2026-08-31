package com.example.snapdata.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.DocumentType
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.AccentRed
import com.example.snapdata.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewEditorScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(0) }
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
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.activeTitle.ifBlank { "Review Document" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = uiState.activeDocType.displayName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${(uiState.activeConfidence * 100).toInt()}% Conf",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.activeEngineUsed.contains("Gemini", ignoreCase = true) || uiState.activeEngineUsed.contains("Proxy", ignoreCase = true)) "Cloud AI" else "On-Device",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                            if (uiState.activePdfPageCount > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("•", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${uiState.activePdfPageCount} Pgs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_editor")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showImagePreview = !showImagePreview },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("toggle_doc_image_preview")
                    ) {
                        Icon(
                            imageVector = if (showImagePreview) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (showImagePreview) "Hide Image Preview" else "Show Image Preview"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.saveActiveDocument() },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("save_document_action")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Changes", tint = PrimaryBlue)
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 6.dp, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveActiveDocument() },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("editor_save_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (uiState.isDocumentSaved) "Saved" else "Save")
                    }

                    Button(
                        onClick = {
                            viewModel.saveActiveDocument()
                            viewModel.navigateTo(AppScreen.EXPORT)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("editor_export_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export", color = Color.White, fontWeight = FontWeight.Bold)
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
            val isTabletDualPane = maxWidth >= 720.dp
            val previewBmp = uiState.preprocessedBitmap ?: uiState.activeBitmap

            if (isTabletDualPane && previewBmp != null) {
                // Wide Tablet Split-Screen: Image Preview on Left, Editor on Right
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Image(
                            bitmap = previewBmp.asImageBitmap(),
                            contentDescription = "Source Preview",
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        EditorBannerSection(uiState = uiState, onRetryDbSave = { viewModel.saveActiveDocument() })
                        EditorTabsContent(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            uiState = uiState,
                            viewModel = viewModel,
                            onAddFieldClick = { showAddFieldDialog = true },
                            onAddColClick = { tblIdx ->
                                selectedTableIndexForCol = tblIdx
                                showAddColDialog = true
                            },
                            onCopyText = { text ->
                                clipboardManager.setText(AnnotatedString(text))
                                Toast.makeText(context, "Copied OCR text to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            } else {
                // Standard Compact Layout
                Column(modifier = Modifier.fillMaxSize()) {
                    if (showImagePreview && previewBmp != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Image(
                                bitmap = previewBmp.asImageBitmap(),
                                contentDescription = "Source Preview",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    EditorBannerSection(uiState = uiState, onRetryDbSave = { viewModel.saveActiveDocument() })

                    EditorTabsContent(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        uiState = uiState,
                        viewModel = viewModel,
                        onAddFieldClick = { showAddFieldDialog = true },
                        onAddColClick = { tblIdx ->
                            selectedTableIndexForCol = tblIdx
                            showAddColDialog = true
                        },
                        onCopyText = { text ->
                            clipboardManager.setText(AnnotatedString(text))
                            Toast.makeText(context, "Copied OCR text to clipboard", Toast.LENGTH_SHORT).show()
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
            title = { Text("Add Custom Field", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newFieldKey,
                        onValueChange = { newFieldKey = it },
                        label = { Text("Field Key / Label") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("dialog_new_field_key")
                    )
                    OutlinedTextField(
                        value = newFieldValue,
                        onValueChange = { newFieldValue = it },
                        label = { Text("Field Value") },
                        modifier = Modifier.fillMaxWidth().testTag("dialog_new_field_val")
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
                    modifier = Modifier.heightIn(min = 48.dp).testTag("dialog_confirm_add_field")
                ) {
                    Text("Add Field")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddFieldDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Column Dialog
    if (showAddColDialog) {
        AlertDialog(
            onDismissRequest = { showAddColDialog = false },
            title = { Text("Add Table Column", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newColName,
                    onValueChange = { newColName = it },
                    label = { Text("Column Header Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("dialog_new_col_name")
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
                    modifier = Modifier.heightIn(min = 48.dp).testTag("dialog_confirm_add_col")
                ) {
                    Text("Add Column")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddColDialog = false },
                    modifier = Modifier.heightIn(min = 48.dp)
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EditorBannerSection(
    uiState: com.example.snapdata.ui.UiState,
    onRetryDbSave: () -> Unit
) {
    Column {
        // Diagnostic Status Banner
        if (uiState.activeDiagnosticMessage.isNotBlank()) {
            val isFallback = uiState.activeDiagnosticMessage.contains("fail", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("error", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("recover", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("malform", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("timed out", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("unreachable", ignoreCase = true) ||
                    uiState.activeDiagnosticMessage.contains("rate limit", ignoreCase = true)

            val bannerBg = if (isFallback) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
            val bannerFg = if (isFallback) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            val bannerIcon = if (isFallback) Icons.Default.Info else Icons.Default.CheckCircle

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("editor_diagnostic_banner"),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = bannerIcon,
                        contentDescription = null,
                        tint = bannerFg,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.activeDiagnosticMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = bannerFg,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Database Save Error Banner
        if (uiState.databaseError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("editor_db_error_banner"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.databaseError!!.userMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = onRetryDbSave,
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("retry_db_save_btn")
                    ) {
                        Text("Retry Save", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorTabsContent(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    uiState: com.example.snapdata.ui.UiState,
    viewModel: SnapDataViewModel,
    onAddFieldClick: () -> Unit,
    onAddColClick: (Int) -> Unit,
    onCopyText: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Fields (${uiState.activeFields.size})") },
                icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("tab_key_value_fields")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Tables (${uiState.activeTables.size})") },
                icon = { Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("tab_data_tables")
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { onTabSelected(2) },
                text = { Text("Raw OCR") },
                icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.heightIn(min = 48.dp).testTag("tab_raw_ocr")
            )
        }

        when (selectedTab) {
            0 -> {
                // Key-Value Fields Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Summary Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Executive Summary",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = uiState.activeSummary,
                                    onValueChange = { viewModel.updateSummary(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("doc_summary_editor"),
                                    textStyle = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Extracted Attributes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            TextButton(
                                onClick = onAddFieldClick,
                                modifier = Modifier
                                    .heightIn(min = 48.dp)
                                    .testTag("add_field_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Field")
                            }
                        }
                    }

                    if (uiState.activeFields.isEmpty()) {
                        item {
                            Text(
                                text = "No structured fields extracted yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        itemsIndexed(uiState.activeFields) { index, field ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("field_card_$index"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(1.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = field.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (field.lowConfidenceWarning) {
                                                Surface(
                                                    color = AccentRed.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(4.dp),
                                                    modifier = Modifier.padding(end = 6.dp)
                                                ) {
                                                    Text(
                                                        text = "Low Confidence",
                                                        color = AccentRed,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteField(index) },
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .testTag("delete_field_$index")
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Delete Field", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = field.key,
                                            onValueChange = { newKey -> viewModel.updateField(index, newKey, field.value) },
                                            label = { Text("Key") },
                                            modifier = Modifier.weight(0.45f).testTag("field_key_input_$index"),
                                            singleLine = true,
                                            textStyle = MaterialTheme.typography.bodySmall
                                        )
                                        OutlinedTextField(
                                            value = field.value,
                                            onValueChange = { newVal -> viewModel.updateField(index, field.key, newVal) },
                                            label = { Text("Value") },
                                            modifier = Modifier.weight(0.55f).testTag("field_value_input_$index"),
                                            textStyle = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
            1 -> {
                // Data Tables Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (uiState.activeTables.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Outlined.TableChart, contentDescription = null, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("No tabular structures detected", fontWeight = FontWeight.Bold)
                                    Text("Documents without matrix grids can still be exported with full Key-Value schemas.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    } else {
                        itemsIndexed(uiState.activeTables) { tblIndex, table ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("table_card_$tblIndex"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
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
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row {
                                            TextButton(
                                                onClick = { onAddColClick(tblIndex) },
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text("+ Col", fontSize = 12.sp)
                                            }
                                            TextButton(
                                                onClick = { viewModel.addTableRow(tblIndex) },
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text("+ Row", fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Horizontal scrollable table matrix
                                    val scrollState = rememberScrollState()
                                    Row(modifier = Modifier.horizontalScroll(scrollState)) {
                                        Column {
                                            // Table Header Row
                                            Row(
                                                modifier = Modifier
                                                    .background(PrimaryBlue, RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                            ) {
                                                table.headers.forEach { header ->
                                                    Text(
                                                        text = header,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.width(130.dp)
                                                    )
                                                }
                                                Text("Del", color = Color.White, fontSize = 12.sp, modifier = Modifier.width(48.dp))
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            // Data Rows
                                            table.rows.forEachIndexed { rIdx, row ->
                                                Row(
                                                    modifier = Modifier
                                                        .padding(vertical = 2.dp)
                                                        .background(
                                                            if (rIdx % 2 == 0) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    row.forEachIndexed { cIdx, cellValue ->
                                                        OutlinedTextField(
                                                            value = cellValue,
                                                            onValueChange = { viewModel.updateTableCell(tblIndex, rIdx, cIdx, it) },
                                                            modifier = Modifier
                                                                .width(130.dp)
                                                                .padding(end = 4.dp)
                                                                .testTag("table_cell_${tblIndex}_${rIdx}_$cIdx"),
                                                            singleLine = true,
                                                            textStyle = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                    IconButton(
                                                        onClick = { viewModel.deleteTableRow(tblIndex, rIdx) },
                                                        modifier = Modifier.size(48.dp)
                                                    ) {
                                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Row", tint = AccentRed, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
            2 -> {
                // Raw OCR Text Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Reconstructed OCR Text",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Button(
                                onClick = { onCopyText(uiState.activeRawOcrText) },
                                modifier = Modifier.heightIn(min = 48.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Text", fontSize = 12.sp)
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.activeRawOcrText,
                            onValueChange = { viewModel.updateRawOcrText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .testTag("raw_ocr_editor"),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }
}
