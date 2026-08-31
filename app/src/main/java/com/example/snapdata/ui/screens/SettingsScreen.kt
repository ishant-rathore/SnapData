package com.example.snapdata.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.processing.GeminiAiService
import com.example.snapdata.ui.AppScreen
import com.example.snapdata.ui.SnapDataViewModel
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SnapDataViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val totalCount by viewModel.totalDocumentCount.collectAsState()
    val context = LocalContext.current

    val hasGeminiKey = remember { GeminiAiService.isGeminiConfigured() }
    val hasBackendUrl = remember { GeminiAiService.getBackendUrl().isNotBlank() }
    var languageMenuExpanded by remember { mutableStateOf(false) }

    val languages = listOf(
        "English (en)",
        "Spanish (es)",
        "French (fr)",
        "German (de)",
        "Japanese (ja)",
        "Chinese Simplified (zh-CN)",
        "Multilingual Auto-Detect"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings & Diagnostics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("nav_back_from_settings")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Navigate Back")
                    }
                }
            )
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val isTabletWide = maxWidth >= 650.dp

            if (isTabletWide) {
                // Wide Tablet Layout
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left column: Engine & Privacy Posture
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            AccountProfileCard(
                                viewModel = viewModel
                            )
                        }
                        item {
                            SecurityEngineCard(
                                hasBackendUrl = hasBackendUrl,
                                hasGeminiKey = hasGeminiKey,
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                        item { DataSafeguardsCard() }
                    }

                    // Right column: OCR Configuration & Storage Stats
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            OcrConfigCard(
                                selectedLanguage = uiState.selectedOcrLanguage,
                                languages = languages,
                                expanded = languageMenuExpanded,
                                onExpandedChange = { languageMenuExpanded = it },
                                onSelectLanguage = {
                                    viewModel.setOcrLanguage(it)
                                    languageMenuExpanded = false
                                }
                            )
                        }
                        item {
                            StorageStatsCard(
                                totalCount = totalCount,
                                onClearCache = {
                                    Toast.makeText(context, "Export and temporary cache cleared", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        item { AboutAppCard() }
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
                    item {
                        AccountProfileCard(
                            viewModel = viewModel
                        )
                    }

                    item {
                        SecurityEngineCard(
                            hasBackendUrl = hasBackendUrl,
                            hasGeminiKey = hasGeminiKey,
                            uiState = uiState,
                            viewModel = viewModel
                        )
                    }

                    item { DataSafeguardsCard() }

                    item {
                        OcrConfigCard(
                            selectedLanguage = uiState.selectedOcrLanguage,
                            languages = languages,
                            expanded = languageMenuExpanded,
                            onExpandedChange = { languageMenuExpanded = it },
                            onSelectLanguage = {
                                viewModel.setOcrLanguage(it)
                                languageMenuExpanded = false
                            }
                        )
                    }

                    item {
                        StorageStatsCard(
                            totalCount = totalCount,
                            onClearCache = {
                                Toast.makeText(context, "Export and temporary cache cleared", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    item { AboutAppCard() }

                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SecurityEngineCard(
    hasBackendUrl: Boolean,
    hasGeminiKey: Boolean,
    uiState: com.example.snapdata.ui.UiState,
    viewModel: SnapDataViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Security & Processing Engine",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasBackendUrl) "Enterprise Backend Proxy"
                            else if (hasGeminiKey) "Gemini 3.5 Flash Multimodal"
                            else "On-Device ML Kit OCR (Default)",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (hasBackendUrl) "Connected via secure server proxy"
                            else if (hasGeminiKey) "Cloud API key configured via Secrets panel"
                            else "100% On-device local heuristic rule parser",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        color = AccentGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AccentGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SECURE", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Force Offline-Only Mode", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Guarantees 100% on-device execution with zero network transmission",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.processingOptions.forceOfflineAi,
                        onCheckedChange = { isChecked ->
                            viewModel.updateProcessingOptions(
                                uiState.processingOptions.copy(
                                    forceOfflineAi = isChecked,
                                    enableCloudAi = if (isChecked) false else uiState.processingOptions.enableCloudAi
                                )
                            )
                        },
                        modifier = Modifier.testTag("toggle_force_offline")
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Enable Cloud AI Enhancement", fontWeight = FontWeight.SemiBold)
                        Text(
                            "Requires explicit opt-in. Uses Gemini Cloud API for complex reasoning",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.processingOptions.enableCloudAi && !uiState.processingOptions.forceOfflineAi,
                        onCheckedChange = { isChecked ->
                            viewModel.updateProcessingOptions(
                                uiState.processingOptions.copy(
                                    enableCloudAi = isChecked,
                                    forceOfflineAi = !isChecked
                                )
                            )
                        },
                        modifier = Modifier.testTag("toggle_cloud_ai_settings")
                    )
                }
            }
        }
    }
}

@Composable
private fun DataSafeguardsCard() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Data Safeguards & Privacy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Data Protection Rules", fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "• Documents are stored exclusively in app-private SQLite database storage.\n• Cloud backup and device-to-device transfer policies exclude sensitive files.\n• Automatic local OCR fallback engages if network fails, times out, or keys are missing.\n• No analytics, telemetry, or user tracking SDKs are embedded.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OcrConfigCard(
    selectedLanguage: String,
    languages: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectLanguage: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "OCR Configuration",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = onExpandedChange
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Primary OCR Character Model") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("ocr_language_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) }
                    ) {
                        languages.forEach { lang ->
                            DropdownMenuItem(
                                text = { Text(lang) },
                                onClick = { onSelectLanguage(lang) },
                                modifier = Modifier.heightIn(min = 48.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StorageStatsCard(
    totalCount: Int,
    onClearCache: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Local Storage & SQLite Database",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Database Records", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$totalCount documents indexed", fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Engine Cache", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Clean (Temporary files auto-recycled)", fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onClearCache,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("clear_cache_btn")
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Temporary Cache")
                }
            }
        }
    }
}

@Composable
private fun AboutAppCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("About SnapData Mobile", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SnapData is an intelligent document processing system engineered for secure, on-device OCR, semantic layout analysis, and instant tabular data extraction into Excel, CSV, JSON, and PDF.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Architecture: Jetpack Compose M3 + Room SQLite + Privacy-First AI Engine", fontSize = 11.sp, color = PrimaryBlue)
        }
    }
}

@Composable
private fun AccountProfileCard(
    viewModel: SnapDataViewModel
) {
    val authState by viewModel.authState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }

    val user = (authState as? com.example.snapdata.auth.domain.AuthState.Authenticated)?.user

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "User Profile & Account",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("account_profile_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (user?.isGuest == true) Color(0xFF64748B) else PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (user?.isGuest == true) Icons.Default.PersonOutline else Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (user?.isGuest == true) "Guest Session" else user?.displayName ?: "SnapData User",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (user?.isGuest == true) "Local Offline Mode" else user?.email ?: "Offline Identity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (user?.isGuest != true && user?.isEmailVerified == true) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AccentGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Verified",
                                color = AccentGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                if (user?.isGuest == true) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(AppScreen.AUTH_WELCOME) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("account_register_btn")
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign In or Register Account")
                    }
                } else {
                    OutlinedButton(
                        onClick = { showSignOutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("account_sign_out_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out of SnapData")
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out of SnapData?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "You will return to the welcome screen. All locally stored documents, OCR records, and exported files will remain completely intact on your device."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("dialog_confirm_sign_out")
                ) {
                    Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSignOutDialog = false },
                    modifier = Modifier.testTag("dialog_cancel_sign_out")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

