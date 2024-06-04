package com.waynebloom.scorekeeper.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.theme.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.theme.color.LightThemeGameColors
import com.waynebloom.scorekeeper.theme.color.charcoal700
import com.waynebloom.scorekeeper.theme.color.charcoal800
import com.waynebloom.scorekeeper.theme.color.rust300
import com.waynebloom.scorekeeper.theme.color.rust600
import com.waynebloom.scorekeeper.theme.color.taupe100
import com.waynebloom.scorekeeper.theme.color.taupe50

private val LightColorPalette = lightColors(
    primary = rust600,
    secondary = rust600,
    background = taupe50,
    surface = taupe100,
    onPrimary = taupe50,
    onSecondary = taupe50,
    onBackground = charcoal800,
    onSurface = charcoal800
)

private val DarkColorPalette = darkColors(
    primary = rust300,
    secondary = rust300,
    background = charcoal800,
    surface = charcoal700,
    onPrimary = charcoal800,
    onSecondary = charcoal800,
    onBackground = taupe100,
    onSurface = taupe100
)

@Composable
fun UserSelectedPrimaryColorTheme(primaryColor: Color, content: @Composable () -> Unit) {
    MedianMeepleTheme(primaryColor = primaryColor, content = content)
}

@Composable
fun MedianMeepleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = if (darkTheme) rust300 else rust600,
    shapes: Shapes = MedianMeepleShapes,
    typography: Typography = MedianMeepleTypography,
    content: @Composable () -> Unit
) {

    val customThemeColors = if (isSystemInDarkTheme()) DarkThemeGameColors() else LightThemeGameColors()
    CompositionLocalProvider(LocalCustomThemeColors provides customThemeColors) {

        val colors = if (darkTheme) {
            DarkColorPalette.copy(primary = primaryColor)
        } else {
            LightColorPalette.copy(primary = primaryColor)
        }

        MaterialTheme(
            colors = colors,
            shapes = shapes,
            typography = typography,
            content = content
        )
    }
}
