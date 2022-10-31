package com.waynebloom.scorekeeper.data

import androidx.room.*
import com.waynebloom.scorekeeper.enums.ScoringMode

val EMPTY_GAME_ENTITY = GameEntity(
    name = "Empty"
)

@Entity(tableName = "Game")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val color: String = "ORANGE",

    val name: String = "",

    @ColumnInfo(name = "scoring_mode", defaultValue = "1")
    val scoringMode: Int = ScoringMode.Descending.ordinal
)
