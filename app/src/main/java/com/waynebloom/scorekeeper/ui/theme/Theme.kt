package com.waynebloom.scorekeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.Typography

private val LightColorPalette = lightColors(
    primary = rust600,
    secondary = gray800,
    background = taupe100,
    surface = gray400,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = taupe800,
    onSurface = taupe800
)

private val DarkColorPalette = darkColors(
    primary = rust300,
    secondary = gray700,
    background = gray800,
    surface = gray700,
    onPrimary = gray800,
    onSecondary = Color.White,
    onBackground = taupe100,
    onSurface = gray400
)

@Composable
fun MedianMeepleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    shapes: Shapes = MedianMeepleShapes,
    typography: Typography = MedianMeepleTypography,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        shapes = shapes,
        typography = typography,
        content = content
    )
}