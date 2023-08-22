package com.waynebloom.scorekeeper.ui

import androidx.compose.runtime.compositionLocalOf
import com.waynebloom.scorekeeper.ui.theme.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.ui.theme.color.GameColors

val LocalGameColors = compositionLocalOf { DarkThemeGameColors() as GameColors }
