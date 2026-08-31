package com.example.snapdata.ui.illustrations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.snapdata.ui.theme.SnapDataRed

/**
 * SnapData Minimalist Editorial Illustration System
 *
 * Design Guidelines:
 * - Dominant visual: Black line art (Stroke: 1.5dp to 2.5dp with Round Cap & Join).
 * - Background & fills: Warm cream (#FBF9F5, #F5F1E8, #ECE6DA) and Card White.
 * - Accents: Pure SnapData Red (#E11D48) for focus dots, laser sweeps, and key nodes.
 * - Negative space: Generous breathing room, clean editorial layout, zero 3D/glossy slop.
 */

object SnapDataIllustrationColors {
    val StrokeBlack = Color(0xFF141414)
    val StrokeCharcoal = Color(0xFF262626)
    val PaperWhite = Color(0xFFFFFFFF)
    val PaperTint = Color(0xFFFBF9F5)
    val PaperShadow = Color(0xFFEFECE4)
    val LineSubtle = Color(0xFF737373)
    val LineMuted = Color(0xFFA3A3A3)
    val AccentRed = SnapDataRed
    val AccentRedLight = Color(0xFFFDE8EC)
}

/**
 * Helper to draw a hand-drawn-style sparkle/star with thin black lines and center dot.
 */
fun DrawScope.drawEditorialSparkle(
    center: Offset,
    radius: Float,
    strokeWidth: Float = 2f,
    accentRed: Boolean = false
) {
    val color = if (accentRed) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack
    // 4-point star path
    val path = Path().apply {
        moveTo(center.x, center.y - radius)
        quadraticTo(center.x, center.y, center.x + radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y + radius)
        quadraticTo(center.x, center.y, center.x - radius, center.y)
        quadraticTo(center.x, center.y, center.x, center.y - radius)
        close()
    }
    drawPath(path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
    drawCircle(
        color = if (accentRed) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack,
        radius = radius * 0.22f,
        center = center
    )
}

/**
 * Helper to draw an editorial paper sheet with folded top-right corner.
 */
fun DrawScope.drawEditorialDocument(
    rect: Rect,
    cornerFoldSize: Float = 22f,
    strokeWidth: Float = 3f,
    fillColor: Color = SnapDataIllustrationColors.PaperWhite,
    strokeColor: Color = SnapDataIllustrationColors.StrokeBlack,
    showLines: Boolean = true,
    accentRedFold: Boolean = false
) {
    val path = Path().apply {
        moveTo(rect.left, rect.top)
        lineTo(rect.right - cornerFoldSize, rect.top)
        lineTo(rect.right, rect.top + cornerFoldSize)
        lineTo(rect.right, rect.bottom)
        lineTo(rect.left, rect.bottom)
        close()
    }

    // Shadow / Fill
    drawPath(path, color = fillColor, style = Fill)
    // Outline
    drawPath(path, color = strokeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Folded corner flap
    val foldPath = Path().apply {
        moveTo(rect.right - cornerFoldSize, rect.top)
        lineTo(rect.right - cornerFoldSize, rect.top + cornerFoldSize)
        lineTo(rect.right, rect.top + cornerFoldSize)
    }
    if (accentRedFold) {
        val foldFillPath = Path().apply {
            moveTo(rect.right - cornerFoldSize, rect.top)
            lineTo(rect.right - cornerFoldSize, rect.top + cornerFoldSize)
            lineTo(rect.right, rect.top + cornerFoldSize)
            close()
        }
        drawPath(foldFillPath, color = SnapDataIllustrationColors.AccentRedLight, style = Fill)
    }
    drawPath(foldPath, color = if (accentRedFold) SnapDataIllustrationColors.AccentRed else strokeColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Inner text lines
    if (showLines) {
        val lineMarginX = rect.width * 0.16f
        val startY = rect.top + cornerFoldSize + 14f
        val lineSpacing = (rect.bottom - startY - 14f) / 4f

        for (i in 0 until 3) {
            val y = startY + (i * lineSpacing)
            val lineWidth = if (i == 0) rect.width * 0.45f else if (i == 2) rect.width * 0.55f else rect.width * 0.68f
            drawLine(
                color = SnapDataIllustrationColors.LineMuted,
                start = Offset(rect.left + lineMarginX, y),
                end = Offset(rect.left + lineMarginX + lineWidth, y),
                strokeWidth = strokeWidth * 0.65f,
                cap = StrokeCap.Round
            )
        }
    }
}

/**
 * 1. HOME HERO ILLUSTRATION
 * Minimalist editorial scene:
 * A stylized human hand presenting a physical document into a clean scanner beam,
 * converting into floating structured data badges and red AI focus nodes.
 */
@Composable
fun HomeHeroIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val laserYOffset by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_y"
    )
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        // Background decorative organic rounded blob
        val bgBlobPath = Path().apply {
            moveTo(w * 0.15f, h * 0.4f)
            cubicTo(w * 0.2f, h * 0.05f, w * 0.75f, h * 0.02f, w * 0.88f, h * 0.35f)
            cubicTo(w * 0.98f, h * 0.65f, w * 0.8f, h * 0.95f, w * 0.5f, h * 0.92f)
            cubicTo(w * 0.25f, h * 0.90f, w * 0.08f, h * 0.75f, w * 0.15f, h * 0.4f)
            close()
        }
        drawPath(bgBlobPath, color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f))

        // Center Physical Document
        val docLeft = w * 0.28f
        val docTop = h * 0.12f + floatOffset
        val docWidth = w * 0.34f
        val docHeight = h * 0.74f
        val docRect = Rect(docLeft, docTop, docLeft + docWidth, docTop + docHeight)

        // Drop shadow for document
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperShadow,
            topLeft = Offset(docLeft + 4.dp.toPx(), docTop + 4.dp.toPx()),
            size = Size(docWidth, docHeight),
            cornerRadius = CornerRadius(4.dp.toPx())
        )

        // Document
        drawEditorialDocument(
            rect = docRect,
            cornerFoldSize = docWidth * 0.28f,
            strokeWidth = strokeW,
            showLines = true,
            accentRedFold = true
        )

        // Subtle table grid inside document
        val tableTop = docTop + docHeight * 0.52f
        val tableLeft = docLeft + docWidth * 0.14f
        val tableW = docWidth * 0.72f
        val tableH = docHeight * 0.32f

        drawRoundRect(
            color = SnapDataIllustrationColors.PaperTint,
            topLeft = Offset(tableLeft, tableTop),
            size = Size(tableW, tableH),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(tableLeft, tableTop),
            size = Size(tableW, tableH),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Stroke(width = strokeW * 0.65f)
        )
        // Table horizontal divider
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(tableLeft, tableTop + tableH * 0.45f),
            end = Offset(tableLeft + tableW, tableTop + tableH * 0.45f),
            strokeWidth = strokeW * 0.5f
        )
        // Table vertical divider
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(tableLeft + tableW * 0.5f, tableTop),
            end = Offset(tableLeft + tableW * 0.5f, tableTop + tableH),
            strokeWidth = strokeW * 0.5f
        )

        // Red Laser Scanner Beam over document
        val scanY = docTop + (docHeight * laserYOffset)
        drawLine(
            color = SnapDataIllustrationColors.AccentRed,
            start = Offset(docLeft - 10.dp.toPx(), scanY),
            end = Offset(docLeft + docWidth + 10.dp.toPx(), scanY),
            strokeWidth = strokeW * 0.9f,
            cap = StrokeCap.Round
        )
        // Red laser endpoints
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 3.dp.toPx(), center = Offset(docLeft - 10.dp.toPx(), scanY))
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 3.dp.toPx(), center = Offset(docLeft + docWidth + 10.dp.toPx(), scanY))

        // Left Side: Stylized Hand holding the document
        val handPath = Path().apply {
            moveTo(0f, h * 0.78f)
            cubicTo(w * 0.10f, h * 0.72f, w * 0.16f, h * 0.65f, w * 0.24f, h * 0.60f)
            // Thumb gripping corner
            quadraticTo(w * 0.29f, h * 0.57f, docLeft + 6.dp.toPx(), docTop + docHeight * 0.65f)
            quadraticTo(w * 0.28f, h * 0.68f, w * 0.22f, h * 0.72f)
            cubicTo(w * 0.18f, h * 0.85f, w * 0.08f, h * 0.98f, 0f, h)
        }
        drawPath(handPath, color = SnapDataIllustrationColors.PaperWhite, style = Fill)
        drawPath(handPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Right Side: Floating Extracted Key-Value Badges (Editorial cards)
        val badge1Left = w * 0.66f
        val badge1Top = h * 0.18f - floatOffset
        val badge1W = w * 0.28f
        val badge1H = h * 0.28f

        // Badge 1 Card (Total / JSON)
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(badge1Left, badge1Top),
            size = Size(badge1W, badge1H),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(badge1Left, badge1Top),
            size = Size(badge1W, badge1H),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(width = strokeW)
        )
        // Red accent dot on badge 1
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 3.dp.toPx(), center = Offset(badge1Left + 10.dp.toPx(), badge1Top + badge1H * 0.5f))
        // Simulated text lines in badge 1
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(badge1Left + 18.dp.toPx(), badge1Top + badge1H * 0.4f),
            end = Offset(badge1Left + badge1W - 8.dp.toPx(), badge1Top + badge1H * 0.4f),
            strokeWidth = strokeW * 0.7f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = SnapDataIllustrationColors.LineMuted,
            start = Offset(badge1Left + 18.dp.toPx(), badge1Top + badge1H * 0.7f),
            end = Offset(badge1Left + badge1W - 14.dp.toPx(), badge1Top + badge1H * 0.7f),
            strokeWidth = strokeW * 0.5f,
            cap = StrokeCap.Round
        )

        // Badge 2 Card (Confidence 99% / Checkmark)
        val badge2Left = w * 0.62f
        val badge2Top = h * 0.55f + floatOffset
        val badge2W = w * 0.32f
        val badge2H = h * 0.28f

        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(badge2Left, badge2Top),
            size = Size(badge2W, badge2H),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(badge2Left, badge2Top),
            size = Size(badge2W, badge2H),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(width = strokeW)
        )
        // Checkmark in badge 2
        val checkPath = Path().apply {
            moveTo(badge2Left + 8.dp.toPx(), badge2Top + badge2H * 0.5f)
            lineTo(badge2Left + 12.dp.toPx(), badge2Top + badge2H * 0.68f)
            lineTo(badge2Left + 18.dp.toPx(), badge2Top + badge2H * 0.32f)
        }
        drawPath(checkPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(width = strokeW * 0.8f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Badge 2 text lines
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(badge2Left + 24.dp.toPx(), badge2Top + badge2H * 0.5f),
            end = Offset(badge2Left + badge2W - 8.dp.toPx(), badge2Top + badge2H * 0.5f),
            strokeWidth = strokeW * 0.7f,
            cap = StrokeCap.Round
        )

        // Connecting dashed editorial arrows
        val arrowPath = Path().apply {
            moveTo(docLeft + docWidth, docTop + docHeight * 0.35f)
            quadraticTo(w * 0.64f, h * 0.32f, badge1Left - 4.dp.toPx(), badge1Top + badge1H * 0.5f)
        }
        drawPath(
            arrowPath,
            color = SnapDataIllustrationColors.LineSubtle,
            style = Stroke(width = strokeW * 0.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f))
        )

        // Sparkles in corners
        drawEditorialSparkle(center = Offset(w * 0.18f, h * 0.22f), radius = 8.dp.toPx(), strokeWidth = strokeW * 0.7f)
        drawEditorialSparkle(center = Offset(w * 0.92f, h * 0.15f), radius = 9.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
        drawEditorialSparkle(center = Offset(w * 0.88f, h * 0.88f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.6f)
    }
}

