package com.waynebloom.scorekeeper.data

import androidx.annotation.NonNull
import androidx.room.*

val EMPTY_SCORE_ENTITY = ScoreEntity()

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

    constructor(
        previousScore: ScoreEntity,
        action: DatabaseAction
    ) : this(
        previousScore.id,
        previousScore.matchId,
        previousScore.name,
        previousScore.scoreValue
    ) {
        this.action = action
    }

    fun copy(
        id: Long = this.id,
        matchId: Long = this.matchId,
        name: String = this.name,
        scoreValue: Long? = this.scoreValue,
        action: DatabaseAction = this.action
    ): ScoreEntity {
        val newEntity = ScoreEntity(
            id = id,
            matchId = matchId,
            name = name,
            scoreValue = scoreValue
        )
        newEntity.action = action
        return newEntity
    }
}
