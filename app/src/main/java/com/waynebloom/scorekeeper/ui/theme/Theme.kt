package com.waynebloom.scorekeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.waynebloom.scorekeeper.data.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.data.color.GameColors

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
fun ScoreKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        shapes = ScorekeeperShapes,
        content = content
    )

    /*if (darkTheme) {  TODO: remove
        CustomThemePalette = listOf(
            deepOrange100,
            orange100,
            amber100,
            yellow100,
            lime100,
            lightGreen100,
            green100,
            teal100,
            cyan100,
            lightBlue100,
            blue100,
            indigo100,
            deepPurple100,
            purple100,
            pink100
        )
    }*/
}