/**
 * 2. PROCESSING PIPELINE ILLUSTRATION
 * Editorial Pipeline Flow: (1) Document -> (2) Scan Frame -> (3) OCR Text -> (4) Red AI Sparkle -> (5) Structured Table/JSON
 */
@Composable
fun ProcessingPipelineIllustration(
    modifier: Modifier = Modifier,
    currentStepIndex: Int = 1,
    height: Dp = 110.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pipe_anim")
    val pulseProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()
        val centerY = h * 0.5f

        val nodeCount = 4
        val stepSpacing = w / (nodeCount + 0.5f)
        val nodeRadius = 18.dp.toPx()

        // Background connecting track line
        val lineStart = Offset(stepSpacing * 0.8f, centerY)
        val lineEnd = Offset(w - stepSpacing * 0.8f, centerY)

        drawLine(
            color = SnapDataIllustrationColors.PaperShadow,
            start = lineStart,
            end = lineEnd,
            strokeWidth = strokeW * 1.6f,
            cap = StrokeCap.Round
        )

        // Active animated pulse line
        val activeLineEnd = Offset(
            lineStart.x + (lineEnd.x - lineStart.x) * ((currentStepIndex.coerceIn(0, 3) + pulseProgress * 0.4f) / 3f).coerceIn(0f, 1f),
            centerY
        )
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = lineStart,
            end = activeLineEnd,
            strokeWidth = strokeW * 1.2f,
            cap = StrokeCap.Round
        )

        // Draw 4 Pipeline Node Circles & Icons
        for (i in 0 until nodeCount) {
            val nodeCenter = Offset(stepSpacing * (i + 0.8f), centerY)
            val isCurrentOrPast = i <= currentStepIndex
            val isCurrent = i == currentStepIndex

            // Node Outer Circle
            drawCircle(
                color = if (isCurrent) SnapDataIllustrationColors.PaperWhite else if (isCurrentOrPast) SnapDataIllustrationColors.PaperTint else SnapDataIllustrationColors.PaperShadow,
                radius = nodeRadius,
                center = nodeCenter,
                style = Fill
            )
            drawCircle(
                color = if (isCurrent) SnapDataIllustrationColors.AccentRed else if (isCurrentOrPast) SnapDataIllustrationColors.StrokeBlack else SnapDataIllustrationColors.LineMuted,
                radius = nodeRadius,
                center = nodeCenter,
                style = Stroke(width = if (isCurrent) strokeW * 1.3f else strokeW)
            )

            // Step specific icon drawing
            when (i) {
                0 -> {
                    // Step 1: Raw Document
                    val docRect = Rect(nodeCenter.x - 7.dp.toPx(), nodeCenter.y - 9.dp.toPx(), nodeCenter.x + 7.dp.toPx(), nodeCenter.y + 9.dp.toPx())
                    drawEditorialDocument(
                        rect = docRect,
                        cornerFoldSize = 4.dp.toPx(),
                        strokeWidth = strokeW * 0.7f,
                        strokeColor = if (isCurrentOrPast) SnapDataIllustrationColors.StrokeBlack else SnapDataIllustrationColors.LineMuted,
                        showLines = false
                    )
                }
                1 -> {
                    // Step 2: Scanner Reticle & Laser
                    val reticleSize = 10.dp.toPx()
                    val rLeft = nodeCenter.x - reticleSize
                    val rTop = nodeCenter.y - reticleSize
                    val rRight = nodeCenter.x + reticleSize
                    val rBottom = nodeCenter.y + reticleSize

                    // Reticle corners
                    drawLine(if (isCurrent) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack, Offset(rLeft, rTop), Offset(rLeft + 5.dp.toPx(), rTop), strokeW * 0.8f)
                    drawLine(if (isCurrent) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack, Offset(rLeft, rTop), Offset(rLeft, rTop + 5.dp.toPx()), strokeW * 0.8f)
                    drawLine(if (isCurrent) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack, Offset(rRight, rBottom), Offset(rRight - 5.dp.toPx(), rBottom), strokeW * 0.8f)
                    drawLine(if (isCurrent) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack, Offset(rRight, rBottom), Offset(rRight, rBottom - 5.dp.toPx()), strokeW * 0.8f)

                    // Laser beam
                    drawLine(SnapDataIllustrationColors.AccentRed, Offset(rLeft - 2.dp.toPx(), nodeCenter.y), Offset(rRight + 2.dp.toPx(), nodeCenter.y), strokeW * 0.9f)
                }
                2 -> {
                    // Step 3: Multimodal AI Sparkle
                    drawEditorialSparkle(
                        center = nodeCenter,
                        radius = 8.dp.toPx(),
                        strokeWidth = strokeW * 0.8f,
                        accentRed = isCurrent
                    )
                }
                3 -> {
                    // Step 4: Structured Data Table Grid
                    val tableRect = Rect(nodeCenter.x - 8.dp.toPx(), nodeCenter.y - 7.dp.toPx(), nodeCenter.x + 8.dp.toPx(), nodeCenter.y + 7.dp.toPx())
                    drawRoundRect(
                        color = if (isCurrentOrPast) SnapDataIllustrationColors.StrokeBlack else SnapDataIllustrationColors.LineMuted,
                        topLeft = Offset(tableRect.left, tableRect.top),
                        size = Size(tableRect.width, tableRect.height),
                        cornerRadius = CornerRadius(2.dp.toPx()),
                        style = Stroke(width = strokeW * 0.7f)
                    )
                    // Grid lines
                    drawLine(if (isCurrentOrPast) SnapDataIllustrationColors.StrokeBlack else SnapDataIllustrationColors.LineMuted, Offset(tableRect.left, nodeCenter.y), Offset(tableRect.right, nodeCenter.y), strokeW * 0.5f)
                    drawLine(if (isCurrentOrPast) SnapDataIllustrationColors.StrokeBlack else SnapDataIllustrationColors.LineMuted, Offset(nodeCenter.x, tableRect.top), Offset(nodeCenter.x, tableRect.bottom), strokeW * 0.5f)
                }
            }
        }
    }
}

