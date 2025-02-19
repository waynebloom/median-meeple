package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetMatchesByDate @Inject constructor(
	private val matchRepository: MatchRepository
) {

	suspend operator fun invoke(
		start: ZonedDateTime,
		period: Period
	): List<MatchDataModel> {

		// NOTE: Due to DST, this will sometimes be inaccurate. That is okay for this use case.
		val durationMillis = period[ChronoUnit.DAYS] * 24 * 60 * 60 * 1000
		return matchRepository.getByDateRange(
			begin = start.toEpochSecond() * 1000,
			duration = durationMillis,
		)
	}
}