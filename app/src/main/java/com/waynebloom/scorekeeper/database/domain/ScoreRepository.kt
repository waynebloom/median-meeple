package com.waynebloom.scorekeeper.database.domain

import android.util.Log
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.domain.sync.SyncHandler
import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.ScoreMapper
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class ScoreRepository @Inject constructor(
	private val scoreDao: ScoreDao,
	private val scoreMapper: ScoreMapper,
	private val supabaseApi: SupabaseApi,
) : SyncHandler {

	override fun sync(change: Pair<Action, JsonObject>) {
		Log.d(this::class.simpleName, "Handling sync for change: $change")
		val entity = scoreMapper.toData(change.second)

		if (change.first == Action.DELETE) {
			scoreDao.delete(entity.id)
		} else {
			scoreDao.upsert(entity)
		}
	}

	fun upsert(score: ScoreDomainModel) {
		scoreDao.upsert(scoreMapper.toData(score))
	}

	suspend fun upsertReturningID(score: ScoreDomainModel): Long {
		return scoreDao.upsertReturningID(scoreMapper.toData(score))
	}
}