/**
 * 3. EMPTY STATE ARCHIVE ILLUSTRATION
 * Friendly minimalist editorial illustration for "No Documents Saved":
 * A clean archive box/folder with document sticking out, magnifying glass, red accent fold.
 */
@Composable
fun EmptyArchiveIllustration(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 140.dp
) {
    Canvas(
        modifier = modifier
            .size(sizeDp)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        // Background soft warm circle
        drawCircle(
            color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f),
            radius = w * 0.44f,
            center = Offset(w * 0.5f, h * 0.52f)
        )

        // Peeking Document Behind Folder
        val docRect = Rect(w * 0.35f, h * 0.16f, w * 0.65f, h * 0.58f)
        drawEditorialDocument(
            rect = docRect,
            cornerFoldSize = 14.dp.toPx(),
            strokeWidth = strokeW,
            showLines = true,
            accentRedFold = true
        )

        // Archive Box / Folder Front
        val boxPath = Path().apply {
            moveTo(w * 0.18f, h * 0.48f)
            lineTo(w * 0.40f, h * 0.48f)
            lineTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.82f, h * 0.56f)
            lineTo(w * 0.82f, h * 0.86f)
            lineTo(w * 0.18f, h * 0.86f)
            close()
        }
        drawPath(boxPath, color = SnapDataIllustrationColors.PaperWhite, style = Fill)
        drawPath(boxPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Archive folder front pocket detail line
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(w * 0.26f, h * 0.68f),
            end = Offset(w * 0.74f, h * 0.68f),
            strokeWidth = strokeW * 0.7f,
            cap = StrokeCap.Round
        )

        // Red Accent Label on Archive Box
        val tagRect = Rect(w * 0.38f, h * 0.74f, w * 0.62f, h * 0.80f)
        drawRoundRect(
            color = SnapDataIllustrationColors.AccentRedLight,
            topLeft = Offset(tagRect.left, tagRect.top),
            size = Size(tagRect.width, tagRect.height),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.AccentRed,
            topLeft = Offset(tagRect.left, tagRect.top),
            size = Size(tagRect.width, tagRect.height),
            cornerRadius = CornerRadius(3.dp.toPx()),
            style = Stroke(width = strokeW * 0.65f)
        )

        // Minimalist Magnifying Glass over the box
        val magCenter = Offset(w * 0.72f, h * 0.42f)
        val magRadius = 14.dp.toPx()

        drawCircle(
            color = SnapDataIllustrationColors.PaperWhite,
            radius = magRadius,
            center = magCenter,
            style = Fill
        )
        drawCircle(
            color = SnapDataIllustrationColors.StrokeBlack,
            radius = magRadius,
            center = magCenter,
            style = Stroke(width = strokeW)
        )

        // Magnifying glass handle
        val handleStart = Offset(magCenter.x + magRadius * 0.7f, magCenter.y + magRadius * 0.7f)
        val handleEnd = Offset(handleStart.x + 12.dp.toPx(), handleStart.y + 12.dp.toPx())
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = handleStart,
            end = handleEnd,
            strokeWidth = strokeW * 1.4f,
            cap = StrokeCap.Round
        )

        // Sparkle near top left
        drawEditorialSparkle(center = Offset(w * 0.22f, h * 0.28f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f)
        drawEditorialSparkle(center = Offset(w * 0.82f, h * 0.22f), radius = 6.dp.toPx(), strokeWidth = strokeW * 0.6f, accentRed = true)
    }
}

/**
 * 4. STRUCTURED DATA / TABLE EDITOR ILLUSTRATION
 * Minimalist illustration showing data grid, column alignment, quill/pencil, and verified red cell highlight.
 */
