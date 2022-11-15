package com.waynebloom.scorekeeper.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.waynebloom.scorekeeper.R

val ScorekeeperFont = FontFamily(
    Font(resId = R.font.dmsans_regular),
    Font(resId = R.font.dmsans_bold, weight = FontWeight.Bold),
    Font(resId = R.font.dmsans_medium, weight = FontWeight.Bold),
    Font(resId = R.font.dmsans_italic, style = FontStyle.Italic),
    Font(resId = R.font.dmsans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resId = R.font.dmsans_medium_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

val ScorekeeperTypography = Typography(
    defaultFontFamily = ScorekeeperFont,
    button = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),
)