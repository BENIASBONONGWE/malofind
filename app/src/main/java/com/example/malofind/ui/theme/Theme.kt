package com.example.malofind.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MaloFindLightColorScheme = lightColorScheme(

    primary = MaloFindGreen,
    onPrimary = MaloFindTextOnGreen,

    primaryContainer = MaloFindDarkGreen,
    onPrimaryContainer = MaloFindTextOnGreen,

    secondary = MaloFindGold,
    onSecondary = MaloFindTextPrimary,

    secondaryContainer = MaloFindGold,
    onSecondaryContainer = MaloFindTextPrimary,

    background = MaloFindBackground,
    onBackground = MaloFindTextPrimary,

    surface = MaloFindSurface,
    onSurface = MaloFindTextPrimary,

    surfaceVariant = MaloFindCardBorder,
    onSurfaceVariant = MaloFindTextSecondary,

    outline = MaloFindCardBorder,

    error = MaloFindError,
    onError = MaloFindSurface,

    errorContainer = MaloFindError,
    onErrorContainer = MaloFindSurface
)

@Composable
fun MaloFindTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MaloFindLightColorScheme,
        typography = MaloFindTypography,
        content = content
    )
}