package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class GetMatchesByDateRange @Inject constructor(
	private val matchRepository: MatchRepository
) {

	suspend operator fun invoke(range: LongRange): List<MatchDataModel> {
		// TODO: this needs to return a Flow
		return matchRepository.getByDateRange(range.first, range.last)
	}
}