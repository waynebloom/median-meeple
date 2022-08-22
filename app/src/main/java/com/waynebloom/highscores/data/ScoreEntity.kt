package com.waynebloom.highscores.data

import androidx.annotation.NonNull
import androidx.room.*
import java.util.*

@Entity(
    tableName = "Score",
    foreignKeys = [ForeignKey(
        entity = MatchEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("match_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["match_id"])]
)
data class ScoreEntity(
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    var id: Long = 0,

    @NonNull
    @ColumnInfo(name = "match_id")
    var matchId: Long = 0,

    @NonNull
    @ColumnInfo(name = "name")
    var name: String = "",

    @NonNull
    @ColumnInfo(name = "score")
    var scoreValue: Long? = null
) {
    @Ignore
    var action: DatabaseAction = DatabaseAction.NO_ACTION

    constructor(matchId: Long, name: String, scoreValue: Long?, action: DatabaseAction) : this() {
        this.matchId = matchId
        this.name = name
        this.scoreValue = scoreValue
        this.action = action
    }

    constructor(previousScore: ScoreEntity, action: DatabaseAction) : this() {
        this.matchId = previousScore.matchId
        this.name = previousScore.name
        this.scoreValue = previousScore.scoreValue
        this.action = action
    }
}
