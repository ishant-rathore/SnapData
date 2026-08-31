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
 * AUTH-02: Sign In Screen.
 */
@Composable
fun SignInScreen(
    onSignIn: (email: String, password: CharArray) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false,
    authError: AppAuthError? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        onClearError()
        var isValid = true

        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            emailError = "Email address is required."
            isValid = false
        } else if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            emailError = "Please enter a valid email address."
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password is required."
            isValid = false
        } else {
            passwordError = null
        }

        if (isValid && !isLoading) {
            focusManager.clearFocus()
            onSignIn(trimmedEmail, password.toCharArray())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
    ) {
        SnapDataAuthHeader(
            title = "Welcome back.",
            subtitle = "Sign in to continue to SnapData.",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // General Auth Error Banner
            AnimatedVisibility(
                visible = authError != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                authError?.let { err ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .testTag("auth_error_banner"),
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
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Medium
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                testTag = "signin_email_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            SnapDataPasswordField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError != null) passwordError = null
                    if (authError != null) onClearError()
                },
                label = "Password",
                isError = passwordError != null,
                errorMessage = passwordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                testTag = "signin_password_input"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPasswordClick,
                    modifier = Modifier.testTag("signin_forgot_password_btn")
                ) {
                    Text(
                        text = "Forgot password?",
                        color = SnapDataBlack.copy(alpha = 0.75f),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Sign In CTA
            SnapDataPrimaryButton(
                text = "Sign In",
                onClick = { validateAndSubmit() },
                isLoading = isLoading,
                testTag = "signin_submit_btn"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up Bottom Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SnapDataBlack.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(
                    onClick = onCreateAccountClick,
                    modifier = Modifier.testTag("signin_to_signup_btn")
                ) {
                    Text(
                        text = "Create Account",
                        color = SnapDataRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
