package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.sql.Date
import java.time.Instant
import java.util.*

val EMPTY_SCORE = Score()

@Entity
data class Score(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "game_owner_id")
    val gameOwnerId: String = "",

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "score")
    var score: Int = 0,

    @ColumnInfo(name = "time_modified")
    var timeModified: Long = Date().time
)
