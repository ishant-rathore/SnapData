package com.example.snapdata.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.snapdata.ui.components.branding.SnapDataLogo
import com.example.snapdata.ui.components.branding.SnapDataLogoVariant
import com.example.snapdata.ui.theme.*

/**
 * Editorial, minimal typography header for SnapData authentication screens.
 */
@Composable
fun SnapDataAuthHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("auth_nav_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate Back",
                    tint = SnapDataBlack
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        SnapDataLogo(
            variant = SnapDataLogoVariant.FULL_HORIZONTAL,
            iconSize = 32.dp,
            wordmarkSize = 18.sp,
            taglineSize = 8.sp,
            isDarkBackground = false,
            showTagline = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = SnapDataBlack,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = SnapDataBlack.copy(alpha = 0.75f),
                lineHeight = 20.sp
            )
        }
    }
}

/**
 * Premium Outlined Text Field adhering to the Warm Off-White / Black editorial theme.
 */
@Composable
fun SnapDataTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    testTag: String = "auth_text_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = SnapDataBlack.copy(alpha = 0.85f),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            placeholder = { Text(placeholder, color = Color.Gray) },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = if (isError) SnapDataRed else SnapDataBlack.copy(alpha = 0.6f)) }
            },
            trailingIcon = trailingIcon,
            isError = isError,
            singleLine = singleLine,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = SnapDataBlack,
                unfocusedBorderColor = Color(0xFFD9D9D9),
                errorBorderColor = SnapDataRed,
                focusedTextColor = SnapDataBlack,
                unfocusedTextColor = SnapDataBlack,
                errorTextColor = SnapDataBlack
            )
        )

        AnimatedVisibility(
            visible = isError && !errorMessage.isNullOrBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage.orEmpty(),
                color = SnapDataRed,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * Specialized Password Input Field with animated visibility toggle.
 */
@Composable
fun SnapDataPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Password",
    modifier: Modifier = Modifier,
    placeholder: String = "••••••••",
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String = "auth_password_field"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    SnapDataTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Lock,
        trailingIcon = {
            IconButton(
                onClick = { passwordVisible = !passwordVisible },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("${testTag}_toggle")
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = SnapDataBlack.copy(alpha = 0.6f)
                )
            }
        },
        isError = isError,
        errorMessage = errorMessage,
        singleLine = true,
        keyboardOptions = keyboardOptions.copy(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = keyboardActions,
        testTag = testTag
    )
}

/**
 * Real-time password requirement checklist & strength visualizer.
 */
@Composable
fun PasswordRequirementsChecklist(
    password: CharArray,
    modifier: Modifier = Modifier
) {
    val lengthOk = password.size >= 8
    var hasUpper = false
    var hasLower = false
    var hasDigit = false
    var hasSpecial = false

    for (c in password) {
        when {
            c.isUpperCase() -> hasUpper = true
            c.isLowerCase() -> hasLower = true
            c.isDigit() -> hasDigit = true
            !c.isLetterOrDigit() -> hasSpecial = true
        }
    }

    val passedCount = listOf(lengthOk, hasUpper, hasLower, hasDigit, hasSpecial).count { it }
    val strengthLabel = when (passedCount) {
        5 -> "Strong"
        3, 4 -> "Medium"
        else -> "Weak"
    }
    val strengthColor = when (passedCount) {
        5 -> Color(0xFF10B981)
        3, 4 -> Color(0xFFF59E0B)
        else -> SnapDataRed
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Security",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = SnapDataBlack
            )
            Text(
                text = strengthLabel,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = strengthColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress bar for strength
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i <= passedCount) strengthColor else Color(0xFFE2E8F0))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        RequirementItem(text = "At least 8 characters", isMet = lengthOk)
        RequirementItem(text = "Contains uppercase & lowercase letters", isMet = hasUpper && hasLower)
        RequirementItem(text = "Contains at least 1 number", isMet = hasDigit)
        RequirementItem(text = "Contains at least 1 special character", isMet = hasSpecial)
    }
}

@Composable
private fun RequirementItem(text: String, isMet: Boolean) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) Color(0xFF10B981) else Color.Gray,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = if (isMet) SnapDataBlack else Color.Gray,
            fontSize = 12.sp
        )
    }
}

/**
 * Editorial High-Contrast Primary Button with loading state.
 */
@Composable
fun SnapDataPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = SnapDataBlack,
    contentColor: Color = WarmOffWhite,
    testTag: String = "auth_primary_btn"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f),
            disabledContentColor = contentColor.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Processing...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}

/**
 * Outlined Secondary Button.
 */
@Composable
fun SnapDataSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "auth_secondary_btn"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SnapDataBlack),
        border = ButtonDefaults.outlinedButtonBorder(enabled = enabled).copy(
            brush = androidx.compose.ui.graphics.SolidColor(SnapDataBlack)
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}
