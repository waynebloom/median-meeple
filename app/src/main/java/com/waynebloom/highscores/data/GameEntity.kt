package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.*

val EMPTY_GAME = GameEntity(
    name = "Empty"
)

@Entity(tableName = "game")
data class GameEntity(

    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "name")
    val name: String = "",

    @ColumnInfo(name = "color", defaultValue = "RED")
    val color: String = GameColor.values().random().name
)
