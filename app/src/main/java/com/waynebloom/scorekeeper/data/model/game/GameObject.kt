package com.waynebloom.scorekeeper.data.model.game

import androidx.room.Embedded
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity

data class GameObject(
    @Embedded
    var entity: GameEntity = GameEntity(),

    @Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchEntity::class)
    var matches: List<MatchObject> = listOf(),

    @Relation(parentColumn = "id", entityColumn = "game_id", entity = SubscoreTitleEntity::class)
    var subscoreTitles: List<SubscoreTitleEntity> = listOf()
)
