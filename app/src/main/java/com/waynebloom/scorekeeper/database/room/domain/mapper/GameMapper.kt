package com.waynebloom.scorekeeper.database.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.util.ext.toScoringMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

class GameMapper @Inject constructor(
	private val matchMapper: MatchMapper,
) {

	fun toData(json: JsonObject): GameDataModel {
		return Json.decodeFromJsonElement(json)
	}

	fun toData(game: GameDomainModel) = game.let {
		GameDataModel(
			id = it.id.let { id -> if (id == -1L) 0 else id },
			color = it.displayColorIndex,
			name = it.name.text,
			scoringMode = it.scoringMode.ordinal,
			isFavorite = it.isFavorite,
		)
	}

	fun toDomain(game: GameDataModel) = game.let {
		GameDomainModel(
			id = it.id,
			displayColorIndex = it.color,
			name = TextFieldValue(it.name),
			scoringMode = it.scoringMode.toScoringMode(),
			isFavorite = it.isFavorite,
		)
	}

	fun toDomainOrNull(game: GameDataModel?) = game.let {
		if (it != null) {
			GameDomainModel(
				id = it.id,
				displayColorIndex = it.color,
				name = TextFieldValue(it.name),
				scoringMode = it.scoringMode.toScoringMode(),
				isFavorite = it.isFavorite,
			)
		} else null
	}

	fun toDomain(games: List<GameDataModel>) = games.mapNotNull { this.toDomain(it) }

	fun toDomainWithRelations(game: GameDataRelationModel?): GameDomainModel? {
		if (game == null) return null
		val categories = game.categories
			.map {
				CategoryDomainModel(
					id = it.id,
					gameID = it.gameID,
					name = TextFieldValue(it.name),
					position = it.position
				)
			}
			.associateBy {
				it.id
			}

		return GameDomainModel(
			id = game.entity.id,
			categories = categories.values.toList(),
			displayColorIndex = game.entity.color,
			matches = matchMapper.toDomainWithRelations(game.matches, categories),
			name = TextFieldValue(game.entity.name),
			scoringMode = game.entity.scoringMode.toScoringMode()
		)
	}

	fun toDomainWithRelations(games: List<GameDataRelationModel?>) = games.map {
		this.toDomainWithRelations(it)
	}
}
