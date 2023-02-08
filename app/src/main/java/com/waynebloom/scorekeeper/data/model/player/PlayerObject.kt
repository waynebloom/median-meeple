package com.waynebloom.scorekeeper.data.model.player

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity

class PlayerObject(
    @Embedded
    var entity: PlayerEntity = PlayerEntity(),

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = SubscoreEntity::class)
    var score: List<SubscoreEntity> = listOf()
) {

    @Ignore
    val uncategorizedScore = entity.score - score.sumOf { it.value }
}