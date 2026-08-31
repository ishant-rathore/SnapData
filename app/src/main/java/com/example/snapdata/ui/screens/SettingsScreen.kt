package com.example.snapdata.ui.screens

import android.widget.Toast
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
import com.example.snapdata.processing.GeminiAiService
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.components.SnapDataPrimaryButton
import com.example.snapdata.ui.components.SnapDataSecondaryButton
import com.example.snapdata.ui.illustrations.OnDevicePrivacyIllustration
import com.example.snapdata.ui.theme.*

import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.components.branding.SnapDataSymbol
import com.example.snapdata.ui.components.branding.SnapDataTagline
import com.example.snapdata.ui.components.branding.SnapDataWordmark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val totalCount by viewModel.totalDocumentCount.collectAsState()
    val context = LocalContext.current

    val hasGeminiKey = remember { GeminiAiService.isGeminiConfigured() }
    val hasBackendUrl = remember { GeminiAiService.getBackendUrl().isNotBlank() }

    var offlineOcrOnly by remember { mutableStateOf(uiState.processingOptions.useOfflineOcr) }
    var autoContrast by remember { mutableStateOf(uiState.processingOptions.autoContrast) }
    var deskew by remember { mutableStateOf(uiState.processingOptions.deskew) }
    var piiRedaction by remember { mutableStateOf(uiState.processingOptions.enablePiiRedaction) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = WarmCreamBackground,
        topBar = {
            TopAppBar(
                title = { Text("Settings & Brand", fontWeight = FontWeight.Bold, color = SnapDataBlack, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_settings")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SnapDataBlack)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmCreamBackground)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. App Info / Official Brand Header Card
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SnapDataSymbol(
                                size = 48.dp,
                                variant = SnapDataLogoVariant.WHITE_ON_DARK
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                SnapDataWordmark(
                                    fontSize = 20.sp,
                                    snapColor = SnapDataBlack,
                                    dataColor = SnapDataRed
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                SnapDataTagline(
                                    fontSize = 9.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = SubtleBorder)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Build Architecture",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Kotlin • Jetpack Compose • Room • ML Kit",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = SnapDataBlack,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Official Brand Identity & Variants Showcase
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Brand Identity System",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Official design specifications: Document outline, folded corner, data circuit grid, and vivid red camera shutter aperture.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )

                        // Variant Preview Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 1. Primary White on Dark
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F1014))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SnapDataSymbol(size = 44.dp, variant = SnapDataLogoVariant.WHITE_ON_DARK)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Primary", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }

                            // 2. Red on Dark
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F1014))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SnapDataSymbol(size = 44.dp, variant = SnapDataLogoVariant.RED_ON_DARK)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Red Accent", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }

                            // 3. Monochrome
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F1014))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SnapDataSymbol(size = 44.dp, variant = SnapDataLogoVariant.MONOCHROME)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Monochrome", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }

                            // 4. Small Icon
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF0F1014))
                                        .padding(6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SnapDataSymbol(size = 36.dp, variant = SnapDataLogoVariant.SMALL_ICON)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Small Icon", fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                            }
                        }
                    }
                }
            }

            // 2. OCR & AI Processing Engine
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
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Processing Configuration",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )

                        OnDevicePrivacyIllustration(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            height = 80.dp
                        )

                        SettingsToggleRow(
                            title = "Strict On-Device OCR Mode",
                            subtitle = "Guarantees 100% private offline processing without cloud transmission",
                            checked = offlineOcrOnly,
                            onCheckedChange = {
                                offlineOcrOnly = it
                                viewModel.updateProcessingOptions(uiState.processingOptions.copy(forceOfflineAi = it))
                            }
                        )

                        Divider(color = SubtleBorder)

                        SettingsToggleRow(
                            title = "Automatic Contrast & Shadow Removal",
                            subtitle = "Enhance faint ink, thermal receipts and skewed captures",
                            checked = autoContrast,
                            onCheckedChange = {
                                autoContrast = it
                                viewModel.updateProcessingOptions(uiState.processingOptions.copy(enhanceContrast = it))
                            }
                        )

                        Divider(color = SubtleBorder)

                        SettingsToggleRow(
                            title = "Perspective Deskewing",
                            subtitle = "Correct quadrilateral orientation during scanning",
                            checked = deskew,
                            onCheckedChange = {
                                deskew = it
                                viewModel.updateProcessingOptions(uiState.processingOptions.copy(deskew = it))
                            }
                        )

                        Divider(color = SubtleBorder)

                        SettingsToggleRow(
                            title = "PII Data Redaction Engine",
                            subtitle = "Automatically masks social security, phone and card numbers",
                            checked = piiRedaction,
                            onCheckedChange = {
                                piiRedaction = it
                                viewModel.updateProcessingOptions(uiState.processingOptions.copy(enablePiiRedaction = it))
                            }
                        )
                    }
                }
            }

            // 3. Storage & Diagnostics
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
                        Text(
                            text = "Storage & Database",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Stored Documents", style = MaterialTheme.typography.bodyMedium, color = TextDark)
                            Text("$totalCount documents", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Local Storage Engine", style = MaterialTheme.typography.bodyMedium, color = TextDark)
                            Text("Room SQLite v2.7", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        SnapDataSecondaryButton(
                            text = "Clear All Documents",
                            icon = Icons.Default.DeleteForever,
                            onClick = { showClearDataDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "clear_all_docs_btn"
                        )
                    }
                }
            }

            // 4. Help & Interactive User Guide
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
                        Text(
                            text = "Help & Tutorial",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack,
                            fontSize = 15.sp
                        )

                        Text(
                            text = "Learn how to capture documents, enhance images, extract structured data, edit tables, and export with SnapData.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.5.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        SnapDataPrimaryButton(
                            text = "Replay App Guide",
                            icon = Icons.Outlined.School,
                            onClick = {
                                viewModel.startUserGuide()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "replay_user_guide_btn"
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = SnapDataRed) },
            title = { Text("Clear All Documents?", fontWeight = FontWeight.Bold, color = SnapDataBlack) },
            text = { Text("This will permanently remove all $totalCount scanned and processed documents from your local archive. This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearSavedDocuments()
                        showClearDataDialog = false
                        Toast.makeText(context, "Archive cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear All", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
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
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = SnapDataBlack,
                fontSize = 13.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
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
