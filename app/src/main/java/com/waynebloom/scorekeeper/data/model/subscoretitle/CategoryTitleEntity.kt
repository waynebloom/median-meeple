package com.waynebloom.scorekeeper.data.model.subscoretitle

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.waynebloom.scorekeeper.data.model.game.GameEntity

@Entity(
    tableName = "SubscoreTitle",
    foreignKeys = [ForeignKey(
        entity = GameEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("game_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["game_id"])]
)
data class CategoryTitleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0,

    var position: Int = 0,

    var title: String = ""
)