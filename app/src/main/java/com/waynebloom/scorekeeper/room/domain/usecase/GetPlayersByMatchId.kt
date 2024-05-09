package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.PlayerDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository
import javax.inject.Inject

class GetPlayersByMatchId @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerDataMapper: PlayerDataMapper
) {
    suspend operator fun invoke(matchId: Long) = playerRepository
        .getByMatchId(matchId)
        .map(playerDataMapper::map)
}