@Composable
fun TableDataEditorIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Background subtle capsule
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.08f, h * 0.10f),
            size = Size(w * 0.84f, h * 0.80f),
            cornerRadius = CornerRadius(12.dp.toPx())
        )

        // Table Spreadsheet Card
        val tableLeft = w * 0.15f
        val tableTop = h * 0.18f
        val tableW = w * 0.52f
        val tableH = h * 0.64f

        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(tableLeft, tableTop),
            size = Size(tableW, tableH),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(tableLeft, tableTop),
            size = Size(tableW, tableH),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(width = strokeW)
        )

        // Header row shading
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperShadow,
            topLeft = Offset(tableLeft, tableTop),
            size = Size(tableW, tableH * 0.32f),
            cornerRadius = CornerRadius(5.dp.toPx()),
            style = Fill
        )

        // Horizontal grid rows
        val rowH = tableH / 3f
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(tableLeft, tableTop + rowH), Offset(tableLeft + tableW, tableTop + rowH), strokeW * 0.7f)
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(tableLeft, tableTop + rowH * 2), Offset(tableLeft + tableW, tableTop + rowH * 2), strokeW * 0.7f)

        // Vertical grid column
        val colW = tableW * 0.45f
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(tableLeft + colW, tableTop), Offset(tableLeft + colW, tableTop + tableH), strokeW * 0.7f)

        // Red Accent Cell Highlight (Row 2, Col 2)
        val redCellRect = Rect(tableLeft + colW + 2.dp.toPx(), tableTop + rowH + 2.dp.toPx(), tableLeft + tableW - 2.dp.toPx(), tableTop + rowH * 2 - 2.dp.toPx())
        drawRoundRect(
            color = SnapDataIllustrationColors.AccentRedLight,
            topLeft = Offset(redCellRect.left, redCellRect.top),
            size = Size(redCellRect.width, redCellRect.height),
            cornerRadius = CornerRadius(2.dp.toPx()),
            style = Fill
        )
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 2.5.dp.toPx(), center = Offset(redCellRect.left + 6.dp.toPx(), redCellRect.top + redCellRect.height * 0.5f))

        // Side: Key-Value Tags Stack (JSON / Extraction items)
        val kvLeft = w * 0.72f
        val kvW = w * 0.20f
        for (i in 0 until 3) {
            val kvTop = h * 0.22f + (i * h * 0.22f)
            val kvH = h * 0.16f
            drawRoundRect(
                color = SnapDataIllustrationColors.PaperWhite,
                topLeft = Offset(kvLeft, kvTop),
                size = Size(kvW, kvH),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Fill
            )
            drawRoundRect(
                color = SnapDataIllustrationColors.StrokeBlack,
                topLeft = Offset(kvLeft, kvTop),
                size = Size(kvW, kvH),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = strokeW * 0.7f)
            )
            drawLine(
                color = if (i == 1) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack,
                start = Offset(kvLeft + 4.dp.toPx(), kvTop + kvH * 0.5f),
                end = Offset(kvLeft + kvW - 4.dp.toPx(), kvTop + kvH * 0.5f),
                strokeWidth = strokeW * 0.6f,
                cap = StrokeCap.Round
            )
        }

        // Connecting arrow from table to KV
        val arrowPath = Path().apply {
            moveTo(tableLeft + tableW, tableTop + tableH * 0.5f)
            lineTo(kvLeft - 4.dp.toPx(), tableTop + tableH * 0.5f)
        }
        drawPath(arrowPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(width = strokeW * 0.8f, cap = StrokeCap.Round))
        drawEditorialSparkle(center = Offset(w * 0.94f, h * 0.20f), radius = 6.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
    }
}

/**
 * 5. EXPORT / TRANSFORMATION ILLUSTRATION
 * Minimalist illustration showing document fanning out into file formats (JSON / CSV / XLSX / PDF) with red download arrow.
 */
