package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class GetIndexOfMatch @Inject constructor(
    private val matchRepository: MatchRepository,
) {

    suspend operator fun invoke(gameId: Long, matchId: Long) = if (matchId == -1L) {
        matchRepository.getByGameId(gameId).lastIndex + 1
    } else {
        matchRepository
            .getByGameId(gameId)
            .indexOfFirst { it.id == matchId }
    }
}
