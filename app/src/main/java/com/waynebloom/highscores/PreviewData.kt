package com.waynebloom.highscores

import com.waynebloom.highscores.data.GameEntity
import com.waynebloom.highscores.data.MatchEntity
import com.waynebloom.highscores.data.ScoreEntity


val PreviewGameData: List<GameEntity> = listOf(
    GameEntity(name = "Carcassonne"),
    GameEntity(name = "Wingspan"),
    GameEntity(name = "Century: Golem Edition"),
    GameEntity(name = "Ticket to Ride: Rails & Sails Test Test Test"),
    GameEntity(name = "Mystic Vale"),
    GameEntity(name = "Mariposas"),
    GameEntity(name = "Azul: Queen's Garden"),
    GameEntity(name = "Catan"),
)

val PreviewScoreData: List<ScoreEntity> = listOf(
    ScoreEntity(
        name = "Wayne",
        scoreValue = 2
    ),
    ScoreEntity(
        name = "Wayne",
        scoreValue = 20
    ),
    ScoreEntity(
        name = "Wayne",
        scoreValue = 200
    )
)

val PreviewMatchData: List<MatchEntity> = listOf(
    MatchEntity(
        matchNotes = "Example notes 1"
    ),
    MatchEntity(
        matchNotes = "Example notes 2"
    ),
    MatchEntity(
        matchNotes = "Example notes 3"
    ),
)
