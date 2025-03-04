package com.waynebloom.scorekeeper.database.room.domain.mapper

import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import javax.inject.Inject

class PlayerDataMapper @Inject constructor(
    private val scoreDataMapper: ScoreDataMapper
) {

    fun map(playerData: PlayerDataModel) = PlayerDomainModel(
        id = playerData.id,
        categoryScores = listOf(),
        name = playerData.name,
        rank = playerData.position,
    )

    /**
     * Maps all relations recursively (players, player scores, etc).
     *
     * @param playerData The match data from db.
     * @param categories The scoring categories of the game for which this match is recorded. This
     * is used to map the category scores for each player.
     */
    fun mapWithRelations(
			playerData: PlayerDataRelationModel,
			categories: Map<Long, CategoryDomainModel>
    ) = PlayerDomainModel(
        id = playerData.entity.id,
        matchId = playerData.entity.matchId,
        categoryScores = playerData.score.map {
            scoreDataMapper.mapWithRelations(it, categories)
        },
        name = playerData.entity.name,
        rank = playerData.entity.position,
    )

    /**
     * Only map the first-level relations.
     *
     * @param playerData The match data from db.
     */
    fun mapWithRelations(playerData: PlayerDataRelationModel) = PlayerDomainModel(
        id = playerData.entity.id,
        matchId = playerData.entity.matchId,
        categoryScores = playerData.score.map {
            scoreDataMapper.map(it)
        },
        name = playerData.entity.name,
        rank = playerData.entity.position,
    )
}
