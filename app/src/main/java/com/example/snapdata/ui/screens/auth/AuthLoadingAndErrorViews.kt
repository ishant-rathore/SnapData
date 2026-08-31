package com.example.snapdata.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.auth.domain.AppAuthError
import com.example.snapdata.ui.theme.*
import kotlinx.coroutines.delay

/**
 * AUTH-06: Authentication Loading & Session Restoration View.
 */
@Composable
fun AuthLoadingScreen(
    statusMessage: String = "Restoring session...",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .testTag("auth_loading_spinner"),
                color = SnapDataBlack,
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SNAPDATA",
                style = MaterialTheme.typography.titleMedium,
                color = SnapDataBlack,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = statusMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = SnapDataBlack.copy(alpha = 0.65f)
            )
        }
    }
}

/**
 * AUTH-07: Authentication Error Screen.
 */
@Composable
fun AuthErrorScreen(
    error: AppAuthError,
    onRetry: () -> Unit,
    onBackToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("auth_error_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color(0xFFFEE2E2), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (error is AppAuthError.NetworkUnavailable) Icons.Default.WifiOff else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = SnapDataRed,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Authentication Notice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = SnapDataBlack
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = error.userMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SnapDataBlack.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (error.isRetryable) {
                    SnapDataPrimaryButton(
                        text = "Try Again",
                        onClick = onRetry,
                        testTag = "auth_error_retry_btn"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                SnapDataSecondaryButton(
                    text = "Back to Sign In",
                    onClick = onBackToAuth,
                    testTag = "auth_error_back_btn"
                )
            }
        }
    }
}

/**
 * AUTH-08: Authentication Success / Transition Screen.
 */
@Composable
fun AuthSuccessScreen(
    onTransitionComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var checkScale by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(Unit) {
        checkScale = 1.0f
        delay(1200) // Brief smooth presentation before transition to Home
        onTransitionComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(checkScale)
                    .background(Color(0xFF10B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Welcome to SnapData",
                style = MaterialTheme.typography.headlineSmall,
                color = SnapDataBlack,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your workspace is ready.",
                style = MaterialTheme.typography.bodyMedium,
                color = SnapDataBlack.copy(alpha = 0.7f)
            )
        }
    }
}
