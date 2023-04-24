package com.waynebloom.scorekeeper.data.model.game

import androidx.room.*
import com.waynebloom.scorekeeper.enums.ScoringMode

@Entity(tableName = "Game")
data class GameEntity(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var color: String = "ORANGE",

    var name: String = "",

    @ColumnInfo(name = "scoring_mode", defaultValue = "1")
    var scoringMode: Int = ScoringMode.Descending.ordinal
)