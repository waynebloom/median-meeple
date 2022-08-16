package com.waynebloom.highscores.data

import androidx.room.Embedded
import androidx.room.Relation

data class MatchObject(
    @Embedded
    var match: MatchEntity = EMPTY_MATCH,

    @Relation(parentColumn = "id", entityColumn = "match_id")
    var scores: List<ScoreEntity> = listOf()
)
