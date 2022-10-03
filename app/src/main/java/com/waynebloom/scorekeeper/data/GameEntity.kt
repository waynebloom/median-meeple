package com.waynebloom.scorekeeper.data

import androidx.annotation.NonNull
import androidx.room.*

val EMPTY_GAME_ENTITY = GameEntity(
    name = "Empty"
)

@Entity(tableName = "Game")
data class GameEntity(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Long = 0,

    @NonNull
    val name: String = "",

    @NonNull
    @ColumnInfo(defaultValue = "ORANGE")
    val color: String = "ORANGE"
)
