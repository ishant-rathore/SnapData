package com.example.snapdata.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Shield
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.theme.*

/**
 * AUTH-01: Authentication Welcome Screen.
 * Visual entry point presenting clear options: Sign In, Create Account, or Continue as Guest.
 */
@Composable
fun AuthWelcomeScreen(
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onGuestClick: () -> Unit,
    onBackToLanding: () -> Unit,
    isFirebaseConfigured: Boolean = false,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Official SnapData Brand Header
        Box(modifier = Modifier.fillMaxWidth()) {
            SnapDataLogo(
                variant = SnapDataLogoVariant.FULL_HORIZONTAL,
                iconSize = 36.dp,
                wordmarkSize = 22.sp,
                taglineSize = 9.sp,
                isDarkBackground = false,
                showTagline = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Headline & Supporting Copy
        Text(
            text = "Welcome to\nSnapData",
            style = MaterialTheme.typography.displayMedium,
            color = SnapDataBlack,
            fontWeight = FontWeight.Bold,
            lineHeight = 44.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Turn documents into structured data.",
            style = MaterialTheme.typography.titleMedium,
            color = SnapDataRed,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Scan, extract, review and export — with an offline-first workflow designed to keep your data private.",
            style = MaterialTheme.typography.bodyLarge,
            color = SnapDataBlack.copy(alpha = 0.75f),
            lineHeight = 24.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Firebase Configuration Status / Notice
        if (!isFirebaseConfigured) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("welcome_unconfigured_firebase_notice"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFF1D4ED8),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Offline Mode Ready",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = "Firebase is unconfigured. You can skip sign-in and jump straight to Home as a guest.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF1E40AF)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Privacy Guarantee Badge
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("privacy_guarantee_badge"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SnapDataBlack,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Zero Cloud Document Upload",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SnapDataBlack
                    )
                    Text(
                        text = "Authentication manages user identity only. OCR and AI document extraction run locally on your device.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SnapDataBlack.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // When Firebase is not configured, prioritize Guest access
        if (!isFirebaseConfigured) {
            SnapDataPrimaryButton(
                text = "Continue to Home as Guest",
                onClick = onGuestClick,
                testTag = "welcome_primary_guest_btn"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SnapDataSecondaryButton(
                text = "Sign In",
                onClick = onSignInClick,
                testTag = "welcome_sign_in_btn"
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onCreateAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("welcome_create_account_btn"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = "Create Account",
                    color = SnapDataBlack,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        } else {
            // Standard Cloud Flow
            SnapDataPrimaryButton(
                text = "Sign In",
                onClick = onSignInClick,
                testTag = "welcome_sign_in_btn"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SnapDataSecondaryButton(
                text = "Create Account",
                onClick = onCreateAccountClick,
                testTag = "welcome_create_account_btn"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onGuestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("welcome_guest_btn")
            ) {
                Text(
                    text = "Continue as Guest (Offline Mode)",
                    color = SnapDataBlack.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onBackToLanding,
            modifier = Modifier.testTag("welcome_back_to_landing_btn")
        ) {
            Text(
                text = "← Back to Overview",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
