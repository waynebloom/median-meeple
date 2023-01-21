package com.waynebloom.scorekeeper.data.model

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.EMPTY_PLAYER_ENTITY
import com.waynebloom.scorekeeper.data.model.PlayerEntity
import com.waynebloom.scorekeeper.data.model.SubscoreEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction

val EMPTY_PLAYER_OBJECT = PlayerObject()

data class PlayerObject(
    @Embedded
    var entity: PlayerEntity = EMPTY_PLAYER_ENTITY,

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = SubscoreEntity::class)
    var score: List<SubscoreEntity> = listOf()
) {
    fun getUncategorizedScoreRemainder() = (entity.score ?: 0) - score.sumOf { it.value ?: 0 }
}