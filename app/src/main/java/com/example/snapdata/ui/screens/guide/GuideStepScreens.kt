package com.example.snapdata.ui.screens.guide

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.components.branding.SnapDataSymbol
import com.example.snapdata.ui.theme.SnapDataRed

/**
 * Visual implementations of the real, interactive UI screens for all 10 steps of the guide.
 */

// Colors matching the visual reference's dark aesthetic
private val GuideBgColor = Color(0xFF0C0D12)
private val CardSurfaceColor = Color(0xFF16171E)
private val CardSurfaceLighter = Color(0xFF1F212A)
private val BorderSubtle = Color(0xFF2B2D3A)
private val TextWhite = Color(0xFFFFFFFF)
private val TextMuted = Color(0xFF9698A6)
private val TextDimmed = Color(0xFF6B6D7C)

// ==========================================
// STEP 01: HOME DASHBOARD
// ==========================================
@Composable
fun GuideScreen01Home(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Bar Mock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SnapDataSymbol(size = 20.dp, variant = SnapDataLogoVariant.WHITE_ON_DARK)
                Spacer(modifier = Modifier.width(6.dp))
                Text("SnapData", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Icon(Icons.Outlined.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            }
        }

        // Greeting
        Column {
            Text("Hello, User", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("What would you like to do today?", color = TextMuted, fontSize = 11.sp)
        }

        // Highlight Target: Scan Document
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(14.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                .clickable { onTargetClick() }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Scan Document", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Upload PDF
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFFF4D4D), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Upload PDF", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        // Upload Image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Image, contentDescription = null, tint = Color(0xFF6C8CFF), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Upload Image", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }

        // Recent Documents
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Documents", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("View all", color = TextMuted, fontSize = 10.sp)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Outlined.Assignment, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Invoice_INV-1024.pdf", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Text("Today, 10:30 AM", color = TextDimmed, fontSize = 9.sp)
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Mini Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text("Home", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Folder, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(18.dp))
                Text("History", color = TextDimmed, fontSize = 9.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(18.dp))
                Text("Export", color = TextDimmed, fontSize = 9.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Settings, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(18.dp))
                Text("Settings", color = TextDimmed, fontSize = 9.sp)
            }
        }
    }
}

// ==========================================
// STEP 02: DOCUMENT INPUT
// ==========================================
@Composable
fun GuideScreen02Input(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Bar Mock
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Input Source", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Option 1: Camera Scan
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Camera Scan", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Capture with camera", color = TextMuted, fontSize = 11.sp)
            }
        }

        // Option 2 (Target): Upload PDF
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                .clickable { onTargetClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A1517)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFFF4D4D), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Upload PDF", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Import PDF documents", color = TextMuted, fontSize = 11.sp)
            }
        }

        // Option 3: Upload Image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Image, contentDescription = null, tint = TextWhite, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Upload Image", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("Import from gallery", color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}

// ==========================================
// STEP 03: CAMERA SCANNER
// ==========================================
@Composable
fun GuideScreen03Camera(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF08090D))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Controls Mock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Close, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
            Icon(Icons.Default.FlashOn, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
            Icon(Icons.Default.GridView, contentDescription = null, tint = TextWhite, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scanner Viewfinder Area with Mock Document
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF14161F))
                .border(1.dp, Color(0xFF323545), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Viewfinder Corner Brackets
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val bracketLen = 24.dp.toPx()
                val strokeW = 2.5.dp.toPx()
                // Top-Left
                drawLine(Color.White, Offset(0f, 0f), Offset(bracketLen, 0f), strokeW)
                drawLine(Color.White, Offset(0f, 0f), Offset(0f, bracketLen), strokeW)
                // Top-Right
                drawLine(Color.White, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), strokeW)
                drawLine(Color.White, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeW)
                // Bottom-Left
                drawLine(Color.White, Offset(0f, size.height), Offset(bracketLen, size.height), strokeW)
                drawLine(Color.White, Offset(0f, size.height), Offset(0f, size.height - bracketLen), strokeW)
                // Bottom-Right
                drawLine(Color.White, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), strokeW)
                drawLine(Color.White, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), strokeW)
            }

            // Document Preview Mock Inside
            Column(
                modifier = Modifier
                    .fillMaxSize(0.78f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFECEEF2))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("INVOICE", color = Color(0xFF1A1A1A), fontSize = 10.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(2.dp))
                repeat(4) {
                    Box(modifier = Modifier.fillMaxWidth(if (it == 3) 0.5f else 0.9f).height(3.dp).background(Color(0xFFC0C4D0)))
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Mini table grid
                repeat(4) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        repeat(4) {
                            Box(modifier = Modifier.size(width = 24.dp, height = 3.dp).background(Color(0xFFB0B6C4)))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Scanner Bottom Bar with Capture Button Target
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Crop, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Auto Crop", color = TextDimmed, fontSize = 8.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Transform, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Perspective", color = TextDimmed, fontSize = 8.sp)
            }

            // Target: Capture Button
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        onTargetPositioned(coordinates.boundsInParent())
                    }
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(3.dp, Color(0xFF888A96), CircleShape)
                    .clickable { onTargetClick() },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Multi-page", color = TextDimmed, fontSize = 8.sp)
            }
        }
    }
}