@Composable
fun ExportTransformIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Background warm organic oval
        drawOval(
            color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f),
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = Size(w * 0.76f, h * 0.76f)
        )

        // Left Source Document
        val srcDocRect = Rect(w * 0.18f, h * 0.20f, w * 0.38f, h * 0.80f)
        drawEditorialDocument(
            rect = srcDocRect,
            cornerFoldSize = 10.dp.toPx(),
            strokeWidth = strokeW,
            showLines = true,
            accentRedFold = false
        )

        // Center Action Arrow (Red download / conversion arrow)
        val arrowStart = Offset(w * 0.42f, h * 0.5f)
        val arrowEnd = Offset(w * 0.56f, h * 0.5f)
        drawLine(
            color = SnapDataIllustrationColors.AccentRed,
            start = arrowStart,
            end = arrowEnd,
            strokeWidth = strokeW * 1.2f,
            cap = StrokeCap.Round
        )
        // Arrow head
        val arrowHeadPath = Path().apply {
            moveTo(arrowEnd.x - 6.dp.toPx(), arrowEnd.y - 6.dp.toPx())
            lineTo(arrowEnd.x, arrowEnd.y)
            lineTo(arrowEnd.x - 6.dp.toPx(), arrowEnd.y + 6.dp.toPx())
        }
        drawPath(arrowHeadPath, color = SnapDataIllustrationColors.AccentRed, style = Stroke(width = strokeW * 1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Right Fanned Export File Cards (PDF, XLSX, CSV, JSON)
        val formats = listOf("JSON", "XLSX", "CSV", "PDF")
        for (i in formats.indices) {
            val cardLeft = w * 0.60f + (i * 8.dp.toPx())
            val cardTop = h * 0.20f + (i * 6.dp.toPx())
            val cardW = w * 0.22f
            val cardH = h * 0.55f

            val cardRect = Rect(cardLeft, cardTop, cardLeft + cardW, cardTop + cardH)
            drawEditorialDocument(
                rect = cardRect,
                cornerFoldSize = 8.dp.toPx(),
                strokeWidth = strokeW * 0.8f,
                strokeColor = if (i == 0) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack,
                fillColor = SnapDataIllustrationColors.PaperWhite,
                showLines = false,
                accentRedFold = (i == 0)
            )

            // Format initial / line
            drawLine(
                color = if (i == 0) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.LineMuted,
                start = Offset(cardLeft + 6.dp.toPx(), cardTop + cardH * 0.5f),
                end = Offset(cardLeft + cardW - 6.dp.toPx(), cardTop + cardH * 0.5f),
                strokeWidth = strokeW * 0.7f,
                cap = StrokeCap.Round
            )
        }

        drawEditorialSparkle(center = Offset(w * 0.88f, h * 0.18f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
    }
}

/**
 * 6. ON-DEVICE PRIVACY & SECURITY ILLUSTRATION
 * Minimalist illustration representing Device + Shield/Lock + Private AI Processing.
 */
@Composable
fun OnDevicePrivacyIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Background halo
        drawCircle(
            color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.6f),
            radius = h * 0.44f,
            center = Offset(w * 0.5f, h * 0.5f)
        )

        // Center Smartphone outline
        val phoneW = w * 0.26f
        val phoneH = h * 0.78f
        val phoneLeft = w * 0.37f
        val phoneTop = h * 0.11f

        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(10.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(phoneLeft, phoneTop),
            size = Size(phoneW, phoneH),
            cornerRadius = CornerRadius(10.dp.toPx()),
            style = Stroke(width = strokeW)
        )

        // Phone top speaker slot
        drawLine(
            color = SnapDataIllustrationColors.StrokeBlack,
            start = Offset(phoneLeft + phoneW * 0.35f, phoneTop + 6.dp.toPx()),
            end = Offset(phoneLeft + phoneW * 0.65f, phoneTop + 6.dp.toPx()),
            strokeWidth = strokeW * 0.6f,
            cap = StrokeCap.Round
        )

        // Security Shield in center of phone
        val shieldCenter = Offset(w * 0.5f, h * 0.48f)
        val shieldW = 16.dp.toPx()
        val shieldH = 20.dp.toPx()

        val shieldPath = Path().apply {
            moveTo(shieldCenter.x, shieldCenter.y - shieldH * 0.5f)
            lineTo(shieldCenter.x + shieldW * 0.5f, shieldCenter.y - shieldH * 0.3f)
            lineTo(shieldCenter.x + shieldW * 0.5f, shieldCenter.y + shieldH * 0.2f)
            quadraticTo(shieldCenter.x, shieldCenter.y + shieldH * 0.6f, shieldCenter.x, shieldCenter.y + shieldH * 0.6f)
            quadraticTo(shieldCenter.x - shieldW * 0.5f, shieldCenter.y + shieldH * 0.2f, shieldCenter.x - shieldW * 0.5f, shieldCenter.y + shieldH * 0.2f)
            lineTo(shieldCenter.x - shieldW * 0.5f, shieldCenter.y - shieldH * 0.3f)
            close()
        }

        drawPath(shieldPath, color = SnapDataIllustrationColors.AccentRedLight, style = Fill)
        drawPath(shieldPath, color = SnapDataIllustrationColors.AccentRed, style = Stroke(width = strokeW * 0.9f, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Shield Lock Shackle & Body
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 2.5.dp.toPx(), center = Offset(shieldCenter.x, shieldCenter.y + 1.dp.toPx()))

        // Left & Right privacy badge lines
        drawEditorialSparkle(center = Offset(w * 0.24f, h * 0.35f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f)
        drawEditorialSparkle(center = Offset(w * 0.76f, h * 0.60f), radius = 8.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
    }
}

/**
 * 7. ONBOARDING / LANDING STEP ILLUSTRATION
 * Step 1: Capture & Crop (Camera + Document + Corner alignments)
 * Step 2: OCR & Multimodal AI (Dual Engine Scanner + Table Matrix)
 * Step 3: Structured Data & Universal Export (Verified Sheets + JSON + CSV)
 */
@Composable
fun OnboardingStepIllustration(
    step: Int, // 0, 1, 2
    modifier: Modifier = Modifier,
    height: Dp = 130.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 2.dp.toPx()

        when (step) {
            0 -> {
                // Step 1: Document Acquisition & Auto-Crop
                drawOval(
                    color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f),
                    topLeft = Offset(w * 0.15f, h * 0.10f),
                    size = Size(w * 0.70f, h * 0.80f)
                )

                // Document sheet with skew/perspective alignment
                val docRect = Rect(w * 0.32f, h * 0.18f, w * 0.68f, h * 0.82f)
                drawEditorialDocument(
                    rect = docRect,
                    cornerFoldSize = 14.dp.toPx(),
                    strokeWidth = strokeW,
                    showLines = true,
                    accentRedFold = true
                )

                // Camera viewfinder corner brackets around document
                val bracketPad = 10.dp.toPx()
                val bLeft = docRect.left - bracketPad
                val bTop = docRect.top - bracketPad
                val bRight = docRect.right + bracketPad
                val bBottom = docRect.bottom + bracketPad
                val bLen = 14.dp.toPx()

                // 4 Red Corner Brackets
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bLeft, bTop), Offset(bLeft + bLen, bTop), strokeW * 1.1f, StrokeCap.Round)
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bLeft, bTop), Offset(bLeft, bTop + bLen), strokeW * 1.1f, StrokeCap.Round)

                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bRight, bTop), Offset(bRight - bLen, bTop), strokeW * 1.1f, StrokeCap.Round)
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bRight, bTop), Offset(bRight, bTop + bLen), strokeW * 1.1f, StrokeCap.Round)

                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bLeft, bBottom), Offset(bLeft + bLen, bBottom), strokeW * 1.1f, StrokeCap.Round)
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bLeft, bBottom), Offset(bLeft, bBottom - bLen), strokeW * 1.1f, StrokeCap.Round)

                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bRight, bBottom), Offset(bRight - bLen, bBottom), strokeW * 1.1f, StrokeCap.Round)
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(bRight, bBottom), Offset(bRight, bBottom - bLen), strokeW * 1.1f, StrokeCap.Round)

                drawEditorialSparkle(center = Offset(w * 0.18f, h * 0.25f), radius = 8.dp.toPx(), strokeWidth = strokeW * 0.7f)
            }
            1 -> {
                // Step 2: Processing & Multimodal AI Extraction
                drawOval(
                    color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f),
                    topLeft = Offset(w * 0.12f, h * 0.10f),
                    size = Size(w * 0.76f, h * 0.80f)
                )

                // Split view: Left OCR scanning -> Right Structured Data
                val docRect = Rect(w * 0.20f, h * 0.18f, w * 0.46f, h * 0.82f)
                drawEditorialDocument(
                    rect = docRect,
                    cornerFoldSize = 10.dp.toPx(),
                    strokeWidth = strokeW,
                    showLines = true,
                    accentRedFold = false
                )

                // Laser scan line
                val scanY = docRect.top + docRect.height * 0.45f
                drawLine(SnapDataIllustrationColors.AccentRed, Offset(docRect.left - 6.dp.toPx(), scanY), Offset(docRect.right + 6.dp.toPx(), scanY), strokeW, StrokeCap.Round)

                // Center AI sparkle connection
                drawEditorialSparkle(center = Offset(w * 0.54f, h * 0.50f), radius = 10.dp.toPx(), strokeWidth = strokeW * 0.8f, accentRed = true)

                // Right structured table result
                val tableRect = Rect(w * 0.62f, h * 0.24f, w * 0.86f, h * 0.76f)
                drawRoundRect(
                    color = SnapDataIllustrationColors.PaperWhite,
                    topLeft = Offset(tableRect.left, tableRect.top),
                    size = Size(tableRect.width, tableRect.height),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                    style = Fill
                )
                drawRoundRect(
                    color = SnapDataIllustrationColors.StrokeBlack,
                    topLeft = Offset(tableRect.left, tableRect.top),
                    size = Size(tableRect.width, tableRect.height),
                    cornerRadius = CornerRadius(6.dp.toPx()),
                    style = Stroke(width = strokeW)
                )
                drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(tableRect.left, tableRect.top + tableRect.height * 0.4f), Offset(tableRect.right, tableRect.top + tableRect.height * 0.4f), strokeW * 0.7f)
                drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(tableRect.left + tableRect.width * 0.45f, tableRect.top), Offset(tableRect.left + tableRect.width * 0.45f, tableRect.bottom), strokeW * 0.7f)
            }
            2 -> {
                // Step 3: Verified Universal Export & Integrations
                drawOval(
                    color = SnapDataIllustrationColors.PaperShadow.copy(alpha = 0.55f),
                    topLeft = Offset(w * 0.12f, h * 0.10f),
                    size = Size(w * 0.76f, h * 0.80f)
                )

                // Main Verified Table Card with Green/Black Checkmark and Red Highlight
                val cardRect = Rect(w * 0.25f, h * 0.18f, w * 0.75f, h * 0.82f)
                drawRoundRect(
                    color = SnapDataIllustrationColors.PaperWhite,
                    topLeft = Offset(cardRect.left, cardRect.top),
                    size = Size(cardRect.width, cardRect.height),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Fill
                )
                drawRoundRect(
                    color = SnapDataIllustrationColors.StrokeBlack,
                    topLeft = Offset(cardRect.left, cardRect.top),
                    size = Size(cardRect.width, cardRect.height),
                    cornerRadius = CornerRadius(8.dp.toPx()),
                    style = Stroke(width = strokeW)
                )

                // Top header bar in card
                drawRoundRect(
                    color = SnapDataIllustrationColors.PaperShadow,
                    topLeft = Offset(cardRect.left, cardRect.top),
                    size = Size(cardRect.width, cardRect.height * 0.32f),
                    cornerRadius = CornerRadius(7.dp.toPx()),
                    style = Fill
                )

                // Checkmark badge in header
                val checkCenter = Offset(cardRect.left + 14.dp.toPx(), cardRect.top + cardRect.height * 0.16f)
                drawCircle(SnapDataIllustrationColors.AccentRed, radius = 5.dp.toPx(), center = checkCenter)

                // Export File Type Pills (JSON / CSV / EXCEL)
                val pillLeft = cardRect.left + 10.dp.toPx()
                val pillTop = cardRect.top + cardRect.height * 0.52f
                val pillW = cardRect.width * 0.42f
                val pillH = cardRect.height * 0.32f

                drawRoundRect(
                    color = SnapDataIllustrationColors.AccentRedLight,
                    topLeft = Offset(pillLeft, pillTop),
                    size = Size(pillW, pillH),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Fill
                )
                drawRoundRect(
                    color = SnapDataIllustrationColors.AccentRed,
                    topLeft = Offset(pillLeft, pillTop),
                    size = Size(pillW, pillH),
                    cornerRadius = CornerRadius(4.dp.toPx()),
                    style = Stroke(width = strokeW * 0.7f)
                )

                drawEditorialSparkle(center = Offset(w * 0.84f, h * 0.22f), radius = 8.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
                drawEditorialSparkle(center = Offset(w * 0.16f, h * 0.75f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f)
            }
        }
    }
}

