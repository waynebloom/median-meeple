package com.waynebloom.scorekeeper.data.model.player

import androidx.room.Embedded
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity

data class PlayerObject(
    @Embedded
    var entity: PlayerEntity = PlayerEntity(),

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = SubscoreEntity::class)
    var score: List<SubscoreEntity> = listOf()
) {
    fun getUncategorizedScoreRemainder() = entity.score - score.sumOf { it.value }
}