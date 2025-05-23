package com.waynebloom.scorekeeper.database.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.waynebloom.scorekeeper.common.ScoringMode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "Game")
data class GameDataModel(

	@PrimaryKey(autoGenerate = true)
	var id: Long = 0,

	var color: Int = 0,

	var name: String = "",

	@SerialName("scoring_mode")
	@ColumnInfo(name = "scoring_mode", defaultValue = "1")
	var scoringMode: Int = ScoringMode.Descending.ordinal,

	@SerialName("is_favorite")
	@ColumnInfo(name = "is_favorite", defaultValue = "false")
	var isFavorite: Boolean = false,
)

data class GameDataRelationModel(
	@Embedded
	var entity: GameDataModel = GameDataModel(),

	@Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchDataModel::class)
	var matches: List<MatchDataRelationModel> = listOf(),

	@Relation(parentColumn = "id", entityColumn = "game_id", entity = CategoryDataModel::class)
	var categories: List<CategoryDataModel> = listOf(),
)
