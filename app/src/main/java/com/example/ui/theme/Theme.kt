package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BloodPrimaryNight,
    onPrimary = Color.Black,
    primaryContainer = BloodPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = BloodSecondary,
    background = BloodBackgroundNight,
    surface = BloodSurfaceNight,
    onBackground = BloodTextPrimaryNight,
    onSurface = BloodTextPrimaryNight,
    error = Color(0xFFCF6679),
    outline = Color(0xFF424242)
)

private val LightColorScheme = lightColorScheme(
    primary = BloodPrimary,
    onPrimary = Color.White,
    primaryContainer = BloodPrimaryContainer,
    onPrimaryContainer = BloodOnPrimaryContainer,
    secondary = BloodSecondary,
    onSecondary = Color.White,
    secondaryContainer = BloodSecondaryContainer,
    background = BloodBackgroundLight,
    surface = BloodSurfaceLight,
    surfaceVariant = BloodSurfaceVariantLight,
    onBackground = BloodTextPrimary,
    onSurface = BloodTextPrimary,
    onSurfaceVariant = BloodTextSecondary,
    outline = BloodOutline,
    error = BloodPrimary
)

@Composable
fun BloodConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BloodConnectTheme(darkTheme = darkTheme, content = content)
}
