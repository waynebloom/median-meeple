package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.domain.mapper.PlayerDataMapper
import com.waynebloom.scorekeeper.database.repository.PlayerRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayersByMatchIdWithRelations @Inject constructor(
	private val playerRepository: PlayerDao,
	private val playerDataMapper: PlayerDataMapper
) {
    suspend operator fun invoke(matchId: Long) = playerRepository
        .getByMatchIdWithRelations(matchId)
        .map(playerDataMapper::mapWithRelations)
}
