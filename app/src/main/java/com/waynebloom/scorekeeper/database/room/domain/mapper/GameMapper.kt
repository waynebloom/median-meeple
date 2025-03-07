package com.waynebloom.scorekeeper.database.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.squareup.moshi.JsonAdapter
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.ext.toScoringMode
import javax.inject.Inject

class GameMapper @Inject constructor(
	private val matchDataMapper: MatchDataMapper,
	private val jsonAdapter: JsonAdapter<GameDataModel>,
) {

	fun toData(json: String): GameDataModel? {
		return jsonAdapter.fromJson(json)
	}

	fun toData(game: GameDomainModel) = game.let {
		GameDataModel(
			id = it.id,
			color = it.displayColorIndex,
			name = it.name.text,
			scoringMode = it.scoringMode.ordinal
		)
	}

	fun toDomain(game: GameDataModel): GameDomainModel {
		return GameDomainModel(
			id = game.id,
			displayColorIndex = game.color,
			name = TextFieldValue(game.name),
			scoringMode = game.scoringMode.toScoringMode()
		)
	}

	fun toDomain(games: List<GameDataModel>) = games.map { this.toDomain(it) }

	fun toDomainWithRelations(gameData: GameDataRelationModel?): GameDomainModel? {
		if (gameData == null) return null
		val categoryDomainModels = gameData.categories
			.map {
				CategoryDomainModel(
					id = it.id,
					name = TextFieldValue(it.name),
					position = it.position
				)
			}
			.associateBy {
				it.id
			}

		return GameDomainModel(
			id = gameData.entity.id,
			categories = categoryDomainModels.values.toList(),
			displayColorIndex = gameData.entity.color,
			matches = gameData.matches.map {
				matchDataMapper.mapWithRelations(it, categoryDomainModels)
			},
			name = TextFieldValue(gameData.entity.name),
			scoringMode = gameData.entity.scoringMode.toScoringMode()
		)
	}

	fun toDomainWithRelations(games: List<GameDataRelationModel?>) = games.map {
		this.toDomainWithRelations(it)
	}
}
