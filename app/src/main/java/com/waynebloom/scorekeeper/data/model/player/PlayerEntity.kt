package com.waynebloom.scorekeeper.data.model.player

import androidx.room.*
import com.waynebloom.scorekeeper.data.model.match.MatchEntity

@Entity(
    tableName = "Player",
    foreignKeys = [ForeignKey(
        entity = MatchEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("match_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["match_id"])]
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    var id: Long = 0,

    @ColumnInfo(name = "match_id")
    var matchId: Long = 0,

    var name: String = "",

    var score: String = "0",

    var position: Int = 0,

    @ColumnInfo(name = "show_detailed_score")
    var showDetailedScore: Boolean = false
)
