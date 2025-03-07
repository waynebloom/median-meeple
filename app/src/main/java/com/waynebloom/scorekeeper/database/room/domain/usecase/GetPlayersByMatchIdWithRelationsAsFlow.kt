package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.PlayerDataMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetPlayersByMatchIdWithRelations @Inject constructor(
	private val playerRepository: PlayerDao,
	private val playerDataMapper: PlayerDataMapper
) {
	suspend operator fun invoke(matchId: Long) = playerRepository
		.getByMatchIdWithRelations(matchId)
		// FIXME: migrate dependents to new pattern
		.first()
		.map(playerDataMapper::mapWithRelations)
}
