package com.waynebloom.scorekeeper.database.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
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

	@SerialName("match_id")
	@ColumnInfo(name = "match_id")
	var matchID: Long = 0,

	var name: String = "",

	var position: Int = 0,
)

class PlayerDataRelationModel(
	@Embedded
	var entity: PlayerDataModel = PlayerDataModel(),

	@Relation(parentColumn = "id", entityColumn = "player_id", entity = ScoreDataModel::class)
	var score: List<ScoreDataModel> = listOf()
)
