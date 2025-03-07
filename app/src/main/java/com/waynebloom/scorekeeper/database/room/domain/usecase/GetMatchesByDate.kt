package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import kotlinx.coroutines.flow.first
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetMatchesByDate @Inject constructor(
	private val matchRepository: MatchDao
) {

	suspend operator fun invoke(
		start: ZonedDateTime,
		period: Period
	): List<MatchDataModel> {

		// NOTE: Due to DST, this will sometimes be inaccurate. That is okay for this use case.
		val durationMillis = period[ChronoUnit.DAYS] * 24 * 60 * 60 * 1000
		return matchRepository
			.getByDateRange(
				begin = start.toEpochSecond() * 1000,
				duration = durationMillis,
			)
			// FIXME: migrate dependents to new pattern
			.first()
	}
}