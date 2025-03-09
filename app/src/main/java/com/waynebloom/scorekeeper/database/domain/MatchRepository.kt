package com.waynebloom.scorekeeper.database.domain

import android.util.Log
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.domain.sync.SyncHandler
import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.database.room.domain.mapper.MatchMapper
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class MatchRepository @Inject constructor(
	private val matchDao: MatchDao,
	private val matchMapper: MatchMapper,
	private val supabaseApi: SupabaseApi,
) : SyncHandler {

	override fun sync(change: Pair<Action, JsonObject>) {
		Log.d(this::class.simpleName, "Handling sync for change: $change")
		val entity = matchMapper.toData(change.second)

		Log.d(this::class.simpleName, "Result of json parse: $entity")

		if (change.first == Action.DELETE) {
			matchDao.delete(entity.id)
		} else {
			matchDao.upsert(entity)
		}
	}

	// TODO: pick back up by updating deletes and upserts to always suspend. Looks like they need to
	// 	no matter what.

	fun deleteBy(id: Long) {
		matchDao.delete(id)
	}

	fun getOne(id: Long): Flow<MatchDomainModel> {
		return matchDao.getOne(id).map(matchMapper::toDomain)
	}

	fun getByDate(start: ZonedDateTime, period: Period): Flow<List<MatchDataModel>> {

		// NOTE: Due to DST, this will sometimes be inaccurate. That is okay for this use case.
		val durationMillis = period[ChronoUnit.DAYS] * 24 * 60 * 60 * 1000
		return matchDao
			.getByDateRange(
				begin = start.toEpochSecond() * 1000,
				duration = durationMillis,
			)
	}

	private fun getByGameID(gameID: Long): Flow<List<MatchDomainModel>> {
		return matchDao.getByGameId(gameID).map(matchMapper::toDomain)
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

	fun upsert(match: MatchDomainModel) {
		matchDao.upsert(matchMapper.toData(match))
	}

	suspend fun upsertReturningID(match: MatchDomainModel): Long {
		return matchDao.upsertReturningID(matchMapper.toData(match))
	}
}
