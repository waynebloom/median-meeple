package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.enums.ScoringMode

fun Int.toScoringMode() = ScoringMode.getModeByOrdinal(this)

fun Int.toRank(): String {
    val ordinals = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this) {
        11, 12, 13 -> "$this" + "th"
        else -> "$this" + ordinals[this % 10]
    }
}