package com.waynebloom.highscores

import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Match
import com.waynebloom.highscores.data.Score


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
        scoreValue = 2
    ),
    Score(
        name = "Wayne",
        scoreValue = 20
    ),
    Score(
        name = "Wayne",
        scoreValue = 200
    )
)

val PreviewMatchData: List<Match> = listOf(
    Match(
        matchNotes = "Example notes 1"
    ),
    Match(
        matchNotes = "Example notes 2"
    ),
    Match(
        matchNotes = "Example notes 3"
    ),
)