// ==========================================
// STEP 04: IMAGE ENHANCEMENT
// ==========================================
@Composable
fun GuideScreen04Enhancement(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Enhance Image", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clean Enhanced Document Mockup
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFAFAFA))
                .border(1.dp, Color(0xFF44475A), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("INVOICE", color = Color(0xFF1A1A1A), fontSize = 11.sp, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(4.dp))
                repeat(4) {
                    Box(modifier = Modifier.fillMaxWidth(if (it == 3) 0.6f else 0.95f).height(3.5.dp).background(Color(0xFFB4B9C8)))
                }
                Spacer(modifier = Modifier.height(6.dp))
                repeat(5) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        repeat(4) {
                            Box(modifier = Modifier.size(width = 30.dp, height = 3.5.dp).background(Color(0xFFA0A6B8)))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Target: Enhancement Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                .clickable { onTargetClick() }
                .padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Crop, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Text("Auto Crop", color = Color.White, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Transform, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Perspective", color = TextMuted, fontSize = 8.5.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.AutoFixHigh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Noise Removal", color = TextMuted, fontSize = 8.5.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Brightness6, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Brightness", color = TextMuted, fontSize = 8.5.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.RotateRight, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
                Text("Auto Rotate", color = TextMuted, fontSize = 8.5.sp)
            }
        }
    }
}

// ==========================================
// STEP 05: OCR PROCESSING
// ==========================================
@Composable
fun GuideScreen05Ocr(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_sweep")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser_pos"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("OCR Processing", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // Center Document with Scanning Laser
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF14161F))
                .border(1.dp, Color(0xFF383B4C), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Document Graphic
            Column(
                modifier = Modifier
                    .fillMaxSize(0.85f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF1F222E))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("INVOICE", color = Color(0xFF4EE482), fontSize = 11.sp, fontWeight = FontWeight.Black)
                repeat(4) {
                    Box(modifier = Modifier.fillMaxWidth(if (it == 3) 0.5f else 0.9f).height(3.5.dp).background(Color(0xFF3C4054)))
                }
                Spacer(modifier = Modifier.height(4.dp))
                repeat(4) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        repeat(4) {
                            Box(modifier = Modifier.size(width = 24.dp, height = 3.5.dp).background(Color(0xFF343848)))
                        }
                    }
                }
            }

            // Green Laser Sweep Line
            Canvas(modifier = Modifier.fillMaxSize()) {
                val y = size.height * laserOffset
                drawLine(
                    color = Color(0xFF00FF7F),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }

        // Target: OCR Status & Progress Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                .clickable { onTargetClick() }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recognizing Text...", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("72%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { 0.72f },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF00FF7F),
                trackColor = Color(0xFF282B38)
            )
            Text("Converting image to editable text", color = TextMuted, fontSize = 10.sp)
        }
    }
}

// ==========================================
// STEP 06: AI DOCUMENT INTELLIGENCE
// ==========================================
@Composable
fun GuideScreen06Ai(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("AI Analysis", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = Color(0xFFB388FF), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Analyzing Document...", color = Color(0xFFB388FF), fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
        }

        // Target: AI Intelligence Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(14.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                .clickable { onTargetClick() }
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. Document Type
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Description, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Document Type", color = TextMuted, fontSize = 10.sp)
                    Text("Invoice", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(color = BorderSubtle)

            // 2. Key-Value Extraction
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.DataObject, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Key-Value Extraction", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Extracting important fields", color = TextMuted, fontSize = 10.sp)
                }
            }

            HorizontalDivider(color = BorderSubtle)

            // 3. Smart Table Detection
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.TableChart, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Smart Table Detection", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Detecting tables", color = TextMuted, fontSize = 10.sp)
                }
            }

            HorizontalDivider(color = BorderSubtle)

            // 4. AI Summary
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("AI Summary", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("Generating summary", color = TextMuted, fontSize = 10.sp)
                }
            }

            HorizontalDivider(color = BorderSubtle)

            // 5. Confidence Score
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Confidence Score", color = TextMuted, fontSize = 10.sp)
                    Text("94% High Confidence", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// STEP 07: STRUCTURED DATA
// ==========================================
@Composable
fun GuideScreen07Structured(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Extracted Data", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }

        Text("Document Type: Invoice", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)

        // Target: Structured Fields Card & Table
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(14.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                .clickable { onTargetClick() }
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Field 1
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Invoice Number", color = TextMuted, fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("INV-1024", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                }
            }
            // Field 2
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Customer", color = TextMuted, fontSize = 11.sp)
                Text("ABC Pvt Ltd", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            // Field 3
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Date", color = TextMuted, fontSize = 11.sp)
                Text("01/05/2024", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            // Field 4
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Amount", color = TextMuted, fontSize = 11.sp)
                Text("₹ 12,450.00", color = Color(0xFF00FF7F), fontSize = 12.sp, fontWeight = FontWeight.Black)
            }

            HorizontalDivider(color = BorderSubtle)

            Text("Items", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            // Mini Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF222430))
                    .padding(vertical = 4.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Item", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
                Text("Qty", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
                Text("Rate", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.8f))
                Text("Amount", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
            }

            // Table Rows
            listOf(
                Triple("Product A", "2", "1500") to "3000",
                Triple("Product B", "3", "1200") to "3600",
                Triple("Product C", "2", "1800") to "3600"
            ).forEach { (left, total) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(left.first, color = TextWhite, fontSize = 9.sp, modifier = Modifier.weight(1.2f))
                    Text(left.second, color = TextWhite, fontSize = 9.sp, modifier = Modifier.weight(0.6f))
                    Text(left.third, color = TextWhite, fontSize = 9.sp, modifier = Modifier.weight(0.8f))
                    Text(total, color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f))
                }
            }
        }
    }
}