/**
 * 8. PERSON SCANNING DOCUMENT ILLUSTRATION (Sheet 1)
 */
@Composable
fun PersonScanningDocumentIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 110.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Person Head & Hair
        val headCenter = Offset(w * 0.24f, h * 0.32f)
        val headRadius = 16.dp.toPx()
        
        // Hair (black fill)
        val hairPath = Path().apply {
            moveTo(headCenter.x - headRadius * 1.1f, headCenter.y + headRadius * 0.2f)
            cubicTo(headCenter.x - headRadius * 1.3f, headCenter.y - headRadius * 1.2f, headCenter.x + headRadius * 0.8f, headCenter.y - headRadius * 1.3f, headCenter.x + headRadius * 1.1f, headCenter.y - headRadius * 0.2f)
            cubicTo(headCenter.x + headRadius * 1.3f, headCenter.y + headRadius * 0.8f, headCenter.x + headRadius * 0.4f, headCenter.y + headRadius * 1.2f, headCenter.x - headRadius * 0.2f, headCenter.y + headRadius * 1.1f)
            close()
        }
        drawPath(hairPath, color = SnapDataIllustrationColors.StrokeBlack, style = Fill)

        // Face profile
        drawCircle(color = SnapDataIllustrationColors.PaperWhite, radius = headRadius * 0.85f, center = headCenter, style = Fill)
        drawCircle(color = SnapDataIllustrationColors.StrokeBlack, radius = headRadius * 0.85f, center = headCenter, style = Stroke(strokeW))

        // Body / Arm holding phone
        val armPath = Path().apply {
            moveTo(headCenter.x - 4.dp.toPx(), headCenter.y + headRadius + 4.dp.toPx())
            quadraticTo(w * 0.28f, h * 0.65f, w * 0.42f, h * 0.58f)
            lineTo(w * 0.46f, h * 0.65f)
            quadraticTo(w * 0.32f, h * 0.78f, headCenter.x - 12.dp.toPx(), h * 0.95f)
        }
        drawPath(armPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round))

        // Smartphone in hand
        val phoneRect = Rect(w * 0.42f, h * 0.35f, w * 0.55f, h * 0.75f)
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(phoneRect.left, phoneRect.top),
            size = Size(phoneRect.width, phoneRect.height),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(phoneRect.left, phoneRect.top),
            size = Size(phoneRect.width, phoneRect.height),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(strokeW)
        )

        // Red viewfinder brackets inside phone
        val reticlePad = 4.dp.toPx()
        val rL = phoneRect.left + reticlePad
        val rT = phoneRect.top + reticlePad
        val rR = phoneRect.right - reticlePad
        val rB = phoneRect.bottom - reticlePad
        val bLen = 6.dp.toPx()
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(rL, rT), Offset(rL + bLen, rT), strokeW * 0.9f)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(rL, rT), Offset(rL, rT + bLen), strokeW * 0.9f)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(rR, rB), Offset(rR - bLen, rB), strokeW * 0.9f)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(rR, rB), Offset(rR, rB - bLen), strokeW * 0.9f)

        // Document being scanned
        val docRect = Rect(w * 0.65f, h * 0.22f, w * 0.88f, h * 0.85f)
        drawEditorialDocument(rect = docRect, cornerFoldSize = 12.dp.toPx(), strokeWidth = strokeW, showLines = true, accentRedFold = true)

        // Red scan beam from phone to document
        val scanPath = Path().apply {
            moveTo(phoneRect.right, phoneRect.top + phoneRect.height * 0.3f)
            lineTo(docRect.left, docRect.top + docRect.height * 0.2f)
            moveTo(phoneRect.right, phoneRect.bottom - phoneRect.height * 0.3f)
            lineTo(docRect.left, docRect.bottom - docRect.height * 0.2f)
        }
        drawPath(scanPath, color = SnapDataIllustrationColors.AccentRed.copy(alpha = 0.5f), style = Stroke(strokeW * 0.7f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)))

        drawEditorialSparkle(center = Offset(w * 0.92f, h * 0.18f), radius = 6.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
    }
}

/**
 * 9. OCR EXTRACTION ILLUSTRATION (Sheet 2)
 */
