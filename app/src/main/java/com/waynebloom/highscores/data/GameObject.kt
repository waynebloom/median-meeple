package com.waynebloom.highscores.data

import androidx.room.Embedded
import androidx.room.Relation

data class GameObject(
    @Embedded
    var game: GameEntity = EMPTY_GAME,

    @Relation(parentColumn = "id", entityColumn = "game_owner_id")
    var matches: List<MatchEntity> = listOf()
)
