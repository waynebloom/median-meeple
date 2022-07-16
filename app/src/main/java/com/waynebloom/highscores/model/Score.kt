package com.waynebloom.highscores.model

import java.util.*

data class Score(
    val id: UUID = UUID.randomUUID(),
    val forGame: String,
    var name: String = "",
    var score: Int = 0,
)