@Composable
fun OcrExtractionIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Left Document with bracketed "A"
        val leftDocRect = Rect(w * 0.15f, h * 0.15f, w * 0.40f, h * 0.85f)
        drawEditorialDocument(rect = leftDocRect, cornerFoldSize = 10.dp.toPx(), strokeWidth = strokeW, showLines = false)

        // Red OCR Brackets around "A"
        val aCenter = Offset(w * 0.275f, h * 0.42f)
        val bPad = 12.dp.toPx()
        val bL = aCenter.x - bPad
        val bT = aCenter.y - bPad
        val bR = aCenter.x + bPad
        val bB = aCenter.y + bPad
        val bLen = 6.dp.toPx()
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(bL, bT), Offset(bL + bLen, bT), strokeW)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(bL, bT), Offset(bL, bT + bLen), strokeW)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(bR, bB), Offset(bR - bLen, bB), strokeW)
        drawLine(SnapDataIllustrationColors.AccentRed, Offset(bR, bB), Offset(bR, bB - bLen), strokeW)

        // Letter A
        val aPath = Path().apply {
            moveTo(aCenter.x, aCenter.y - 7.dp.toPx())
            lineTo(aCenter.x - 5.dp.toPx(), aCenter.y + 7.dp.toPx())
            moveTo(aCenter.x, aCenter.y - 7.dp.toPx())
            lineTo(aCenter.x + 5.dp.toPx(), aCenter.y + 7.dp.toPx())
            moveTo(aCenter.x - 3.dp.toPx(), aCenter.y + 2.dp.toPx())
            lineTo(aCenter.x + 3.dp.toPx(), aCenter.y + 2.dp.toPx())
        }
        drawPath(aPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW * 1.1f, cap = StrokeCap.Round))

        // Lines below A
        drawLine(SnapDataIllustrationColors.LineMuted, Offset(leftDocRect.left + 8.dp.toPx(), h * 0.68f), Offset(leftDocRect.right - 8.dp.toPx(), h * 0.68f), strokeW * 0.6f, StrokeCap.Round)
        drawLine(SnapDataIllustrationColors.LineMuted, Offset(leftDocRect.left + 8.dp.toPx(), h * 0.76f), Offset(leftDocRect.right - 14.dp.toPx(), h * 0.76f), strokeW * 0.6f, StrokeCap.Round)

        // OCR arrow
        val arrowPath = Path().apply {
            moveTo(w * 0.44f, h * 0.5f)
            lineTo(w * 0.58f, h * 0.5f)
            lineTo(w * 0.54f, h * 0.44f)
            moveTo(w * 0.58f, h * 0.5f)
            lineTo(w * 0.54f, h * 0.56f)
        }
        drawPath(arrowPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Right Clean Text Document
        val rightDocRect = Rect(w * 0.62f, h * 0.15f, w * 0.87f, h * 0.85f)
        drawEditorialDocument(rect = rightDocRect, cornerFoldSize = 10.dp.toPx(), strokeWidth = strokeW, showLines = true, accentRedFold = true)
    }
}

/**
 * 10. AI DOCUMENT INTELLIGENCE ILLUSTRATION (Sheet 3)
 */
@Composable
fun AiDocumentIntelligenceIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 100.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Left Document
        val docRect = Rect(w * 0.15f, h * 0.15f, w * 0.40f, h * 0.85f)
        drawEditorialDocument(rect = docRect, cornerFoldSize = 10.dp.toPx(), strokeWidth = strokeW, showLines = true)

        // Connecting dashed curved line
        val connectorPath = Path().apply {
            moveTo(docRect.right + 4.dp.toPx(), h * 0.5f)
            quadraticTo(w * 0.53f, h * 0.42f, w * 0.62f, h * 0.5f)
        }
        drawPath(connectorPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW * 0.8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)))

        // Right AI Brain
        val brainCenter = Offset(w * 0.76f, h * 0.5f)
        val brainRadius = 18.dp.toPx()

        // Left hemisphere
        val leftBrain = Path().apply {
            moveTo(brainCenter.x, brainCenter.y - brainRadius)
            cubicTo(brainCenter.x - brainRadius * 0.8f, brainCenter.y - brainRadius * 1.1f, brainCenter.x - brainRadius * 1.3f, brainCenter.y - brainRadius * 0.2f, brainCenter.x - brainRadius * 0.9f, brainCenter.y)
            cubicTo(brainCenter.x - brainRadius * 1.3f, brainCenter.y + brainRadius * 0.3f, brainCenter.x - brainRadius * 0.8f, brainCenter.y + brainRadius * 1.1f, brainCenter.x, brainCenter.y + brainRadius)
        }
        // Right hemisphere
        val rightBrain = Path().apply {
            moveTo(brainCenter.x, brainCenter.y - brainRadius)
            cubicTo(brainCenter.x + brainRadius * 0.8f, brainCenter.y - brainRadius * 1.1f, brainCenter.x + brainRadius * 1.3f, brainCenter.y - brainRadius * 0.2f, brainCenter.x + brainRadius * 0.9f, brainCenter.y)
            cubicTo(brainCenter.x + brainRadius * 1.3f, brainCenter.y + brainRadius * 0.3f, brainCenter.x + brainRadius * 0.8f, brainCenter.y + brainRadius * 1.1f, brainCenter.x, brainCenter.y + brainRadius)
        }

        drawPath(leftBrain, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round))
        drawPath(rightBrain, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round))

        // Brain internal folds
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(brainCenter.x, brainCenter.y - brainRadius * 0.8f), Offset(brainCenter.x, brainCenter.y + brainRadius * 0.8f), strokeW * 0.7f)
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 3.dp.toPx(), center = Offset(brainCenter.x - 4.dp.toPx(), brainCenter.y - 4.dp.toPx()))
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 3.dp.toPx(), center = Offset(brainCenter.x + 4.dp.toPx(), brainCenter.y + 4.dp.toPx()))

        drawEditorialSparkle(center = Offset(w * 0.92f, h * 0.22f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
        drawEditorialSparkle(center = Offset(w * 0.58f, h * 0.78f), radius = 5.dp.toPx(), strokeWidth = strokeW * 0.6f, accentRed = true)
    }
}

/**
 * 11. DATA EXTRACTION FIELDS ILLUSTRATION (Sheet 4)
 */
@Composable
fun DataExtractionFieldsIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 110.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Left Document
        val docRect = Rect(w * 0.12f, h * 0.15f, w * 0.38f, h * 0.85f)
        drawEditorialDocument(rect = docRect, cornerFoldSize = 10.dp.toPx(), strokeWidth = strokeW, showLines = true)

        // Arrow
        val arrowPath = Path().apply {
            moveTo(w * 0.42f, h * 0.5f)
            lineTo(w * 0.52f, h * 0.5f)
            lineTo(w * 0.49f, h * 0.45f)
            moveTo(w * 0.52f, h * 0.5f)
            lineTo(w * 0.49f, h * 0.55f)
        }
        drawPath(arrowPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Right Fields Card
        val cardRect = Rect(w * 0.56f, h * 0.12f, w * 0.90f, h * 0.88f)
        drawRoundRect(
            color = SnapDataIllustrationColors.PaperWhite,
            topLeft = Offset(cardRect.left, cardRect.top),
            size = Size(cardRect.width, cardRect.height),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Fill
        )
        drawRoundRect(
            color = SnapDataIllustrationColors.StrokeBlack,
            topLeft = Offset(cardRect.left, cardRect.top),
            size = Size(cardRect.width, cardRect.height),
            cornerRadius = CornerRadius(6.dp.toPx()),
            style = Stroke(strokeW)
        )

        // Field items (Name, Date, Total, Phone icons & lines)
        val fieldCount = 4
        val fieldH = cardRect.height / fieldCount
        for (i in 0 until fieldCount) {
            val y = cardRect.top + (i * fieldH) + fieldH * 0.5f
            val isTotal = (i == 2)

            // Left icon dot or symbol
            drawCircle(
                color = if (isTotal) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack,
                radius = 2.5.dp.toPx(),
                center = Offset(cardRect.left + 8.dp.toPx(), y)
            )

            // Key line
            drawLine(
                color = if (isTotal) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.StrokeBlack,
                start = Offset(cardRect.left + 14.dp.toPx(), y),
                end = Offset(cardRect.left + cardRect.width * 0.45f, y),
                strokeWidth = strokeW * 0.7f,
                cap = StrokeCap.Round
            )
            // Value line
            drawLine(
                color = if (isTotal) SnapDataIllustrationColors.AccentRed else SnapDataIllustrationColors.LineMuted,
                start = Offset(cardRect.left + cardRect.width * 0.55f, y),
                end = Offset(cardRect.right - 8.dp.toPx(), y),
                strokeWidth = strokeW * 0.6f,
                cap = StrokeCap.Round
            )
        }

        drawEditorialSparkle(center = Offset(w * 0.50f, h * 0.22f), radius = 6.dp.toPx(), strokeWidth = strokeW * 0.6f, accentRed = true)
    }
}

