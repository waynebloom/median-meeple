package com.waynebloom.scorekeeper.data

import androidx.room.*

val EMPTY_GAME_ENTITY = GameEntity(
    name = "Empty"
)

@Entity(tableName = "Game")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Long = 0,

    val name: String = "",

    @ColumnInfo(defaultValue = "ORANGE")
    val color: String = "ORANGE"
)
