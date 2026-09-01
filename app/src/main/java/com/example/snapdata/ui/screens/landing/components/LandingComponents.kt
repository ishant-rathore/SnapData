package com.example.snapdata.ui.screens.landing.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.components.branding.SnapDataSymbol
import com.example.snapdata.ui.illustrations.*
import com.example.snapdata.ui.theme.*

/**
 * SECTION 1: HEADER COMPONENT
 * Minimal top navigation with SnapData logo on left, quick navigation menu on right.
 */
@Composable
fun LandingHeader(
    onNavigateSection: (String) -> Unit,
    onLaunchApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenuDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(WarmCreamBackground)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Official SnapData Logo
        SnapDataLogo(
            variant = SnapDataLogoVariant.FULL_HORIZONTAL,
            iconSize = 34.dp,
            wordmarkSize = 20.sp,
            taglineSize = 8.sp,
            isDarkBackground = false,
            showTagline = true,
            onClick = { onNavigateSection("hero") }
        )

        // Right: Navigation Menu Button
        Box {
            IconButton(
                onClick = { showMenuDropdown = !showMenuDropdown },
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(CardWhite)
                    .border(1.dp, LightBorder, CircleShape)
                    .testTag("landing_menu_button")
            ) {
                Icon(
                    imageVector = if (showMenuDropdown) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Landing Page Menu",
                    tint = SnapDataBlack,
                    modifier = Modifier.size(20.dp)
                )
            }

            DropdownMenu(
                expanded = showMenuDropdown,
                onDismissRequest = { showMenuDropdown = false },
                modifier = Modifier
                    .background(CardWhite)
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .width(220.dp)
            ) {
                DropdownMenuItem(
                    text = { Text("Overview", fontWeight = FontWeight.SemiBold, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("hero") },
                    leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null, tint = SnapDataRed) }
                )
                DropdownMenuItem(
                    text = { Text("Workflow", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("workflow") },
                    leadingIcon = { Icon(Icons.Outlined.AccountTree, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("Features", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("features") },
                    leadingIcon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("App Showcase", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("showcase") },
                    leadingIcon = { Icon(Icons.Outlined.Smartphone, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("AI Intelligence", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("ai") },
                    leadingIcon = { Icon(Icons.Outlined.Psychology, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("Table Extraction", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("tables") },
                    leadingIcon = { Icon(Icons.Outlined.TableChart, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("Privacy & Security", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("privacy") },
                    leadingIcon = { Icon(Icons.Outlined.Shield, contentDescription = null, tint = SnapDataBlack) }
                )
                DropdownMenuItem(
                    text = { Text("Export Formats", fontWeight = FontWeight.Medium, color = SnapDataBlack) },
                    onClick = { showMenuDropdown = false; onNavigateSection("export") },
                    leadingIcon = { Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = SnapDataBlack) }
                )
                HorizontalDivider(color = LightBorder, modifier = Modifier.padding(vertical = 4.dp))
                DropdownMenuItem(
                    text = { Text("Launch SnapData", fontWeight = FontWeight.Bold, color = SnapDataRed) },
                    onClick = { showMenuDropdown = false; onLaunchApp() },
                    leadingIcon = { Icon(Icons.Default.Launch, contentDescription = null, tint = SnapDataRed) }
                )
            }
        }
    }
}

/**
 * SECTION 2: HERO SECTION
 * Eyebrow, bold typography with SnapData red accent, action CTA buttons, realistic Android phone mockup with Home Dashboard UI, and 4 floating cards with curved dashed connectors.
 */
@Composable
fun HeroSection(
    onDownloadClick: () -> Unit,
    onSeeHowItWorksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Eyebrow
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(SnapDataRedContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(SnapDataRed)
            )
            Text(
                text = "AI-POWERED DOCUMENT INTELLIGENCE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SnapDataRed,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Headline
        Text(
            text = buildAnnotatedString {
                append("Turn Documents Into\n")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Structured Data.")
                }
            },
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack,
            textAlign = TextAlign.Center,
            lineHeight = 42.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description
        Text(
            text = "AI-powered document processing that turns PDFs and images into clean, editable, structured data in seconds.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // CTA Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDownloadClick,
                colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.1f)
                    .height(52.dp)
                    .testTag("hero_download_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = CardWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download SnapData",
                    color = CardWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onSeeHowItWorksClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SnapDataBlack),
                border = BorderStroke(1.dp, SnapDataBlack),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(0.9f)
                    .height(52.dp)
                    .testTag("hero_how_it_works_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = null,
                    tint = SnapDataBlack,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "See How It Works",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Subtitle Feature Pills
        Text(
            text = buildAnnotatedString {
                append("Offline-first ")
                withStyle(SpanStyle(color = SnapDataRed)) { append("•") }
                append(" OCR ")
                withStyle(SpanStyle(color = SnapDataRed)) { append("•") }
                append(" AI Extraction ")
                withStyle(SpanStyle(color = SnapDataRed)) { append("•") }
                append(" Multiple Exports")
            },
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Hero Visual: Realistic Android Phone Mockup with Floating Interactive Cards
        HeroPhoneMockupWithFloatingCards()
    }
}

/**
 * Realistic Android Phone Mockup displaying the actual SnapData Home Dashboard UI
 * flanked by floating cards (Camera Scan, Processing, Extracted Data, Editable Table).
 */
@Composable
fun HeroPhoneMockupWithFloatingCards(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Floating Top Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Floating Card 1: Camera Scan
            HeroFloatingCard(
                title = "Camera Scan",
                tag = "Real-Time",
                isRedTag = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SnapDataRedContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = SnapDataRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text("Auto-detect", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Text("Instant Reticle", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }

            // Floating Card 2: AI Processing Pipeline
            HeroFloatingCard(
                title = "AI Pipeline",
                tag = "On-Device",
                isRedTag = false,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceWarm),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = SnapDataBlack,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Column {
                        Text("OCR + AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Text("Confidence 99.4%", fontSize = 9.sp, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Phone Mockup (Realistic Frame)
        Box(
            modifier = Modifier
                .width(280.dp)
                .shadow(12.dp, RoundedCornerShape(32.dp), ambientColor = Color(0x20000000))
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF18191E))
                .border(2.5.dp, Color(0xFF2E303A), RoundedCornerShape(32.dp))
                .padding(6.dp)
        ) {
            // Inner Screen Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(WarmCreamBackground)
                    .padding(12.dp)
            ) {
                // Phone Top Bar (Speaker & Camera Cutout)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF333333))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF222222))
                    )
                }

                // SnapData Mini App Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SnapDataLogo(
                        variant = SnapDataLogoVariant.FULL_HORIZONTAL,
                        iconSize = 22.dp,
                        wordmarkSize = 13.sp,
                        taglineSize = 6.sp,
                        isDarkBackground = false,
                        showTagline = false
                    )
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = SnapDataBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mini Greeting Card (Hello, Aarav)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    border = BorderStroke(1.dp, LightBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Hello, Aarav", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                            Text("Ready to scan documents", fontSize = 9.sp, color = TextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SnapDataRedContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mini Action Buttons (Camera Scan & Upload)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = BorderStroke(1.dp, LightBorder)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Filled.PhotoCamera,
                                contentDescription = null,
                                tint = SnapDataRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Camera Scan", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                            Text("Fast OCR", fontSize = 8.sp, color = TextSecondary)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        border = BorderStroke(1.dp, LightBorder)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Icon(
                                imageVector = Icons.Outlined.UploadFile,
                                contentDescription = null,
                                tint = SnapDataBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Upload File", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                            Text("PDF & Images", fontSize = 8.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mini Overview Stats (128 / 98.4% / 86)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardWhite)
                        .border(1.dp, LightBorder, RoundedCornerShape(10.dp))
                        .padding(vertical = 6.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("128", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Text("Docs", fontSize = 7.5.sp, color = TextSecondary)
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(LightBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("98.4%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
                        Text("Accuracy", fontSize = 7.5.sp, color = TextSecondary)
                    }
                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(LightBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("86", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Text("Exports", fontSize = 7.5.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mini Recent Documents List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Recent Activity", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                    MiniDocItem("Invoice_0324.pdf", "Tax Invoice", "₹38,500.00")
                    MiniDocItem("Receipt_Cafe.jpg", "Cafe Receipt", "₹650.00")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mini Bottom Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardWhite)
                        .border(1.dp, LightBorder, RoundedCornerShape(8.dp))
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(14.dp))
                    Icon(Icons.Outlined.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Icon(Icons.Filled.AddCircle, contentDescription = null, tint = SnapDataBlack, modifier = Modifier.size(14.dp))
                    Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Icon(Icons.Outlined.Settings, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Floating Bottom Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Floating Card 3: Extracted Data
            HeroFloatingCard(
                title = "Extracted Data",
                tag = "INV-0324",
                isRedTag = false,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Aarohan Digital Solutions", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                    Text("Total: ₹38,500.00", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
                }
            }

            // Floating Card 4: Editable Table
            HeroFloatingCard(
                title = "Editable Table",
                tag = "4 Items",
                isRedTag = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Web Design • UI/UX", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                    Text("Export to Excel & CSV", fontSize = 9.sp, color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun MiniDocItem(title: String, type: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CardWhite)
            .border(1.dp, LightBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SnapDataRedContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("📄", fontSize = 7.sp)
            }
            Column {
                Text(title, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, maxLines = 1)
                Text(type, fontSize = 7.sp, color = TextSecondary)
            }
        }
        Text(value, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
    }
}

@Composable
fun HeroFloatingCard(
    title: String,
    tag: String,
    isRedTag: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x0A000000))
            .border(1.dp, LightBorder, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                Text(
                    text = tag,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRedTag) SnapDataRed else TextSecondary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isRedTag) SnapDataRedContainer else SurfaceWarm)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

/**
 * SECTION 3: WORKFLOW SECTION
 * Title: "From Document to Data in Seconds."
 * Horizontal visual workflow:
 * DOCUMENT → SCAN → OCR → AI → STRUCTURED DATA → EXPORT
 * with minimalist line-art icons and interactive step explanations.
 */
@Composable
fun WorkflowSection(
    modifier: Modifier = Modifier
) {
    var selectedStepIndex by remember { mutableStateOf(0) }

    val steps = listOf(
        WorkflowStep(
            number = "01",
            title = "DOCUMENT",
            subtitle = "Physical or Digital",
            description = "Import any PDF, receipt, invoice, bank statement, form, or photo directly from storage or capture live.",
            icon = Icons.Outlined.Description
        ),
        WorkflowStep(
            number = "02",
            title = "SCAN",
            subtitle = "Camera Reticle",
            description = "Real-time edge detection and perspective correction isolate the document with sharp clarity.",
            icon = Icons.Outlined.PhotoCamera
        ),
        WorkflowStep(
            number = "03",
            title = "OCR",
            subtitle = "Text Recognition",
            description = "High-accuracy on-device optical character recognition extracts all text blocks, coordinates, and words.",
            icon = Icons.Outlined.DocumentScanner
        ),
        WorkflowStep(
            number = "04",
            title = "AI",
            subtitle = "Document Intelligence",
            description = "Multimodal semantic intelligence understands document layout, categories, and key-value relationships.",
            icon = Icons.Outlined.Psychology
        ),
        WorkflowStep(
            number = "05",
            title = "STRUCTURED DATA",
            subtitle = "Clean Key-Values",
            description = "Converts messy text into structured fields, dates, vendor details, and clean editable tables.",
            icon = Icons.Outlined.TableChart
        ),
        WorkflowStep(
            number = "06",
            title = "EXPORT",
            subtitle = "Universal Formats",
            description = "Export verified structured data in Excel (.xlsx), CSV (.csv), JSON (.json), or PDF (.pdf) formats with one tap.",
            icon = Icons.Outlined.FileDownload
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Section Header
        Text(
            text = "HOW IT WORKS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("From Document to ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Data")
                }
                append(" in Seconds.")
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "An automated 6-step on-device pipeline transforming unformatted documents into clean, structured records.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal Step Pipeline
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(steps.size) { index ->
                val step = steps[index]
                val isSelected = (index == selectedStepIndex)

                WorkflowStepCard(
                    step = step,
                    isSelected = isSelected,
                    onClick = { selectedStepIndex = index },
                    modifier = Modifier.width(135.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Step Detailed Card
        val currentStep = steps[selectedStepIndex]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SnapDataRedContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = currentStep.icon,
                        contentDescription = null,
                        tint = SnapDataRed,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "STEP ${currentStep.number}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataRed
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = currentStep.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentStep.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

data class WorkflowStep(
    val number: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun WorkflowStepCard(
    step: WorkflowStep,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) SnapDataRed else LightBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .testTag("workflow_step_${step.number}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) CardWhite else SurfaceWarm)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.number,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) SnapDataRed else TextSecondary
                )
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = if (isSelected) SnapDataRed else SnapDataBlack,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = step.title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = step.subtitle,
                fontSize = 9.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * SECTION 4: FEATURES SECTION
 * Title: "Everything Your Documents Need."
 * 6 premium cards with 01-06 numbering, minimal icons, and crisp borders.
 */
@Composable
fun FeatureGridSection(
    modifier: Modifier = Modifier
) {
    val features = listOf(
        FeatureItem(
            number = "01",
            title = "Document Scanning",
            description = "Scan documents directly using your camera with real-time frame guidance, skew correction, and auto-cropping.",
            icon = Icons.Outlined.PhotoCamera
        ),
        FeatureItem(
            number = "02",
            title = "Powerful OCR",
            description = "Extract text accurately from images and PDFs using high-performance on-device machine learning models.",
            icon = Icons.Outlined.DocumentScanner
        ),
        FeatureItem(
            number = "03",
            title = "AI Document Intelligence",
            description = "Understand document layout, type, semantics, and field relationships automatically without rigid templates.",
            icon = Icons.Outlined.Psychology
        ),
        FeatureItem(
            number = "04",
            title = "Smart Data Extraction",
            description = "Detect names, dates, totals, phone numbers, addresses, and line-item details with high confidence scoring.",
            icon = Icons.Outlined.Key
        ),
        FeatureItem(
            number = "05",
            title = "Table Extraction",
            description = "Turn complex multi-column document tables into clean, structured data with automatic column header detection.",
            icon = Icons.Outlined.TableChart
        ),
        FeatureItem(
            number = "06",
            title = "Editable Data",
            description = "Review, modify, add rows, and refine extracted information before saving, with instant subtotal and tax calculation.",
            icon = Icons.Outlined.Edit
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Eyebrow
        Text(
            text = "CAPABILITIES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Everything Your ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Documents")
                }
                append(" Need.")
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Six powerful core features designed to handle any invoice, receipt, or business document with enterprise accuracy.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 6 Feature Cards
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            features.forEach { feature ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                        .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SurfaceWarm)
                                .border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = null,
                                tint = SnapDataRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = feature.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SnapDataBlack
                                )
                                Text(
                                    text = feature.number,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SnapDataRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = feature.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FeatureItem(
    val number: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * SECTION 5: APP SHOWCASE SECTION
 * Title: "Meet SnapData."
 * Showcase 8 real application screens in elegant phone mockup frames with interactive preview.
 */
@Composable
fun AppShowcaseSection(
    onSelectScreen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val showcaseScreens = listOf(
        ShowcaseItem("1", "Home Dashboard", "Overview metrics, quick actions & recent scans", Icons.Outlined.Home),
        ShowcaseItem("2", "Camera Scan", "High-speed camera viewfinder with reticle frame", Icons.Outlined.PhotoCamera),
        ShowcaseItem("3", "Processing Document", "Multi-stage pipeline (Pre-processing, OCR, AI)", Icons.Outlined.HourglassTop),
        ShowcaseItem("4", "Extracted Data", "Structured key-value pairs & vendor details", Icons.Outlined.Assessment),
        ShowcaseItem("5", "Edit Data", "Interactive table editor with auto-calculations", Icons.Outlined.TableRows),
        ShowcaseItem("6", "Document History", "Searchable archive with filter chips & badges", Icons.Outlined.History),
        ShowcaseItem("7", "Export Document", "One-tap export to XLSX, CSV, JSON & PDF", Icons.Outlined.FileDownload),
        ShowcaseItem("8", "Settings", "On-device AI model setup & language options", Icons.Outlined.Settings)
    )

    var previewScreen by remember { mutableStateOf<ShowcaseItem?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Section Header
        Text(
            text = "APPLICATION SHOWCASE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Meet ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("SnapData.")
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Designed for seamless mobile productivity. Tap any screen to inspect details or launch directly.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Grid of 8 Showcase Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            showcaseScreens.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(1.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x06000000))
                                .border(1.dp, LightBorder, RoundedCornerShape(14.dp))
                                .clickable { previewScreen = item }
                                .testTag("showcase_item_${item.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SnapDataRedContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = SnapDataRed,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Text(
                                        text = "#0${item.id}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = item.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SnapDataBlack,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.subtitle,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    lineHeight = 14.sp,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Screen Details
    if (previewScreen != null) {
        AlertDialog(
            onDismissRequest = { previewScreen = null },
            confirmButton = {
                Button(
                    onClick = {
                        val screen = previewScreen
                        previewScreen = null
                        if (screen != null) {
                            onSelectScreen(screen.title)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Open in SnapData", color = CardWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { previewScreen = null }) {
                    Text("Close", color = SnapDataBlack)
                }
            },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = previewScreen?.icon ?: Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = SnapDataRed,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = previewScreen?.title ?: "",
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = previewScreen?.subtitle ?: "",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceWarm)
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Interactive feature ready to test live in the application workspace.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SnapDataBlack
                        )
                    }
                }
            },
            shape = RoundedCornerShape(18.dp),
            containerColor = CardWhite
        )
    }
}

data class ShowcaseItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

/**
 * SECTION 6: AI INTELLIGENCE SECTION
 * Title: "AI That Understands Your Documents."
 * Feature checklist on left, line-art transformation in center, extracted key-value card with red totals.
 */
@Composable
fun AiIntelligenceSection(
    modifier: Modifier = Modifier
) {
    val aiFeatures = listOf(
        "Document Type Detection",
        "Key-Value Extraction",
        "Table Detection",
        "Document Summary",
        "Confidence Scoring"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "DOCUMENT INTELLIGENCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("AI That ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Understands")
                }
                append(" Your Documents.")
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Going beyond simple text recognition. SnapData understands semantics, document structure, and financial figures.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // AI Illustration: Document -> AI Brain with red sparks
        AiDocumentIntelligenceIllustration(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Checkmark features list
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(1.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
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
                    text = "Semantic Extraction Capabilities",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack
                )

                aiFeatures.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(SnapDataRedContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
                        }
                        Text(
                            text = item,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = SnapDataBlack
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Extracted Fields Card Sample
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Extracted Fields", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                    Text(
                        text = "99.4% Verified",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SnapDataRedContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ExtractedFieldRow("Document Type", "GST Tax Invoice")
                ExtractedFieldRow("Name", "Aarav Sharma")
                ExtractedFieldRow("Date", "21 August 2026")
                ExtractedFieldRow("Invoice No", "INV-2026-1042")
                ExtractedFieldRow("Phone", "+91 98765 43210")
                ExtractedFieldRow("Total", "₹38,500.00", isHighlighted = true)
            }
        }
    }
}

@Composable
private fun ExtractedFieldRow(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = TextSecondary)
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isHighlighted) SnapDataRed else SnapDataBlack
        )
    }
}

/**
 * SECTION 7: TABLE EXTRACTION SECTION
 * Title: "From Messy Tables to Clean Data."
 * Scanned Invoice → OCR / AI Processing → Interactive Live Table with editable cells and auto-total calculation.
 */
@Composable
fun TableExtractionSection(
    modifier: Modifier = Modifier
) {
    var webDesignQty by remember { mutableStateOf(1) }
    var uiUxQty by remember { mutableStateOf(1) }
    var devQty by remember { mutableStateOf(1) }

    val webDesignTotal = webDesignQty * 12000
    val uiUxTotal = uiUxQty * 8000
    val devTotal = devQty * 15000
    val subtotal = webDesignTotal + uiUxTotal + devTotal
    val tax = (subtotal * 0.18f).toInt()
    val total = subtotal + tax

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "TABLE EXTRACTION",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("From Messy Tables to ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Clean Data.")
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Extract multi-line tables with automatic column alignment. Review and adjust values in real-time.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Transformation Pipeline Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceWarm)
                .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Scanned Document", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SnapDataBlack)
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(16.dp))
            Text("OCR & AI", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SnapDataRed)
            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SnapDataRed, modifier = Modifier.size(16.dp))
            Text("Editable Table", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Editable Table Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceWarm)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Item", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.weight(1.4f))
                    Text("Qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    Text("Price", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                    Text("Total", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Table Items
                InteractiveTableRow(
                    item = "Web Design",
                    qty = webDesignQty,
                    price = "₹12,000",
                    total = "₹$webDesignTotal",
                    onQtyChange = { if (it in 1..9) webDesignQty = it }
                )

                InteractiveTableRow(
                    item = "UI/UX Design",
                    qty = uiUxQty,
                    price = "₹8,000",
                    total = "₹$uiUxTotal",
                    onQtyChange = { if (it in 1..9) uiUxQty = it }
                )

                InteractiveTableRow(
                    item = "Development",
                    qty = devQty,
                    price = "₹15,000",
                    total = "₹$devTotal",
                    onQtyChange = { if (it in 1..9) devQty = it }
                )

                HorizontalDivider(color = LightBorder, modifier = Modifier.padding(vertical = 8.dp))

                // Summary Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Subtotal", fontSize = 11.sp, color = TextSecondary)
                    Text("₹$subtotal", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SnapDataBlack)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("GST (18%)", fontSize = 11.sp, color = TextSecondary)
                    Text("₹$tax", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SnapDataBlack)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SnapDataRedContainer)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
                    Text("₹$total", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SnapDataRed)
                }
            }
        }
    }
}

@Composable
private fun InteractiveTableRow(
    item: String,
    qty: Int,
    price: String,
    total: String,
    onQtyChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(item, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SnapDataBlack, modifier = Modifier.weight(1.4f))

        // Interactive Qty Switcher
        Row(
            modifier = Modifier.weight(0.7f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "-",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SnapDataRed,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onQtyChange(qty - 1) }
                    .padding(horizontal = 4.dp)
            )
            Text(text = "$qty", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.padding(horizontal = 2.dp))
            Text(
                text = "+",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SnapDataRed,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onQtyChange(qty + 1) }
                    .padding(horizontal = 4.dp)
            )
        }

        Text(price, fontSize = 11.sp, color = TextSecondary, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
        Text(total, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
    }
}

/**
 * SECTION 8: PRIVACY SECTION
 * Dark charcoal container, "Your Documents. Your Data.", PHONE → OCR → AI → STRUCTURED DATA diagram with shield/lock, 4 checkmarks.
 */
@Composable
fun PrivacySection(
    modifier: Modifier = Modifier
) {
    val privacyBadges = listOf(
        "Offline-first Architecture",
        "Local On-Device OCR Processing",
        "Encrypted SQLite Storage",
        "Zero Mandatory Cloud Uploads"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111216))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Eyebrow
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF22242C))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SnapDataRed,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "SECURITY & PRIVACY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CardWhite,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Headline
            Text(
                text = buildAnnotatedString {
                    append("Your Documents.\nYour ")
                    withStyle(SpanStyle(color = SnapDataRed)) {
                        append("Data.")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CardWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = "Designed with an offline-first architecture so processing can happen locally on the device after initial AI model setup.",
                fontSize = 13.sp,
                color = Color(0xFFA0A0A8),
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Device Illustration
            OnDevicePrivacyIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                height = 90.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Badges
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                privacyBadges.forEach { badge ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(SnapDataRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✓", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CardWhite)
                        }
                        Text(
                            text = badge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = CardWhite
                        )
                    }
                }
            }
        }
    }
}

/**
 * SECTION 9: EXPORT SECTION
 * Title: "Your Data. Your Format."
 * 4 interactive export format cards: Excel, CSV, JSON, PDF with format switcher & live sample export preview.
 */
@Composable
fun ExportSection(
    modifier: Modifier = Modifier
) {
    var selectedFormatIndex by remember { mutableStateOf(0) }

    val formats = listOf(
        ExportFormatItem("Excel", ".xlsx", "Formatted spreadsheet with column headers and total formulas", "Item\tQty\tPrice\tTotal\nWeb Design\t1\t₹12,000\t₹12,000\nUI/UX\t1\t₹8,000\t₹8,000\nDev\t1\t₹15,000\t₹15,000\nGST (18%)\t\t\t₹6,300\nTotal\t\t\t₹41,300", Icons.Outlined.TableChart),
        ExportFormatItem("CSV", ".csv", "Comma-separated raw values compatible with any database", "Item,Qty,Price,Total\nWeb Design,1,12000,12000\nUI/UX Design,1,8000,8000\nDevelopment,1,15000,15000", Icons.Outlined.GridOn),
        ExportFormatItem("JSON", ".json", "Structured object hierarchy for API integrations", "{\n  \"vendor\": \"Aarohan Digital Solutions\",\n  \"invoice_no\": \"INV-2026-1042\",\n  \"date\": \"2026-08-28\",\n  \"total\": 41300.00\n}", Icons.Outlined.Code),
        ExportFormatItem("PDF", ".pdf", "Clean, formatted vector document summary", "Document Summary:\nGST Tax Invoice #INV-2026-1042\nVendor: Aarohan Digital Solutions Pvt. Ltd.\nTotal: ₹41,300.00 INR", Icons.Outlined.PictureAsPdf)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "UNIVERSAL EXPORTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Your Data. Your ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Format.")
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Export structured data effortlessly into enterprise spreadsheets, developer payloads, or clean reports.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Export Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            formats.forEachIndexed { index, format ->
                val isSelected = (index == selectedFormatIndex)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x06000000))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) SnapDataRed else LightBorder,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedFormatIndex = index }
                        .testTag("export_format_${format.name}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) CardWhite else SurfaceWarm)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = format.icon,
                            contentDescription = null,
                            tint = if (isSelected) SnapDataRed else SnapDataBlack,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(format.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SnapDataBlack)
                        Text(format.extension, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = if (isSelected) SnapDataRed else TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Format Output Live Preview Card
        val activeFormat = formats[selectedFormatIndex]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp), ambientColor = Color(0x06000000))
                .border(1.dp, LightBorder, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${activeFormat.name} Format Preview",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack
                    )
                    Text(
                        text = activeFormat.extension,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataRed,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SnapDataRedContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = activeFormat.description,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF18191E))
                        .padding(12.dp)
                ) {
                    Text(
                        text = activeFormat.sampleContent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFE2E2E6),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

data class ExportFormatItem(
    val name: String,
    val extension: String,
    val description: String,
    val sampleContent: String,
    val icon: ImageVector
)

/**
 * SECTION 10: USE CASES SECTION
 * Title: "Built for Real Documents."
 * 9 compact cards: Invoices, Receipts, Bank Statements, Forms, Certificates, Mark Sheets, Business Documents, Tables, PDFs.
 */
@Composable
fun UseCasesSection(
    modifier: Modifier = Modifier
) {
    val useCases = listOf(
        UseCaseItem("Invoices", "Commercial & Vendor", Icons.Outlined.ReceiptLong),
        UseCaseItem("Receipts", "Retail & Expenses", Icons.Outlined.Receipt),
        UseCaseItem("Bank Statements", "Accounts & Ledgers", Icons.Outlined.AccountBalance),
        UseCaseItem("Forms", "Applications & KYC", Icons.Outlined.Assignment),
        UseCaseItem("Certificates", "Awards & Credentials", Icons.Outlined.WorkspacePremium),
        UseCaseItem("Mark Sheets", "Academic Transcripts", Icons.Outlined.School),
        UseCaseItem("Business Docs", "Contracts & Quotes", Icons.Outlined.BusinessCenter),
        UseCaseItem("Tables", "Spreadsheet Grids", Icons.Outlined.TableChart),
        UseCaseItem("PDF Documents", "Multi-Page Archives", Icons.Outlined.PictureAsPdf)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Header
        Text(
            text = "SUPPORTED WORKLOADS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SnapDataRed,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Built for ")
                withStyle(SpanStyle(color = SnapDataRed)) {
                    append("Real Documents.")
                }
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SnapDataBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Engineered to handle diverse real-world document variations without manual data entry.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 9 Compact Use Case Cards in 3x3 Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            useCases.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(1.dp, RoundedCornerShape(12.dp), ambientColor = Color(0x06000000))
                                .border(1.dp, LightBorder, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = SnapDataRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SnapDataBlack,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = item.subtitle,
                                    fontSize = 8.5.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class UseCaseItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

/**
 * SECTION 11: FINAL CTA SECTION
 * Dark charcoal premium section, person with laptop illustration, prominent logo,
 * "Turn Every Document Into Data.", Scan it • Understand it • Structure it • Export it, Download & Explore buttons.
 */
@Composable
fun FinalCtaSection(
    onDownloadClick: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0E12))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Prominent Logo
            SnapDataLogo(
                variant = SnapDataLogoVariant.FULL_VERTICAL,
                iconSize = 48.dp,
                wordmarkSize = 24.sp,
                taglineSize = 8.sp,
                isDarkBackground = true,
                showTagline = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Headline
            Text(
                text = buildAnnotatedString {
                    append("Turn Every Document\nInto ")
                    withStyle(SpanStyle(color = SnapDataRed)) {
                        append("Data.")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = CardWhite,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subheading
            Text(
                text = "Scan it. • Understand it. • Structure it. • Export it.",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFA0A0A8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Person with Laptop Illustration
            HeroPersonIllustration(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                height = 120.dp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = onDownloadClick,
                colors = ButtonDefaults.buttonColors(containerColor = SnapDataRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("final_cta_download_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = CardWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Download SnapData",
                    color = CardWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onExploreClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CardWhite),
                border = BorderStroke(1.dp, Color(0xFF33353F)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("final_cta_explore_button")
            ) {
                Text(
                    text = "Explore SnapData →",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CardWhite
                )
            }
        }
    }
}

/**
 * SECTION 12: FOOTER
 * SnapData logo with AI-Powered Document Intelligence, navigation links, and copyright notice.
 */
@Composable
fun LandingFooter(
    onNavigateSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(WarmCreamBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = LightBorder, modifier = Modifier.padding(bottom = 20.dp))

        // SnapData Logo
        SnapDataLogo(
            variant = SnapDataLogoVariant.FULL_HORIZONTAL,
            iconSize = 30.dp,
            wordmarkSize = 18.sp,
            taglineSize = 7.5.sp,
            isDarkBackground = false,
            showTagline = true,
            onClick = { onNavigateSection("hero") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation Links
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Features",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier
                    .clickable { onNavigateSection("features") }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Text("•", fontSize = 10.sp, color = LightBorder)
            Text(
                text = "How It Works",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier
                    .clickable { onNavigateSection("workflow") }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Text("•", fontSize = 10.sp, color = LightBorder)
            Text(
                text = "Privacy",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier
                    .clickable { onNavigateSection("privacy") }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Text("•", fontSize = 10.sp, color = LightBorder)
            Text(
                text = "Export",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                modifier = Modifier
                    .clickable { onNavigateSection("export") }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Copyright Notice
        Text(
            text = "© 2026 SnapData. All rights reserved. On-Device AI Document Intelligence.",
            fontSize = 10.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
