package com.waynebloom.scorekeeper.data.model.subscoretitle

import androidx.room.*
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
data class SubscoreTitleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0,

    var position: Int = 0,

    var title: String = ""
)