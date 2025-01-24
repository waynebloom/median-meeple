package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Expansion",
    foreignKeys = [
        ForeignKey(
            entity = GameDataModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("game_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ]
)
data class Expansion(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "game_id")
    val gameId: Long,

    val name: String,
    val default: Boolean,
)
