package com.example.snapdata.ui.screens.guide

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.components.branding.SnapDataSymbol

/**
 * Top header component for the SnapData Interactive User Guide.
 * Displays the official SnapData symbol, AI tagline, "INTERACTIVE USER GUIDE" pill,
 * 10-dot progress sequence with luminous active glow, and "01 / 10" step counter.
 */
@Composable
fun GuideHeader(
    currentStepIndex: Int,
    totalSteps: Int = 10,
    onStepClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val displayStepIndex = (currentStepIndex + 1).coerceIn(1, totalSteps)
    val formattedCounter = String.format("%02d / %02d", displayStepIndex, totalSteps)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Logo + Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            SnapDataSymbol(
                size = 32.dp,
                variant = SnapDataLogoVariant.WHITE_ON_DARK
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "SNAPDATA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // 2. Tagline
        Text(
            text = "AI-Powered Document Intelligence",
            fontSize = 11.5.sp,
            color = Color(0xFFA0A0AA),
            letterSpacing = 0.4.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. "INTERACTIVE USER GUIDE" Pill Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF14151B))
                .border(1.dp, Color(0xFF2E303E), RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text = "INTERACTIVE USER GUIDE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = Color(0xFFE2E4EB)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Horizontal 10-Dot Progress Bar
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalSteps) {
                val isActive = i == currentStepIndex
                val isCompleted = i < currentStepIndex

                val dotWidth = if (isActive) 18.dp else 7.dp
                val dotColor = when {
                    isActive -> Color.White
                    isCompleted -> Color(0xFF8E90A0)
                    else -> Color(0xFF333544)
                }

                Box(
                    modifier = Modifier
                        .height(7.dp)
                        .width(dotWidth)
                        .clip(RoundedCornerShape(4.dp))
                        .background(dotColor)
                        .then(
                            if (isActive) {
                                Modifier.shadow(6.dp, CircleShape, spotColor = Color.White)
                            } else {
                                Modifier
                            }
                        )
                        .then(
                            if (onStepClick != null) {
                                Modifier.clickable { onStepClick(i) }
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. Step Counter (01 / 10)
        Text(
            text = formattedCounter,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = Color(0xFFD0D2DC)
        )
    }
}
