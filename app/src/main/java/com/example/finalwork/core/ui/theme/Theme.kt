package com.example.finalwork.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,
    secondary = EarthSecondary,
    secondaryContainer = EarthSecondaryContainer,
    tertiary = SunTertiary,
    tertiaryContainer = SunTertiaryContainer,
    background = LightBackground,
    surface = LightSurface
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimaryContainer,
    onPrimary = GreenOnPrimaryContainer,
    primaryContainer = GreenPrimary,
    onPrimaryContainer = GreenOnPrimaryContainer,
    secondary = EarthSecondaryContainer,
    tertiary = SunTertiaryContainer,
    background = DarkBackground,
    surface = DarkSurface
)

/** 专注星球全局主题。成员 A（张恩鸣）维护。 */
@Composable
fun FocusPlanetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = FocusTypography,
        content = content
    )
}
