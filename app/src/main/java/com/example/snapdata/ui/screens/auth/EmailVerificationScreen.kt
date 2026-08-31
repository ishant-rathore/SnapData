package com.example.snapdata.ui.screens.auth

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.auth.domain.AppAuthError
import com.example.snapdata.ui.theme.*
import kotlinx.coroutines.delay

/**
 * AUTH-05: Email Verification Screen.
 */
@Composable
fun EmailVerificationScreen(
    userEmail: String,
    onCheckVerified: () -> Unit,
    onResendEmail: () -> Unit,
    onSignOutOrBack: () -> Unit,
    onProceedAnyway: () -> Unit,
    isLoading: Boolean = false,
    authError: AppAuthError? = null,
    isResendSuccess: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 60-second cooldown timer state
    var cooldownSeconds by remember { mutableIntStateOf(60) }
    var isCooldownActive by remember { mutableStateOf(true) }

    LaunchedEffect(isCooldownActive, isResendSuccess) {
        if (isResendSuccess) {
            cooldownSeconds = 60
            isCooldownActive = true
        }
        if (isCooldownActive) {
            while (cooldownSeconds > 0) {
                delay(1000)
                cooldownSeconds -= 1
            }
            isCooldownActive = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Brand & Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(SnapDataBlack, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = null,
                tint = WarmOffWhite,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Verify your email",
            style = MaterialTheme.typography.headlineMedium,
            color = SnapDataBlack,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "We've sent a verification link to:",
            style = MaterialTheme.typography.bodyMedium,
            color = SnapDataBlack.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userEmail.ifBlank { "your email address" },
            style = MaterialTheme.typography.titleMedium,
            color = SnapDataBlack,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Click the link in the email to complete your registration.",
            style = MaterialTheme.typography.bodySmall,
            color = SnapDataBlack.copy(alpha = 0.65f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (authError != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = authError.userMessage,
                    color = Color(0xFF991B1B),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (isResendSuccess) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Verification email resent successfully.",
                    color = Color(0xFF065F46),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Action 1: Open Email App
        SnapDataPrimaryButton(
            text = "Open Email App",
            onClick = {
                try {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_APP_EMAIL)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                    // Fallback to general intent
                }
            },
            testTag = "verify_open_email_btn"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Action 2: I've Verified (checks state)
        SnapDataSecondaryButton(
            text = "I've Verified",
            onClick = onCheckVerified,
            enabled = !isLoading,
            testTag = "verify_check_status_btn"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Action 3: Resend Email with Cooldown
        OutlinedButton(
            onClick = onResendEmail,
            enabled = !isCooldownActive && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("verify_resend_email_btn"),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isCooldownActive) "Resend Email (${cooldownSeconds}s)" else "Resend Verification Email",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue / Offline bypass
        TextButton(
            onClick = onProceedAnyway,
            modifier = Modifier.testTag("verify_continue_btn")
        ) {
            Text(
                text = "Continue to SnapData (Verify later)",
                color = SnapDataBlack.copy(alpha = 0.7f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        TextButton(
            onClick = onSignOutOrBack,
            modifier = Modifier.testTag("verify_sign_out_btn")
        ) {
            Text(
                text = "Sign out or change account",
                color = SnapDataRed,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
