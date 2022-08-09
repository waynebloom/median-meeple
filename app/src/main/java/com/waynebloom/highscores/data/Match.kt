package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.waynebloom.highscores.GamesViewModel
import java.util.*

val EMPTY_MATCH = Match()

@Entity
data class Match(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @NonNull
    @ColumnInfo(name = "game_owner_id")
    val gameOwnerId: String = "",

    @NonNull
    @ColumnInfo(name = "time_modified")
    var timeModified: Long = Date().time,

    @ColumnInfo(name = "match_notes")
    var matchNotes: String = ""
) {
    @Ignore
    var scores: List<Score> = listOf()
}
