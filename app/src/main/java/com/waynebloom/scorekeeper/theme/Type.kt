package com.waynebloom.scorekeeper.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.waynebloom.scorekeeper.R

val MedianMeepleFont = FontFamily(
    Font(resId = R.font.dmsans_regular),
    Font(resId = R.font.dmsans_bold, weight = FontWeight.Bold),
    Font(resId = R.font.dmsans_medium, weight = FontWeight.Bold),
    Font(resId = R.font.dmsans_italic, style = FontStyle.Italic),
    Font(resId = R.font.dmsans_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resId = R.font.dmsans_medium_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
)

private val defaultTypography = Typography()
val MedianMeepleTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = MedianMeepleFont),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = MedianMeepleFont),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = MedianMeepleFont),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = MedianMeepleFont),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = MedianMeepleFont),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = MedianMeepleFont),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = MedianMeepleFont),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = MedianMeepleFont),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = MedianMeepleFont),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = MedianMeepleFont),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = MedianMeepleFont),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = MedianMeepleFont),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = MedianMeepleFont),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = MedianMeepleFont),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = MedianMeepleFont)
)