package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MonochromeDarkColorScheme = darkColorScheme(
    primary = AuraWhite,
    onPrimary = AuraBlack,
    primaryContainer = AuraSurfaceElevated,
    onPrimaryContainer = AuraWhite,
    secondary = AuraWhite,
    onSecondary = AuraBlack,
    secondaryContainer = AuraPillInactive,
    onSecondaryContainer = AuraOffWhite,
    background = AuraBlack,
    onBackground = AuraOffWhite,
    surface = AuraSurfaceDark,
    onSurface = AuraOffWhite,
    surfaceVariant = AuraCardDark,
    onSurfaceVariant = AuraTextMuted,
    outline = AuraCardBorder,
    outlineVariant = AuraPillInactiveBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Forced to true luxury monochrome OLED
    content: @Composable () -> Unit
) {
    val colorScheme = MonochromeDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
