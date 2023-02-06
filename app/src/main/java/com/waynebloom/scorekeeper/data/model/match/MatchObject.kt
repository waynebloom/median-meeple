package com.waynebloom.scorekeeper.data.model.match

import androidx.room.Embedded
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject

data class MatchObject(
    @Embedded
    var entity: MatchEntity = MatchEntity(),

    @Relation(parentColumn = "id", entityColumn = "match_id", entity = PlayerEntity::class)
    var players: List<PlayerObject> = listOf()
)
