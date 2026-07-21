package com.rork.rockscout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RockScoutColors = lightColorScheme(
    primary = Citrine,
    onPrimary = Ink,
    primaryContainer = CitrineSoft,
    onPrimaryContainer = Ink,
    secondary = Aqua,
    onSecondary = Ink,
    secondaryContainer = AquaDeep,
    onSecondaryContainer = Color.White,
    tertiary = Amethyst,
    onTertiary = Ink,
    background = Obsidian,
    onBackground = DarkTextHigh,
    surface = Slate900,
    onSurface = TextHigh,
    surfaceVariant = Slate700,
    onSurfaceVariant = TextMid,
    surfaceContainer = Slate800,
    surfaceContainerHigh = Slate700,
    surfaceContainerHighest = Slate600,
    outline = StoneLine,
    outlineVariant = Slate600,
    error = Danger,
    onError = Color.White,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RockScoutColors,
        typography = RockScoutTypography,
        content = content
    )
}
