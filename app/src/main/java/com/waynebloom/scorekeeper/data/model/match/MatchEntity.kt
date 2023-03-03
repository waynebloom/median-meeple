package com.waynebloom.scorekeeper.data.model.match

import androidx.room.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import java.util.*

@Entity(
    tableName = "Match",
    foreignKeys = [ForeignKey(
        entity = GameEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("game_owner_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["game_owner_id"])]
)
data class MatchEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    val id: Long = 0,

    @ColumnInfo(name = "game_owner_id")
    val gameId: Long = 0,

    @ColumnInfo(name = "time_modified")
    var timeModified: Long = Date().time,

    @ColumnInfo(name = "match_notes")
    var matchNotes: String = ""
)
