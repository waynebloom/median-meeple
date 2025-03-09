package com.waynebloom.scorekeeper.database.room.domain.mapper

import com.squareup.moshi.JsonAdapter
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

class PlayerMapper @Inject constructor(
	private val scoreMapper: ScoreMapper,
) {

	fun toData(json: JsonObject): PlayerDataModel {
		return Json.decodeFromJsonElement(json)
	}

	fun toData(player: PlayerDomainModel) = player.let {
		PlayerDataModel(
			id = it.id,
			matchID = it.matchID,
			name = it.name,
			position = it.position,
		)
	}

	fun toDomain(player: PlayerDataModel) = player.let {
		PlayerDomainModel(
			id = it.id,
			name = it.name,
			position = it.position,
		)
	}

	fun toDomain(players: List<PlayerDataModel>) = players.map { this.toDomain(it) }

	/**
	 * Maps all relations recursively (players, player scores, etc).
	 *
	 * @param playerData The match data from db.
	 * @param categories The scoring categories of the game for which this match is recorded. This
	 * is used to map the category scores for each player.
	 */
	fun toDomainWithRelations(
		playerData: PlayerDataRelationModel,
		categories: Map<Long, CategoryDomainModel>
	) = PlayerDomainModel(
		id = playerData.entity.id,
		matchID = playerData.entity.matchID,
		categoryScores = playerData.score.map {
			scoreMapper.toDomainWithRelations(it, categories)
		},
		name = playerData.entity.name,
		position = playerData.entity.position,
	)

	/**
	 * Only map the first-level relations.
	 *
	 * @param playerData The match data from db.
	 */
	fun toDomainWithRelations(playerData: PlayerDataRelationModel) = PlayerDomainModel(
		id = playerData.entity.id,
		matchID = playerData.entity.matchID,
		categoryScores = playerData.score.map {
			scoreMapper.toDomain(it)
		},
		name = playerData.entity.name,
		position = playerData.entity.position,
	)

	fun toDomainWithRelations(players: List<PlayerDataRelationModel>) = players.map { this.toDomainWithRelations(it) }
}
