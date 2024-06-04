package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.PlayerDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayersByMatchIdWithRelations @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerDataMapper: PlayerDataMapper
) {
    suspend operator fun invoke(matchId: Long) = playerRepository
        .getByMatchIdWithRelations(matchId)
        .map(playerDataMapper::mapWithRelations)
}
