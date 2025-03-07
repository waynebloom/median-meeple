package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetIndexOfMatch @Inject constructor(
	private val matchRepository: MatchDao,
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(gameId: Long, matchId: Long) = if (matchId == -1L) {
		matchRepository.getByGameId(gameId).first().lastIndex + 1
	} else {
		matchRepository
			.getByGameId(gameId)
			.first()
			.indexOfFirst { it.id == matchId }
	}
}
