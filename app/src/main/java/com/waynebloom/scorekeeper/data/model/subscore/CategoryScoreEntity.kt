package com.waynebloom.scorekeeper.data.model.subscore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity

@Entity(
    tableName = "Subscore",
    foreignKeys = [
        ForeignKey(
            entity = CategoryTitleEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subscore_title_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
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
data class CategoryScoreEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "subscore_title_id")
    var categoryTitleId: Long = 0,

    @ColumnInfo(name = "player_id")
    var playerId: Long = 0,

    var value: String = "0"
)

