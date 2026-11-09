package com.treblenote.scout.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ScoutColorScheme = lightColorScheme(
    primary = PinePrimary,
    onPrimary = SplashPaper,
    primaryContainer = PineBright,
    onPrimaryContainer = SplashPaper,
    secondary = SageSecondary,
    onSecondary = ForestText,
    secondaryContainer = SageSurface,
    onSecondaryContainer = ForestText,
    tertiary = TrailAccent,
    onTertiary = SplashPaper,
    background = PaperMoss,
    onBackground = ForestText,
    surface = CardPaper,
    onSurface = ForestText,
    surfaceVariant = SageSurface,
    onSurfaceVariant = PinePrimary,
    outline = SageSecondary,
    outlineVariant = ForestTextMuted,
    error = TrailAccent,
    onError = SplashPaper
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ScoutColorScheme,
        typography = ScoutTypography,
        content = content
    )
}
