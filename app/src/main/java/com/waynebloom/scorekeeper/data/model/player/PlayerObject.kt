package com.waynebloom.scorekeeper.data.model.player

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import java.math.BigDecimal

class PlayerObject(
    @Embedded
    var entity: PlayerEntity = PlayerEntity(),

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = SubscoreEntity::class)
    var score: List<SubscoreEntity> = listOf()
) {

    @Ignore
    fun getUncategorizedScore(): BigDecimal {
        val subscoreSum = score.sumOf { it.value.toBigDecimal() }
        val totalScoreBigDecimal = entity.score.toBigDecimal()
        return totalScoreBigDecimal - subscoreSum
    }
}