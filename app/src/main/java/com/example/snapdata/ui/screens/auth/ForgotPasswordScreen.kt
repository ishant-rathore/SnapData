package com.example.snapdata.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.auth.domain.AppAuthError
import com.example.snapdata.ui.theme.*

/**
 * AUTH-04: Forgot Password Screen.
 */
@Composable
fun ForgotPasswordScreen(
    onSendResetLink: (email: String) -> Unit,
    onBackToSignIn: () -> Unit,
    isLoading: Boolean = false,
    authError: AppAuthError? = null,
    isResetSent: Boolean = false,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        onClearError()
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            emailError = "Email address is required."
        } else if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            emailError = "Please enter a valid email address."
        } else {
            emailError = null
            focusManager.clearFocus()
            onSendResetLink(trimmedEmail)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
    ) {
        SnapDataAuthHeader(
            title = "Reset your password",
            subtitle = "Enter your email address and we'll send you instructions to recover access.",
            onBackClick = onBackToSignIn
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            if (isResetSent) {
                // Success Confirmation Card (anti-enumeration safe)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reset_success_card"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Check your email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SnapDataBlack
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "If an account matches this email, instructions to reset your password have been sent.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SnapDataBlack.copy(alpha = 0.75f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                SnapDataPrimaryButton(
                    text = "Return to Sign In",
                    onClick = onBackToSignIn,
                    testTag = "reset_return_to_signin_btn"
                )
            } else {
                // Error Banner
                AnimatedVisibility(
                    visible = authError != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    authError?.let { err ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = SnapDataRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err.userMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    }
                }

                // Email Input
                SnapDataTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                        if (authError != null) onClearError()
                    },
                    label = "Email Address",
                    placeholder = "name@company.com",
                    leadingIcon = Icons.Default.Email,
                    isError = emailError != null,
                    errorMessage = emailError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                    testTag = "forgot_email_input"
                )

                Spacer(modifier = Modifier.height(24.dp))

                SnapDataPrimaryButton(
                    text = "Send Reset Link",
                    onClick = { validateAndSubmit() },
                    isLoading = isLoading,
                    testTag = "forgot_submit_btn"
                )

                Spacer(modifier = Modifier.height(16.dp))

                SnapDataSecondaryButton(
                    text = "Cancel & Back to Sign In",
                    onClick = onBackToSignIn,
                    testTag = "forgot_cancel_btn"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
