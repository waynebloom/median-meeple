package com.waynebloom.scorekeeper.room.domain.mapper

import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import javax.inject.Inject

class MatchDataMapper @Inject constructor(
    private val playerDataMapper: PlayerDataMapper
) {

    fun map(matchData: MatchDataModel) = MatchDomainModel(
        id = matchData.id,
        notes = matchData.notes,
        dateMillis = matchData.dateMillis,
        location = matchData.location,
        players = listOf()
    )

    /**
     * Maps all relations recursively (players, player scores, etc).
     *
     * @param matchData The match data from db.
     * @param categories The scoring categories of the game for which this match is recorded. This
     * is used to map the category scores for each player.
     */
    fun mapWithRelations(
        matchData: MatchDataRelationModel,
        categories: Map<Long, CategoryDomainModel>
    ) = MatchDomainModel(
        id = matchData.entity.id,
        gameId = matchData.entity.gameId,
        notes = matchData.entity.notes,
        location = matchData.entity.location,
        dateMillis = matchData.entity.dateMillis,
        players = matchData.players.map {
            playerDataMapper.mapWithRelations(it, categories)
        }
    )
}
