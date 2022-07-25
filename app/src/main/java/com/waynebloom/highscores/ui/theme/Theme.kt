package com.waynebloom.highscores.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = rust600,
    secondary = gray900,
    background = taupe100,
    surface = Color.White.copy(alpha = .85f),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = taupe800,
    onSurface = gray900.copy(alpha = 0.8f)
)

private val DarkColorPalette = darkColors(
    primary = rust300,
    secondary = gray700,
    background = gray900,
    surface = gray700,
    onPrimary = gray900,
    onSecondary = Color.White,
    onBackground = taupe100,
    onSurface = Color.White
)

@Composable
fun HighScoresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        shapes = HighScoresShapes,
        content = content
    )
}