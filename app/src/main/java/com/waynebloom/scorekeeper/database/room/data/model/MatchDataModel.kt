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
import java.time.Instant

@Serializable
@Entity(
	tableName = "Match",
	foreignKeys = [ForeignKey(
		entity = GameDataModel::class,
		parentColumns = arrayOf("id"),
		childColumns = arrayOf("game_owner_id"),
		onDelete = ForeignKey.CASCADE,
		onUpdate = ForeignKey.CASCADE
	)],
	indices = [Index(value = ["game_owner_id"])]
)
data class MatchDataModel(

	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,

	@SerialName("game_id")
	@ColumnInfo(name = "game_owner_id")
	val gameId: Long = 0,

	@ColumnInfo(name = "match_notes")
	var notes: String = "",

	@SerialName("date_millis")
	@ColumnInfo(name = "date_millis")
	var dateMillis: Long = Instant.now().toEpochMilli(),

	@ColumnInfo(defaultValue = "")
	var location: String = "",
)

data class MatchDataRelationModel(
	@Embedded
	var entity: MatchDataModel = MatchDataModel(),

	@Relation(parentColumn = "id", entityColumn = "match_id", entity = PlayerDataModel::class)
	var players: List<PlayerDataRelationModel> = listOf()
)
