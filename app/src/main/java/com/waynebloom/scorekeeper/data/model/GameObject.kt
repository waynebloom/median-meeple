package com.waynebloom.scorekeeper.data.model

import androidx.room.Embedded
import androidx.room.Relation

val EMPTY_GAME_OBJECT = GameObject(
    entity = EMPTY_GAME_ENTITY
)

data class GameObject(
    @Embedded
    var entity: GameEntity = GameEntity(),

    @Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchEntity::class)
    var matches: List<MatchObject> = listOf(),

    @Relation(parentColumn = "id", entityColumn = "game_id", entity = SubscoreTitleEntity::class)
    var subscoreTitles: List<SubscoreTitleEntity> = listOf()
)
