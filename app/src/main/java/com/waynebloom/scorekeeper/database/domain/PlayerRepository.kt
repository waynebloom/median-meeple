package com.waynebloom.scorekeeper.database.domain

import android.util.Log
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.domain.sync.SyncHandler
import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.PlayerMapper
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class PlayerRepository @Inject constructor(
	private val playerDao: PlayerDao,
	private val playerMapper: PlayerMapper,
	private val supabaseApi: SupabaseApi,
) : SyncHandler {

	override suspend fun sync(change: Pair<Action, JsonObject>) {
		Log.d(this::class.simpleName, "Handling sync for change: $change")
		val entity = playerMapper.toData(change.second)

		if (change.first == Action.DELETE) {
			playerDao.delete(entity.id)
		} else {
			playerDao.upsert(entity)
		}
	}

	suspend fun deleteBy(id: Long) {
		playerDao.delete(id)
	}

	fun getByMatchIDWithRelations(matchID: Long): Flow<List<PlayerDomainModel>> {
		return playerDao.getByMatchIDWithRelations(matchID).map(playerMapper::toDomainWithRelations)
	}

	suspend fun upsert(player: PlayerDomainModel) {
		playerDao.upsert(playerMapper.toData(player))
	}

	suspend fun upsertReturningID(player: PlayerDomainModel): Long {
		return playerDao.upsertReturningID(playerMapper.toData(player))
	}
}
