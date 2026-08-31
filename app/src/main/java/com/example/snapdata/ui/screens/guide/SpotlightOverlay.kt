package com.example.snapdata.ui.screens.guide

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Premium spotlight overlay that dims the background and highlights a target UI component
 * with a luminous white border and animated subtle outer glow.
 */
@Composable
fun SpotlightOverlay(
    targetBounds: Rect?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    scrimColor: Color = Color(0xBF050608),
    glowColor: Color = Color.White,
    isCircle: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spotlight_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_glow_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spotlight_scale"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        if (targetBounds == null || targetBounds.isEmpty) {
            // No target, just draw a soft scrim
            drawRect(color = scrimColor)
            return@Canvas
        }

        val radiusPx = cornerRadius.toPx()

        // 1. Cutout dimmed scrim using PathOperation.Difference
        val fullRectPath = Path().apply {
            addRect(Rect(0f, 0f, size.width, size.height))
        }

        val cutoutPath = Path().apply {
            if (isCircle) {
                val center = targetBounds.center
                val radius = (targetBounds.maxDimension / 2f) + 4.dp.toPx()
                addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
            } else {
                val paddedRect = Rect(
                    left = targetBounds.left - 4.dp.toPx(),
                    top = targetBounds.top - 4.dp.toPx(),
                    right = targetBounds.right + 4.dp.toPx(),
                    bottom = targetBounds.bottom + 4.dp.toPx()
                )
                addRoundRect(RoundRect(paddedRect, CornerRadius(radiusPx, radiusPx)))
            }
        }

        val combinedScrimPath = Path.combine(PathOperation.Difference, fullRectPath, cutoutPath)
        drawPath(path = combinedScrimPath, color = scrimColor)

        // 2. Draw luminous white border & glowing outline around the cutout
        val paddedRect = Rect(
            left = targetBounds.left - 4.dp.toPx(),
            top = targetBounds.top - 4.dp.toPx(),
            right = targetBounds.right + 4.dp.toPx(),
            bottom = targetBounds.bottom + 4.dp.toPx()
        )

        if (isCircle) {
            val center = targetBounds.center
            val radius = (targetBounds.maxDimension / 2f) + 4.dp.toPx()

            // Outer glow ring
            drawCircle(
                color = glowColor.copy(alpha = pulseAlpha * 0.4f),
                radius = radius + (3.dp.toPx() * pulseScale),
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Crisp inner white ring
            drawCircle(
                color = glowColor,
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        } else {
            // Outer glow rounded rect
            val glowPadding = 2.dp.toPx() * pulseScale
            drawRoundRect(
                color = glowColor.copy(alpha = pulseAlpha * 0.35f),
                topLeft = Offset(paddedRect.left - glowPadding, paddedRect.top - glowPadding),
                size = Size(paddedRect.width + (glowPadding * 2), paddedRect.height + (glowPadding * 2)),
                cornerRadius = CornerRadius(radiusPx + glowPadding, radiusPx + glowPadding),
                style = Stroke(width = 3.dp.toPx())
            )

            // Crisp inner white border
            drawRoundRect(
                color = glowColor,
                topLeft = Offset(paddedRect.left, paddedRect.top),
                size = Size(paddedRect.width, paddedRect.height),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = 1.75.dp.toPx())
            )
        }
    }
}