/**
 * 12. HERO PERSON WITH LAPTOP ILLUSTRATION (Sheet 12)
 */
@Composable
fun HeroPersonIllustration(
    modifier: Modifier = Modifier,
    height: Dp = 140.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        val w = size.width
        val h = size.height
        val strokeW = 1.8.dp.toPx()

        // Person Head & Hair
        val headCenter = Offset(w * 0.5f, h * 0.30f)
        val headRadius = 14.dp.toPx()

        // Hair
        val hairPath = Path().apply {
            moveTo(headCenter.x - headRadius * 1.1f, headCenter.y + headRadius * 0.2f)
            cubicTo(headCenter.x - headRadius * 1.2f, headCenter.y - headRadius * 1.3f, headCenter.x + headRadius * 1.2f, headCenter.y - headRadius * 1.3f, headCenter.x + headRadius * 1.1f, headCenter.y + headRadius * 0.2f)
            cubicTo(headCenter.x + headRadius * 0.6f, headCenter.y - headRadius * 0.5f, headCenter.x - headRadius * 0.6f, headCenter.y - headRadius * 0.5f, headCenter.x - headRadius * 1.1f, headCenter.y + headRadius * 0.2f)
            close()
        }
        drawPath(hairPath, color = SnapDataIllustrationColors.StrokeBlack, style = Fill)
        drawCircle(color = SnapDataIllustrationColors.PaperWhite, radius = headRadius * 0.8f, center = headCenter, style = Fill)
        drawCircle(color = SnapDataIllustrationColors.StrokeBlack, radius = headRadius * 0.8f, center = headCenter, style = Stroke(strokeW))

        // Torso
        val torsoPath = Path().apply {
            moveTo(headCenter.x - 8.dp.toPx(), headCenter.y + headRadius)
            quadraticTo(headCenter.x - 22.dp.toPx(), h * 0.60f, headCenter.x - 30.dp.toPx(), h * 0.78f)
            lineTo(headCenter.x + 30.dp.toPx(), h * 0.78f)
            quadraticTo(headCenter.x + 22.dp.toPx(), h * 0.60f, headCenter.x + 8.dp.toPx(), headCenter.y + headRadius)
            close()
        }
        drawPath(torsoPath, color = SnapDataIllustrationColors.PaperWhite, style = Fill)
        drawPath(torsoPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Desk / Surface line
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(w * 0.15f, h * 0.80f), Offset(w * 0.85f, h * 0.80f), strokeW, StrokeCap.Round)

        // Laptop on desk
        val laptopW = 44.dp.toPx()
        val laptopH = 26.dp.toPx()
        val laptopLeft = headCenter.x - laptopW * 0.5f
        val laptopTop = h * 0.54f

        // Screen
        val screenPath = Path().apply {
            moveTo(laptopLeft, laptopTop)
            lineTo(laptopLeft + laptopW, laptopTop)
            lineTo(laptopLeft + laptopW * 0.95f, laptopTop + laptopH)
            lineTo(laptopLeft + laptopW * 0.05f, laptopTop + laptopH)
            close()
        }
        drawPath(screenPath, color = SnapDataIllustrationColors.PaperWhite, style = Fill)
        drawPath(screenPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW))

        // SnapData Red Logo on back of laptop
        drawCircle(SnapDataIllustrationColors.AccentRed, radius = 4.dp.toPx(), center = Offset(headCenter.x, laptopTop + laptopH * 0.5f))

        // Laptop base
        drawLine(SnapDataIllustrationColors.StrokeBlack, Offset(laptopLeft - 6.dp.toPx(), h * 0.80f), Offset(laptopLeft + laptopW + 6.dp.toPx(), h * 0.80f), strokeW * 1.5f, StrokeCap.Round)

        // Small Plant on left
        val potCenter = Offset(w * 0.22f, h * 0.78f)
        val potPath = Path().apply {
            moveTo(potCenter.x - 8.dp.toPx(), potCenter.y - 10.dp.toPx())
            lineTo(potCenter.x + 8.dp.toPx(), potCenter.y - 10.dp.toPx())
            lineTo(potCenter.x + 6.dp.toPx(), potCenter.y)
            lineTo(potCenter.x - 6.dp.toPx(), potCenter.y)
            close()
        }
        drawPath(potPath, color = SnapDataIllustrationColors.PaperWhite, style = Fill)
        drawPath(potPath, color = SnapDataIllustrationColors.StrokeBlack, style = Stroke(strokeW))
        // Plant leaves
        drawCircle(SnapDataIllustrationColors.AccentRedLight, radius = 4.dp.toPx(), center = Offset(potCenter.x - 3.dp.toPx(), potCenter.y - 14.dp.toPx()))
        drawCircle(SnapDataIllustrationColors.StrokeBlack, radius = 4.dp.toPx(), center = Offset(potCenter.x - 3.dp.toPx(), potCenter.y - 14.dp.toPx()), style = Stroke(strokeW * 0.7f))
        drawCircle(SnapDataIllustrationColors.AccentRedLight, radius = 4.dp.toPx(), center = Offset(potCenter.x + 3.dp.toPx(), potCenter.y - 14.dp.toPx()))
        drawCircle(SnapDataIllustrationColors.StrokeBlack, radius = 4.dp.toPx(), center = Offset(potCenter.x + 3.dp.toPx(), potCenter.y - 14.dp.toPx()), style = Stroke(strokeW * 0.7f))

        // Stack of documents on right
        val stackLeft = w * 0.74f
        val stackTop = h * 0.68f
        for (i in 0 until 3) {
            val sT = stackTop + (i * 4.dp.toPx())
            drawRoundRect(
                color = SnapDataIllustrationColors.PaperWhite,
                topLeft = Offset(stackLeft, sT),
                size = Size(20.dp.toPx(), 12.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx()),
                style = Fill
            )
            drawRoundRect(
                color = SnapDataIllustrationColors.StrokeBlack,
                topLeft = Offset(stackLeft, sT),
                size = Size(20.dp.toPx(), 12.dp.toPx()),
                cornerRadius = CornerRadius(2.dp.toPx()),
                style = Stroke(strokeW * 0.7f)
            )
        }

        // Sparkles
        drawEditorialSparkle(center = Offset(w * 0.35f, h * 0.20f), radius = 7.dp.toPx(), strokeWidth = strokeW * 0.7f, accentRed = true)
        drawEditorialSparkle(center = Offset(w * 0.65f, h * 0.22f), radius = 6.dp.toPx(), strokeWidth = strokeW * 0.6f, accentRed = true)
    }
}
