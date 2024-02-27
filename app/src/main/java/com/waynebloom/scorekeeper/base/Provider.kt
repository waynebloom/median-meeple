package com.waynebloom.scorekeeper.base

import androidx.compose.runtime.compositionLocalOf
import com.waynebloom.scorekeeper.theme.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.theme.color.GameColors

val LocalCustomThemeColors = compositionLocalOf { DarkThemeGameColors() as GameColors }
