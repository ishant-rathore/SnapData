package com.example.snapdata.ui.screens

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.model.ExportFormat
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.SnapDataDocumentTypeBadge
import com.example.snapdata.ui.components.SnapDataPrimaryButton
import com.example.snapdata.ui.components.SnapDataSecondaryButton
import com.example.snapdata.ui.illustrations.ExportTransformIllustration
import com.example.snapdata.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = { Text("Export Document", fontWeight = FontWeight.Bold, color = SnapDataBlack, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.REVIEW_EDITOR) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_export")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SnapDataBlack)
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
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.lastExportResult != null && !uiState.isExporting) {
                        SnapDataSecondaryButton(
                            text = "Share File",
                            icon = Icons.Outlined.Share,
                            onClick = { viewModel.shareExportFile() },
                            modifier = Modifier.weight(1f),
                            testTag = "share_export_btn"
                        )
                    }

                    SnapDataPrimaryButton(
                        text = if (uiState.isExporting) "Exporting..." else "Export ${uiState.selectedExportFormat.name}",
                        icon = Icons.Outlined.FileDownload,
                        enabled = !uiState.isExporting,
                        onClick = { viewModel.performExport() },
                        modifier = Modifier.weight(1f),
                        testTag = "generate_export_btn"
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Target Document Details Card
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.activeTitle.ifBlank { "Untitled Document" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SnapDataBlack,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            SnapDataDocumentTypeBadge(docType = uiState.activeDocType)
                        }

                        Divider(color = SubtleBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Extracted Fields", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                                Text("${uiState.activeFields.size} Key-Values", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                            }
                            Column {
                                Text("Tables", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                                Text("${uiState.activeTables.size} Matrix Grids", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                            }
                            Column {
                                Text("Confidence", style = MaterialTheme.typography.bodySmall, color = TextSecondary, fontSize = 11.sp)
                                Text("${(uiState.activeConfidence * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentGreen)
                            }
                        }
                    }
                }
            }

            // Minimalist Hand-Drawn Transformation Illustration
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
                        ExportTransformIllustration(
                            modifier = Modifier.fillMaxWidth(),
                            height = 90.dp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Universal Schema Conversion (JSON • CSV • XLSX • PDF)",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Export Errors
            if (uiState.exportError != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF5C2C4), RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SnapDataRedLight)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = SnapDataRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(uiState.exportError ?: "Export failed", color = TextDark, modifier = Modifier.weight(1f), fontSize = 12.sp)
                            IconButton(onClick = { viewModel.clearExportError() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = SnapDataRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Export Success Card
            if (uiState.lastExportResult != null && !uiState.isExporting) {
                item {
                    val result = uiState.lastExportResult!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFA7EAC7), RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8FAF1))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = CardWhite, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ready: ${result.file.name}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F5132),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${Formatter.formatFileSize(context, result.sizeBytes)} • Saved to device storage",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Format Selection Section
            item {
                Text(
                    text = "Select Export Format",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack,
                    fontSize = 16.sp
                )
            }

            items(ExportFormat.entries.size) { index ->
                val format = ExportFormat.entries[index]
                val isSelected = uiState.selectedExportFormat == format

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isSelected) 2.dp else 1.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) SnapDataRed else LightBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { viewModel.setSelectedExportFormat(format) }
                        .testTag("export_format_${format.name.lowercase()}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Format icon box
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SnapDataRedLight else Color(0xFFF6F4ED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (format) {
                                    ExportFormat.JSON -> Icons.Outlined.Code
                                    ExportFormat.CSV -> Icons.Outlined.TableView
                                    ExportFormat.EXCEL -> Icons.Outlined.GridOn
                                    ExportFormat.PDF -> Icons.Outlined.PictureAsPdf
                                },
                                contentDescription = null,
                                tint = if (isSelected) SnapDataRed else TextSecondary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = format.displayName,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) SnapDataBlack else TextDark,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (isSelected) SnapDataRedLight else Color(0xFFF1EFE8),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = format.extension.uppercase(),
                                        color = if (isSelected) SnapDataRed else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = when (format) {
                                    ExportFormat.EXCEL -> "Spreadsheet workbook with formatted sheets and tables"
                                    ExportFormat.CSV -> "RFC 4180 standard comma-separated tabular values"
                                    ExportFormat.JSON -> "Nested structured document schema with confidence values"
                                    ExportFormat.PDF -> "Multi-page printable report with rendered tables"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        // Radio checkmark indicator
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) SnapDataRed else Color.Transparent)
                                .border(1.5.dp, if (isSelected) SnapDataRed else LightBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = CardWhite, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
