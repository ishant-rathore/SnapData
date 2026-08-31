package com.example.snapdata.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFFE0EDFF),
    onPrimaryContainer = PrimaryBlueDark,
    secondary = SecondaryCyan,
    onSecondary = Color(0xFF003642),
    secondaryContainer = Color(0xFFC8F5FF),
    onSecondaryContainer = Color(0xFF001F28),
    tertiary = TertiaryIndigo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE0E0FF),
    onTertiaryContainer = Color(0xFF1B198F),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = AccentRed,
    onError = Color.White,
    errorContainer = Color(0xFFFEE2E2),
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = SurfaceLight,
    primaryContainer = Color(0xFF003882),
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = SecondaryCyan,
    onSecondary = Color(0xFF003642),
    secondaryContainer = Color(0xFF004D61),
    onSecondaryContainer = Color(0xFFBCE9F5),
    tertiary = TertiaryIndigo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF373B8A),
    onTertiaryContainer = Color(0xFFE0E0FF),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155),
    error = AccentRed,
    onError = Color.White,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun SnapDataTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
