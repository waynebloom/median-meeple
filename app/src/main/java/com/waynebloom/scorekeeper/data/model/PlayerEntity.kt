package com.waynebloom.scorekeeper.data.model

import androidx.room.*

val EMPTY_PLAYER_ENTITY = PlayerEntity()

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

    // TODO make this non-nullable, default 0
    var score: Long? = null,

    var position: Int = 0,

    @ColumnInfo(name = "show_detailed_score")
    var showDetailedScore: Boolean = false
)
