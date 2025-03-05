package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.repository.MatchRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import javax.inject.Inject

class GetIndexOfMatch @Inject constructor(
	private val matchRepository: MatchDao,
) {

    suspend operator fun invoke(gameId: Long, matchId: Long) = if (matchId == -1L) {
        matchRepository.getByGameId(gameId).lastIndex + 1
    } else {
        matchRepository
            .getByGameId(gameId)
            .indexOfFirst { it.id == matchId }
    }
}
