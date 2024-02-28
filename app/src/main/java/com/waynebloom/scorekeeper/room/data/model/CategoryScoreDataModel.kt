package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Subscore",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDataModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subscore_title_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerDataModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("player_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_id"]),
        Index(value = ["subscore_title_id"])
    ]
)
data class CategoryScoreDataModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "subscore_title_id")
    var categoryId: Long = 0,

    @ColumnInfo(name = "player_id")
    var playerId: Long = 0,

    var value: String = "0"
)
