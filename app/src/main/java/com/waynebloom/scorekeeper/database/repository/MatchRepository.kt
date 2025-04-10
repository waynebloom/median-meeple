package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.MatchMapper
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class MatchRepository @Inject constructor(
	private val matchDao: MatchDao,
	private val matchMapper: MatchMapper,
) {

	suspend fun deleteBy(id: Long) {
		matchDao.delete(id)
	}

	fun getOne(id: Long): Flow<MatchDomainModel> {
		return matchDao.getOne(id).map(matchMapper::toDomain)
	}

	fun getByDate(start: Instant, period: Period): Flow<List<MatchDomainModel>> {

		// NOTE: Due to DST, this will sometimes produce technically incorrect results.
		//  That is okay for this use case.
		val startMillis = start.toEpochMilli()
		val durationMillis = period[ChronoUnit.DAYS] * 24 * 60 * 60 * 1000
		return matchDao
			.getByDateRange(
				start = start.toEpochMilli(),
				end = startMillis + durationMillis,
			)
			.map(matchMapper::toDomain)
	}

	private fun getByGameID(gameID: Long): Flow<List<MatchDomainModel>> {
		return matchDao.getByGameID(gameID).map(matchMapper::toDomain)
	}

	fun getCountByGameID(gameID: Long): Flow<Int> {
		return matchDao.getCountByGameID(gameID)
	}

	fun getIndexOf(gameID: Long, matchID: Long): Flow<Int> {
		return getByGameID(gameID).map { data ->

			if (matchID == -1L) {
				data.lastIndex + 1
			} else {
				data.indexOfFirst { it.id == matchID }
			}
		}
	}

	suspend fun upsert(match: MatchDomainModel): Long {
		return matchDao.upsertReturningID(matchMapper.toData(match))
	}
}