// ==========================================
// STEP 08: REVIEW & EDIT
// ==========================================
@Composable
fun GuideScreen08Review(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Edit Data", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // Tabs (Target: Edit Text tab)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CardSurfaceColor)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        onTargetPositioned(coordinates.boundsInParent())
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable { onTargetClick() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Edit Text", color = Color(0xFF0C0D12), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Edit Table", color = TextMuted, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Action Buttons Mock
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Add Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Add Row", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Delete Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = Color(0xFFFF6B6B), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Delete Row", color = Color(0xFFFF6B6B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Validate Data
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Validate Data", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            // Save Changes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Save, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Save Changes", color = Color(0xFF00E676), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// STEP 09: EXPORT
// ==========================================
@Composable
fun GuideScreen09Export(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Export Document", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // Format Grid 2x2
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // Target: Excel Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .onGloballyPositioned { coordinates ->
                        onTargetPositioned(coordinates.boundsInParent())
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurfaceColor)
                    .border(1.5.dp, Color.White, RoundedCornerShape(14.dp))
                    .clickable { onTargetClick() }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF107C41)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("X", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Excel (.xlsx)", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }

            // CSV Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CSV", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("CSV (.csv)", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // JSON Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF6A1B9A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("{ }", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("JSON (.json)", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                }
            }

            // PDF Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFC62828)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("PDF", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PDF (.pdf)", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Share Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Share, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// STEP 10: DOCUMENT HISTORY
// ==========================================
@Composable
fun GuideScreen10History(
    onTargetPositioned: (Rect) -> Unit,
    onTargetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App Bar
        Text("History", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        // Target: Search Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    onTargetPositioned(coordinates.boundsInParent())
                }
                .clip(RoundedCornerShape(12.dp))
                .background(CardSurfaceColor)
                .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                .clickable { onTargetClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Search documents", color = TextMuted, fontSize = 12.sp)
            Icon(Icons.Outlined.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
        }

        // Filter Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All" to true, "Invoices" to false, "Receipts" to false, "Forms" to false).forEach { (title, isSelected) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) Color.White else CardSurfaceColor)
                        .border(1.dp, if (isSelected) Color.White else BorderSubtle, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // History Items List
        listOf(
            "Invoice_INV-1024.pdf" to "Today, 10:30 AM",
            "Receipt_RCP-889.pdf" to "Yesterday, 04:15 PM",
            "Statement_May.pdf" to "12 May 2024, 09:20 AM",
            "Form_Application.pdf" to "10 May 2024, 11:10 AM"
        ).forEach { (title, date) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurfaceColor)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = TextWhite, fontSize = 10.5.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(date, color = TextDimmed, fontSize = 8.5.sp)
                }
                Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextDimmed, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Actions Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(CardSurfaceColor)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Visibility, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                Text("View", color = TextWhite, fontSize = 8.5.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Replay, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                Text("Reopen", color = TextWhite, fontSize = 8.5.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Delete, contentDescription = null, tint = TextWhite, modifier = Modifier.size(16.dp))
                Text("Delete", color = TextWhite, fontSize = 8.5.sp)
            }
        }
    }
}

// ==========================================
// COMPLETION SCREEN: YOU'RE READY
// ==========================================
@Composable
fun GuideScreenCompletion(
    onStartApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "completion_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GuideBgColor)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(0.8f))

        // Large Glowing Checkmark
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color(0xFF161820))
                .border(2.5.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Ready",
                tint = Color.White,
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Main Title
        Text(
            text = "YOU'RE READY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
            text = "Scan. Extract. Edit. Export.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4D6E0),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Description
        Text(
            text = "You have learned the essentials.\nStart processing your documents with SnapData.",
            fontSize = 13.sp,
            color = Color(0xFFA0A2B0),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // Start Using SnapData Button
        Button(
            onClick = onStartApp,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("guide_finish_start_app_btn"),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = "START USING SNAPDATA",
                color = Color.Black,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
