package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

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
    @ColumnInfo(defaultValue = "RED")
    val color: String = GameColor.values().random().name
)
