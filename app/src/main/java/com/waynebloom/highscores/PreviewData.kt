package com.waynebloom.highscores

import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.Score

val PreviewGameData: List<Game> = listOf(
    Game("Carcassonne"),
    Game("Wingspan"),
    Game("Century: Golem Edition"),
    Game("Ticket to Ride: Rails & Sails Test Test Test"),
    Game("Mystic Vale"),
    Game("Mariposas"),
    Game("Azul: Queen's Garden"),
    Game("Catan"),
)

val PreviewScoreData: List<Score> = listOf(
    Score(
        name = "Wayne",
        score = 2,
        forGame = "Wingspan"
    ),
    Score(
        name = "Wayne",
        score = 20,
        forGame = "Carcassonne"
    ),
    Score(
        name = "Wayne",
        score = 200,
        forGame = "Wingspan"
    ),
    Score(
        name = "Wayne",
        score = 2000,
        forGame = "Mystic Vale"
    ),
)