package com.example.snapdata.ui.screens

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.ExportFormat
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export & Share Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.REVIEW_EDITOR) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_export")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
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
                    Button(
                        onClick = { viewModel.performExport() },
                        enabled = !uiState.isExporting,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("generate_export_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp).testTag("export_progress_indicator"),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Exporting...", fontWeight = FontWeight.Bold, color = Color.White)
                        } else {
                            Icon(Icons.Default.FileDownload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export ${uiState.selectedExportFormat.name}", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (uiState.lastExportResult != null && !uiState.isExporting) {
                        FilledTonalButton(
                            onClick = { viewModel.shareExportFile() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("share_export_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share File", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isTabletWide = maxWidth >= 650.dp

            if (isTabletWide) {
                // Wide Tablet Split Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column: Document info and export status
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TargetDocCard(uiState = uiState)

                        if (uiState.exportError != null) {
                            ExportErrorCard(
                                errorMsg = uiState.exportError ?: "Unknown error",
                                onDismiss = { viewModel.clearExportError() },
                                onRetry = { viewModel.performExport() }
                            )
                        }

                        if (uiState.lastExportResult != null && !uiState.isExporting) {
                            ExportSuccessCard(result = uiState.lastExportResult!!, context = context)
                        }
                    }

                    // Right Column: Format selection list
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Select Export Format",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(ExportFormat.values().size) { index ->
                                val format = ExportFormat.values()[index]
                                ExportFormatCard(
                                    format = format,
                                    isSelected = uiState.selectedExportFormat == format,
                                    isExporting = uiState.isExporting,
                                    onSelect = {
                                        viewModel.setSelectedExportFormat(format)
                                        viewModel.performExport(format)
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // Standard Compact Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { TargetDocCard(uiState = uiState) }

                    if (uiState.exportError != null) {
                        item {
                            ExportErrorCard(
                                errorMsg = uiState.exportError ?: "Unknown error",
                                onDismiss = { viewModel.clearExportError() },
                                onRetry = { viewModel.performExport() }
                            )
                        }
                    }

                    item {
                        Text(
                            text = "Select Export Format",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(ExportFormat.values().size) { index ->
                        val format = ExportFormat.values()[index]
                        ExportFormatCard(
                            format = format,
                            isSelected = uiState.selectedExportFormat == format,
                            isExporting = uiState.isExporting,
                            onSelect = {
                                viewModel.setSelectedExportFormat(format)
                                viewModel.performExport(format)
                            }
                        )
                    }

                    if (uiState.lastExportResult != null && !uiState.isExporting) {
                        item {
                            ExportSuccessCard(result = uiState.lastExportResult!!, context = context)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(60.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TargetDocCard(uiState: com.example.snapdata.ui.UiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Target Document",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = uiState.activeTitle.ifBlank { "Untitled Document" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${uiState.activeDocType.displayName} • ${uiState.activeFields.size} fields • ${uiState.activeTables.size} table(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExportErrorCard(
    errorMsg: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("export_error_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Export Error",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMsg,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("export_dismiss_error_btn")
                ) {
                    Text("Dismiss", color = MaterialTheme.colorScheme.error)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("export_retry_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ExportFormatCard(
    format: ExportFormat,
    isSelected: Boolean,
    isExporting: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(enabled = !isExporting) { onSelect() }
            .then(
                if (isSelected) Modifier.border(2.dp, PrimaryBlue, RoundedCornerShape(14.dp))
                else Modifier
            )
            .testTag("export_format_${format.extension}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when (format) {
                            ExportFormat.EXCEL -> Color(0xFF107C41)
                            ExportFormat.CSV -> Color(0xFF0078D4)
                            ExportFormat.JSON -> Color(0xFF8B5CF6)
                            ExportFormat.PDF -> Color(0xFFE11D48)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (format) {
                        ExportFormat.EXCEL -> Icons.Default.TableChart
                        ExportFormat.CSV -> Icons.Default.GridOn
                        ExportFormat.JSON -> Icons.Default.Code
                        ExportFormat.PDF -> Icons.Default.PictureAsPdf
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = format.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = when (format) {
                        ExportFormat.EXCEL -> "Multi-sheet workbook with Overview, Key-Values, and Tabular Matrices."
                        ExportFormat.CSV -> "Universal spreadsheet CSV with UTF-8 BOM ready for Excel, Google Sheets, or Pandas."
                        ExportFormat.JSON -> "Full structured schema with metadata, confidence scores & field tags."
                        ExportFormat.PDF -> "Digitized A4 publication report generated locally with clean typography."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = isSelected,
                enabled = !isExporting,
                onClick = onSelect
            )
        }
    }
}

@Composable
private fun ExportSuccessCard(
    result: com.example.snapdata.export.ExportManager.ExportResult,
    context: android.content.Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("export_success_card"),
        colors = CardDefaults.cardColors(containerColor = AccentGreen.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ready to Share (${result.format.displayName})",
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen
                )
                Text(
                    text = "${result.file.name} • ${Formatter.formatFileSize(context, result.sizeBytes)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
