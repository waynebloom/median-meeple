package com.waynebloom.scorekeeper.data

import androidx.room.Embedded

data class ScoreObject(
    @Embedded
    var entity: ScoreEntity = EMPTY_SCORE_ENTITY,

    var action: DatabaseAction = DatabaseAction.NO_ACTION
)