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
    primary = SnapDataRed,
    onPrimary = CardWhite,
    primaryContainer = SnapDataRedContainer,
    onPrimaryContainer = SnapDataRedDark,
    secondary = SnapDataBlack,
    onSecondary = CardWhite,
    secondaryContainer = SurfaceWarm,
    onSecondaryContainer = TextDark,
    tertiary = AccentGreen,
    onTertiary = CardWhite,
    tertiaryContainer = AccentGreenLight,
    onTertiaryContainer = Color(0xFF065F46),
    background = WarmCreamBackground,
    onBackground = TextDark,
    surface = CardWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceWarm,
    onSurfaceVariant = TextSecondary,
    outline = LightBorder,
    outlineVariant = SubtleBorder,
    error = AccentRed,
    onError = CardWhite,
    errorContainer = SnapDataRedLight,
    onErrorContainer = SnapDataRedDark
)

private val DarkColorScheme = darkColorScheme(
    primary = SnapDataRed,
    onPrimary = CardWhite,
    primaryContainer = Color(0xFF42090B),
    onPrimaryContainer = Color(0xFFFFD9D9),
    secondary = Color(0xFFE2DDD5),
    onSecondary = Color(0xFF161513),
    secondaryContainer = SurfaceVariantDark,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AccentGreen,
    onTertiary = CardWhite,
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = Color(0xFF45423C),
    outlineVariant = Color(0xFF33302B),
    error = AccentRed,
    onError = CardWhite,
    errorContainer = Color(0xFF5C1113),
    onErrorContainer = Color(0xFFFFD9D9)
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
