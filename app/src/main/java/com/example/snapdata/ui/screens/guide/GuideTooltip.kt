package com.example.snapdata.ui.screens.guide

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Premium dark glassmorphic tooltip card that explains the highlighted component.
 */
@Composable
fun GuideTooltip(
    text: String,
    modifier: Modifier = Modifier,
    isAboveTarget: Boolean = false
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            animationSpec = tween(300),
            initialOffsetY = { if (isAboveTarget) -20 else 20 }
        ),
        exit = fadeOut(animationSpec = tween(200))
    ) {
        Box(
            modifier = modifier
                .shadow(12.dp, RoundedCornerShape(14.dp), ambientColor = Color(0x66000000), spotColor = Color(0xAA000000))
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF01C1E26),
                            Color(0xF012131A)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x80FFFFFF),
                            Color(0x22FFFFFF)
                        )
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
