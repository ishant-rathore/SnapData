package com.example.snapdata.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import kotlinx.coroutines.delay

/**
 * Official SnapData Android Splash Screen:
 * Centered white/red SnapData logo on a deep dark charcoal background (#0C0D10).
 * Minimalist, elegant, and enterprise-grade.
 */
@Composable
fun SnapDataSplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startFade by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startFade) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "splash_fade"
    )

    LaunchedEffect(Unit) {
        startFade = true
        delay(1200)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C0D10))
            .testTag("snapdata_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .alpha(alphaAnim)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SnapDataLogo(
                variant = SnapDataLogoVariant.FULL_VERTICAL,
                iconSize = 100.dp,
                wordmarkSize = 36.sp,
                taglineSize = 11.sp,
                isDarkBackground = true,
                showTagline = true
            )
        }

        // Minimalist bottom version indicator
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(alphaAnim * 0.7f)
        ) {
            Text(
                text = "v2.0 • ON-DEVICE INTELLIGENCE",
                color = Color(0xFF6B6B75),
                fontSize = 10.sp,
                letterSpacing = 1.2.sp
            )
        }
    }
}
