package com.waynebloom.scorekeeper.data

import androidx.room.Embedded
import androidx.room.Relation

val EMPTY_MATCH_OBJECT = MatchObject()

data class MatchObject(
    @Embedded
    var entity: MatchEntity = EMPTY_MATCH_ENTITY,

    @Relation(parentColumn = "id", entityColumn = "match_id", entity = ScoreEntity::class)
    var scores: List<ScoreEntity> = listOf()
)
