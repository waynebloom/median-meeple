package com.waynebloom.scorekeeper.ui.theme.color

import androidx.compose.ui.graphics.Color

abstract class GameColors {
    private companion object {
        const val DeepOrange = "DEEP_ORANGE"
        const val Orange = "ORANGE"
        const val Amber = "AMBER"
        const val Yellow = "YELLOW"
        const val Lime = "LIME"
        const val LightGreen = "LIGHT_GREEN"
        const val Green = "GREEN"
        const val Teal = "TEAL"
        const val Cyan = "CYAN"
        const val LightBlue = "LIGHT_BLUE"
        const val Blue = "BLUE"
        const val Indigo = "INDIGO"
        const val DeepPurple = "DEEP_PURPLE"
        const val Purple = "PURPLE"
        const val Pink = "PINK"
    }

    abstract val deepOrange: Color
    abstract val orange: Color
    abstract val amber: Color
    abstract val yellow: Color
    abstract val lime: Color
    abstract val lightGreen: Color
    abstract val green: Color
    abstract val teal: Color
    abstract val cyan: Color
    abstract val lightBlue: Color
    abstract val blue: Color
    abstract val indigo: Color
    abstract val deepPurple: Color
    abstract val purple: Color
    abstract val pink: Color

    fun getColorsAsKeyList(): List<String> {
        return listOf(
            DeepOrange,
            Orange,
            Amber,
            Yellow,
            Lime,
            LightGreen,
            Green,
            Teal,
            Cyan,
            LightBlue,
            Blue,
            Indigo,
            DeepPurple,
            Purple,
            Pink
        )
    }

    fun getColorByKey(key: String): Color {
        return when(key) {
            DeepOrange -> deepOrange
            Orange -> orange
            Amber -> amber
            Yellow -> yellow
            Lime -> lime
            LightGreen -> lightGreen
            Green -> green
            Teal -> teal
            Cyan -> cyan
            LightBlue -> lightBlue
            Blue -> blue
            Indigo -> indigo
            DeepPurple -> deepPurple
            Purple -> purple
            Pink -> pink
            else -> deepOrange
        }
    }
}