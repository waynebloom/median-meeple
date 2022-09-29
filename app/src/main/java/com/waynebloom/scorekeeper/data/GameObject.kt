package com.waynebloom.scorekeeper.data

import androidx.room.Embedded
import androidx.room.Relation

val EMPTY_GAME_OBJECT = GameObject()

data class GameObject(
    @Embedded
    var entity: GameEntity = EMPTY_GAME_ENTITY,

    @Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchEntity::class)
    var matches: List<MatchObject> = listOf()
)
