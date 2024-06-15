package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Category",
    foreignKeys = [ForeignKey(
        entity = GameDataModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("game_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["game_id"])]
)
data class CategoryDataModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0,

    @ColumnInfo(name = "name")
    var name: String = "",

    var position: Int = 0,
)
