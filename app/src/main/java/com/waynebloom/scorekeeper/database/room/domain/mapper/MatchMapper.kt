package com.waynebloom.scorekeeper.database.room.domain.mapper

import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

class MatchMapper @Inject constructor(
	private val playerMapper: PlayerMapper,
) {

	fun toData(json: JsonObject): MatchDataModel {
		return Json.decodeFromJsonElement(json)
	}

	fun toData(match: MatchDomainModel) = match.let {
		MatchDataModel(
			id = it.id,
			gameId = it.gameId,
			notes = it.notes,
			dateMillis = it.dateMillis,
			location = it.location,
		)
	}

	fun toDomain(match: MatchDataModel) = match.let {
		MatchDomainModel(
			id = it.id,
			notes = it.notes,
			dateMillis = it.dateMillis,
			location = it.location,
		)
	}

	fun toDomain(matches: List<MatchDataModel>) = matches.map { this.toDomain(it) }

	/**
	 * Maps all relations recursively (players, player scores, etc).
	 *
	 * @param match The match data from db.
	 * @param categories The scoring categories of the game for which this match is recorded. This
	 * is used to map the category scores for each player.
	 */
	// FIXME: Give this structure some more thought. There may be a better way to provide the children
	fun toDomainWithRelations(
		match: MatchDataRelationModel,
		categories: Map<Long, CategoryDomainModel>
	) = MatchDomainModel(
		id = match.entity.id,
		gameId = match.entity.gameId,
		notes = match.entity.notes,
		location = match.entity.location,
		dateMillis = match.entity.dateMillis,
		players = match.players.map {
			playerMapper.toDomainWithRelations(it, categories)
		}
	)

	fun toDomainWithRelations(
		matches: List<MatchDataRelationModel>,
		categories: Map<Long, CategoryDomainModel>,
	): List<MatchDomainModel> {
		return matches.map {
			this.toDomainWithRelations(it, categories)
		}
	}
}
