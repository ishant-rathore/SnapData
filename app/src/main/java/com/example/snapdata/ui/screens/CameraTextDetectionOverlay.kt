package com.example.snapdata.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.theme.AccentGreen
import com.example.snapdata.ui.theme.PrimaryBlue
import kotlin.math.roundToInt

/**
 * Model representing a detected text block with normalized relative bounding coordinates (0f..1f).
 */
data class DetectedTextBlock(
    val id: String,
    val text: String,
    val category: String,
    val confidence: Float,
    val relativeLeft: Float,
    val relativeTop: Float,
    val relativeRight: Float,
    val relativeBottom: Float,
    val isKeyData: Boolean = false
)

/**
 * Comprehensive visual overlay that renders real-time detected text blocks,
 * animated scanning lasers, targeting reticles, HUD telemetry, and interactive block inspection.
 */
@Composable
fun CameraTextDetectionOverlay(
    detectedBlocks: List<DetectedTextBlock>,
    isScanningActive: Boolean = true,
    showBoundingBoxes: Boolean = true,
    showLaserSweep: Boolean = true,
    showConfidenceBadges: Boolean = true,
    selectedBlockId: String? = null,
    onBlockSelected: (DetectedTextBlock?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 1. Continuous Laser Sweep Animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser_transition")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_sweep_pos"
    )

    // 2. Pulse Glow for Bounding Boxes
    val boxPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "box_pulse_alpha"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("camera_text_detection_overlay")
    ) {
        val containerWidth = maxWidth
        val containerHeight = maxHeight

        // Canvas for Rendering Vector Laser & Glowing Bounding Paths
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Render Laser Sweep Line with Gradient Trail
            if (isScanningActive && showLaserSweep) {
                val currentLaserY = h * laserProgress

                // Ambient glow band above & below laser
                val laserGlowBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        PrimaryBlue.copy(alpha = 0.05f),
                        PrimaryBlue.copy(alpha = 0.35f),
                        Color(0xFF00F0FF).copy(alpha = 0.85f),
                        PrimaryBlue.copy(alpha = 0.35f),
                        PrimaryBlue.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = currentLaserY - 40f,
                    endY = currentLaserY + 40f
                )

                drawRect(
                    brush = laserGlowBrush,
                    topLeft = Offset(0f, currentLaserY - 40f),
                    size = Size(w, 80f)
                )

                // High-intensity sharp core beam
                drawLine(
                    color = Color(0xFF00F0FF),
                    start = Offset(0f, currentLaserY),
                    end = Offset(w, currentLaserY),
                    strokeWidth = 3f
                )

                // End-point beam indicators
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(12f, currentLaserY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(w - 12f, currentLaserY)
                )
            }

            // Draw bounding boxes for detected blocks
            if (showBoundingBoxes) {
                for (block in detectedBlocks) {
                    val isSelected = block.id == selectedBlockId
                    val left = w * block.relativeLeft
                    val top = h * block.relativeTop
                    val right = w * block.relativeRight
                    val bottom = h * block.relativeBottom
                    val boxW = (right - left).coerceAtLeast(10f)
                    val boxH = (bottom - top).coerceAtLeast(10f)

                    val baseColor = when {
                        isSelected -> Color(0xFFFFB703) // Gold focus
                        block.isKeyData -> Color(0xFF00F0FF) // Cyan for key metadata / totals
                        block.confidence >= 0.95f -> AccentGreen // Emerald for high confidence
                        else -> PrimaryBlue // Standard Primary Blue
                    }

                    val fillAlpha = if (isSelected) 0.28f else if (block.isKeyData) 0.18f else 0.12f
                    val strokeAlpha = if (isSelected) 1f else boxPulseAlpha

                    // 1. Translucent Box Fill
                    drawRoundRect(
                        color = baseColor.copy(alpha = fillAlpha),
                        topLeft = Offset(left, top),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // 2. Glowing Dotted/Solid Border
                    drawRoundRect(
                        color = baseColor.copy(alpha = strokeAlpha),
                        topLeft = Offset(left, top),
                        size = Size(boxW, boxH),
                        cornerRadius = CornerRadius(8f, 8f),
                        style = Stroke(
                            width = if (isSelected) 3.5f else 2f,
                            pathEffect = if (isSelected) null else PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                        )
                    )

                    // 3. Tech Corner Target Brackets
                    drawCornerBrackets(
                        left = left,
                        top = top,
                        right = right,
                        bottom = bottom,
                        color = baseColor,
                        strokeWidth = if (isSelected) 5f else 3.5f,
                        bracketLength = (minOf(boxW, boxH) * 0.25f).coerceIn(10f, 24f)
                    )
                }
            }
        }

        // Overlay Interactive Composable Text Badges & Tags
        if (showBoundingBoxes) {
            for (block in detectedBlocks) {
                val isSelected = block.id == selectedBlockId
                val leftDp = containerWidth * block.relativeLeft
                val topDp = containerHeight * block.relativeTop
                val rightDp = containerWidth * block.relativeRight
                val bottomDp = containerHeight * block.relativeBottom
                val blockWidthDp = (rightDp - leftDp).coerceAtLeast(40.dp)
                val blockHeightDp = (bottomDp - topDp).coerceAtLeast(24.dp)

                val tagColor = when {
                    isSelected -> Color(0xFFFFB703)
                    block.isKeyData -> Color(0xFF00F0FF)
                    block.confidence >= 0.95f -> AccentGreen
                    else -> PrimaryBlue
                }

                // Interactive tap area matching the bounding box
                Box(
                    modifier = Modifier
                        .offset(x = leftDp, y = topDp)
                        .size(width = blockWidthDp, height = blockHeightDp)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (selectedBlockId == block.id) {
                                onBlockSelected(null)
                            } else {
                                onBlockSelected(block)
                            }
                        }
                        .testTag("detected_block_${block.id}")
                )

                // Floating Text & Confidence Badge
                if (showConfidenceBadges) {
                    val badgeYOffset = (topDp - 18.dp).coerceAtLeast(4.dp)
                    val badgeXOffset = leftDp.coerceAtLeast(4.dp)

                    Surface(
                        color = Color(0xDD0A1128),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.8f)),
                        modifier = Modifier
                            .offset(x = badgeXOffset, y = badgeYOffset)
                            .clickable { onBlockSelected(block) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(tagColor)
                            )
                            Text(
                                text = block.text.take(18) + if (block.text.length > 18) "…" else "",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${(block.confidence * 100).roundToInt()}%",
                                color = tagColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Top Scanner Telemetry HUD Banner
        Surface(
            color = Color(0xBB080E1E),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3300F0FF)),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .testTag("scanner_hud_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Live Blinking Indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isScanningActive) AccentGreen else Color.Gray)
                )
                Text(
                    text = if (isScanningActive) "LIVE OCR" else "PAUSED",
                    color = if (isScanningActive) AccentGreen else Color.Gray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "•",
                    color = Color(0x66FFFFFF),
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = null,
                    tint = Color(0xFF00F0FF),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${detectedBlocks.size} Blocks Locked",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                if (detectedBlocks.isNotEmpty()) {
                    val avgConf = (detectedBlocks.map { it.confidence }.average() * 100).roundToInt()
                    Text(
                        text = "•",
                        color = Color(0x66FFFFFF),
                        fontSize = 11.sp
                    )
                    Text(
                        text = "$avgConf% Conf",
                        color = if (avgConf >= 90) AccentGreen else Color(0xFFFFB703),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Selected Text Block Detail Inspector Pill (Appears at bottom of viewfinder when clicked)
        AnimatedVisibility(
            visible = selectedBlockId != null,
            enter = fadeIn() + androidx.compose.animation.slideInVertically { it / 2 },
            exit = fadeOut() + androidx.compose.animation.slideOutVertically { it / 2 },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            val selectedBlock = detectedBlocks.find { it.id == selectedBlockId }
            if (selectedBlock != null) {
                Surface(
                    color = Color(0xF00A1128),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFB703)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .testTag("selected_block_inspector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = Color(0x33FFB703),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = selectedBlock.category.uppercase(),
                                        color = Color(0xFFFFB703),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Confidence: ${(selectedBlock.confidence * 100).roundToInt()}%",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = selectedBlock.text,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = { onBlockSelected(null) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Inspector",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Draws precise high-tech corner brackets around bounding rectangles.
 */
private fun DrawScope.drawCornerBrackets(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    color: Color,
    strokeWidth: Float,
    bracketLength: Float
) {
    // Top-Left Corner
    drawLine(color, Offset(left, top), Offset(left + bracketLength, top), strokeWidth)
    drawLine(color, Offset(left, top), Offset(left, top + bracketLength), strokeWidth)

    // Top-Right Corner
    drawLine(color, Offset(right, top), Offset(right - bracketLength, top), strokeWidth)
    drawLine(color, Offset(right, top), Offset(right, top + bracketLength), strokeWidth)

    // Bottom-Left Corner
    drawLine(color, Offset(left, bottom), Offset(left + bracketLength, bottom), strokeWidth)
    drawLine(color, Offset(left, bottom), Offset(left, bottom - bracketLength), strokeWidth)

    // Bottom-Right Corner
    drawLine(color, Offset(right, bottom), Offset(right - bracketLength, bottom), strokeWidth)
    drawLine(color, Offset(right, bottom), Offset(right, bottom - bracketLength), strokeWidth)
}
