package com.example.flora1.core.presentation.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val lightColorScheme = lightColorScheme(
    primary = FloraRed,
    onPrimary = FloraWhite, // Primary background, white on top of that
    primaryContainer = FloraRed60, // Will be used as alpha version of primary
    secondary = FloraWhite70,
    tertiary = FloraGray,
    background = FloraWhiteBackground,
    onBackground = FloraBlack, // Contrast to background
    surface = FloraWhiteBackground, // background on top of actual background, like dialogs
    onSurface = FloraBlack,
    surfaceTint = Color(0xFFE4E3E3), // For Calendar surface,
    surfaceVariant = Color(0xFFEFEEF0),
    onSurfaceVariant = FloraBlack,
)

val darkColorScheme = darkColorScheme(
    primary = FloraRed,
    onPrimary = FloraWhite,
    primaryContainer = FloraRed60,
    secondary = FloraWhite70,
    tertiary = FloraGray,
    background = FloraBlack,
    onBackground = FloraWhite, // Contrast to background
    surface = FloraDarkGray,
    surfaceTint = Color(0xFF2C2C2C), // For Calendar surface
    surfaceVariant = FloraDarkSurfaceVariant,
    onSurfaceVariant = FloraWhiteBackground,
)

@Composable
fun Flora1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    val view = LocalView.current

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    }
}
