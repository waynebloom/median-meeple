package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

val EMPTY_MATCH = MatchEntity()

@Entity(
    tableName = "match",
    foreignKeys = [ForeignKey(
        entity = GameEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("game_owner_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class MatchEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "game_owner_id")
    val gameOwnerId: String = "",

    @NonNull
    @ColumnInfo(name = "time_modified")
    var timeModified: Long = Date().time,

    @ColumnInfo(name = "match_notes")
    var matchNotes: String = "",

    @Relation(parentColumn = "id", entityColumn = "match_id")
    var scores: List<ScoreEntity> = listOf()
)
