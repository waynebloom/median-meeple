package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.enums.ScoringMode

fun Int.toScoringMode() = ScoringMode.getModeByOrdinal(this)