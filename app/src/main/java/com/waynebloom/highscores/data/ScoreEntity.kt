package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

val EMPTY_SCORE = ScoreEntity()

@Entity(
    tableName = "score",
    foreignKeys = [ForeignKey(
        entity = MatchEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("match_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class ScoreEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "match_id")
    val matchId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "score")
    var scoreValue: Long? = null
)
