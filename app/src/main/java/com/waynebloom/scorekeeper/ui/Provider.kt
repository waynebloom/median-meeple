package com.waynebloom.scorekeeper.ui

import androidx.compose.runtime.compositionLocalOf
import com.waynebloom.scorekeeper.ui.theme.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.ui.theme.color.GameColors

val LocalCustomThemeColors = compositionLocalOf { DarkThemeGameColors() as GameColors }
