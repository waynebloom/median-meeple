package com.waynebloom.scorekeeper.data.model

import androidx.room.*
import com.waynebloom.scorekeeper.enums.ScoringMode

val EMPTY_GAME_ENTITY = GameEntity(
    id = -1,
    name = "Empty"
)

@Entity(tableName = "Game")
data class GameEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var color: String = "ORANGE",

    var name: String = "",

    @ColumnInfo(name = "scoring_mode", defaultValue = "1")
    var scoringMode: Int = ScoringMode.Descending.ordinal
)
