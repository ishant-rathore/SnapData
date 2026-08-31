package com.example.snapdata.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
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
import com.example.snapdata.auth.data.ProductionAuthProvider
import com.example.snapdata.auth.domain.AppAuthError
import com.example.snapdata.ui.theme.*

/**
 * AUTH-03: Sign Up Screen.
 */
@Composable
fun SignUpScreen(
    onSignUp: (fullName: String, email: String, password: CharArray) -> Unit,
    onSignInClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false,
    authError: AppAuthError? = null,
    onClearError: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var termsError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    fun validateAndSubmit() {
        onClearError()
        var isValid = true

        val trimmedName = fullName.trim()
        if (trimmedName.isBlank()) {
            nameError = "Full name is required."
            isValid = false
        } else {
            nameError = null
        }

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

        val passwordChars = password.toCharArray()
        if (password.isBlank()) {
            passwordError = "Password is required."
            isValid = false
        } else if (!ProductionAuthProvider.isStrongPassword(passwordChars)) {
            passwordError = "Password must meet all security requirements below."
            isValid = false
        } else {
            passwordError = null
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Please confirm your password."
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Passwords do not match."
            isValid = false
        } else {
            confirmPasswordError = null
        }

        if (!termsAccepted) {
            termsError = "Please accept the terms to proceed."
            isValid = false
        } else {
            termsError = null
        }

        if (isValid && !isLoading) {
            focusManager.clearFocus()
            onSignUp(trimmedName, trimmedEmail, passwordChars)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmOffWhite)
            .verticalScroll(scrollState)
    ) {
        SnapDataAuthHeader(
            title = "Create your SnapData account.",
            subtitle = "Set up your credentials to securely manage your workspace.",
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

            // Full Name Input
            SnapDataTextField(
                value = fullName,
                onValueChange = {
                    fullName = it
                    if (nameError != null) nameError = null
                    if (authError != null) onClearError()
                },
                label = "Full Name",
                placeholder = "Alex Vance",
                leadingIcon = Icons.Default.Person,
                isError = nameError != null,
                errorMessage = nameError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                testTag = "signup_name_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                testTag = "signup_email_input"
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                testTag = "signup_password_input"
            )

            if (password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementsChecklist(password = password.toCharArray())
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Input
            SnapDataPasswordField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (confirmPasswordError != null) confirmPasswordError = null
                    if (authError != null) onClearError()
                },
                label = "Confirm Password",
                placeholder = "••••••••",
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { validateAndSubmit() }),
                testTag = "signup_confirm_password_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms & Privacy Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { termsAccepted = !termsAccepted },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SnapDataBlack,
                        checkmarkColor = WarmOffWhite
                    ),
                    modifier = Modifier.testTag("signup_terms_checkbox")
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy. All document processing remains offline on device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = SnapDataBlack.copy(alpha = 0.8f)
                )
            }

            if (termsError != null) {
                Text(
                    text = termsError.orEmpty(),
                    color = SnapDataRed,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            SnapDataPrimaryButton(
                text = "Create Account",
                onClick = { validateAndSubmit() },
                isLoading = isLoading,
                testTag = "signup_submit_btn"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SnapDataBlack.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                TextButton(
                    onClick = onSignInClick,
                    modifier = Modifier.testTag("signup_to_signin_btn")
                ) {
                    Text(
                        text = "Sign In",
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
