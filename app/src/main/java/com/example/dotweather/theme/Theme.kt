package com.example.dotweather.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Developed by S. M. Mahmud Iqbal
private val NothingDarkColorScheme = darkColorScheme(
    primary = NothingWhite,
    onPrimary = NothingBlack,
    primaryContainer = NothingCardBg,
    onPrimaryContainer = NothingWhite,
    secondary = NothingRed,
    onSecondary = NothingWhite,
    background = NothingBlack,
    onBackground = NothingWhite,
    surface = NothingCardBg,
    onSurface = NothingWhite,
    surfaceVariant = NothingCardInner,
    onSurfaceVariant = NothingWhite70,
    outline = NothingCardBorder
)

private val NothingLightColorScheme = lightColorScheme(
    primary = NothingBlack,
    onPrimary = NothingWhite,
    primaryContainer = Color(0xFFF2F2F2),
    onPrimaryContainer = NothingBlack,
    secondary = NothingRed,
    onSecondary = NothingWhite,
    background = Color(0xFFF6F6F6),
    onBackground = NothingBlack,
    surface = Color(0xFFFFFFFF),
    onSurface = NothingBlack,
    surfaceVariant = Color(0xFFEBEBEB),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFDDDDDD)
)

@Composable
fun DotWeatherTheme(
    darkTheme: Boolean = true, // Default to Nothing OS signature true-black dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) NothingDarkColorScheme else NothingLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
