package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "Player",
    foreignKeys = [ForeignKey(
        entity = MatchDataModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("match_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["match_id"])]
)
data class PlayerDataModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    var id: Long = 0,

    @ColumnInfo(name = "match_id")
    var matchId: Long = 0,

    var name: String = "",

    var position: Int = 0,
)

class PlayerDataRelationModel(
    @Embedded
    var entity: PlayerDataModel = PlayerDataModel(),

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = CategoryScoreDataModel::class)
    var score: List<CategoryScoreDataModel> = listOf()
)
