package com.waynebloom.scorekeeper.database.domain

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import kotlinx.coroutines.flow.flowOf
import java.time.Period
import java.time.ZonedDateTime
import javax.inject.Inject

class MatchRepository @Inject constructor(
	private val matchDao: MatchDao,
) {

	fun getByDate(start: ZonedDateTime, period: Period) = flowOf(listOf<MatchDataModel>())
}
