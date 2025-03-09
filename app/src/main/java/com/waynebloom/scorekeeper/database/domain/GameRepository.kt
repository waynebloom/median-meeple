package com.waynebloom.scorekeeper.database.domain

import android.util.Log
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.domain.sync.SyncHandler
import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class GameRepository @Inject constructor(
	private val gameDao: GameDao,
	private val gameMapper: GameMapper,
	private val supabaseApi: SupabaseApi,
): SyncHandler {

	override suspend fun sync(change: Pair<Action, JsonObject>) {
		Log.d(this::class.simpleName, "Handling sync for change: $change")
		val entity = gameMapper.toData(change.second)

		if (change.first == Action.DELETE) {
			gameDao.delete(entity.id)
		} else {
			gameDao.upsert(entity)
		}
	}

	suspend fun deleteBy(id: Long) {
		gameDao.delete(id)
	}

	suspend fun delete(entity: GameDataModel) {
		gameDao.delete(entity)
	}

	fun getOne(id: Long): Flow<GameDomainModel> {
		return gameDao.getOne(id).map(gameMapper::toDomain)
	}

	fun getOneWithRelations(id: Long): Flow<GameDomainModel?> {
		return gameDao.getOneWithRelations(id).map(gameMapper::toDomainWithRelations)
	}

	fun getAll(): Flow<List<GameDomainModel>> {
		return gameDao.getAll().map(gameMapper::toDomain)
	}

	fun getAllWithRelations(): Flow<List<GameDomainModel?>> {
		return gameDao.getAllWithRelations().map(gameMapper::toDomainWithRelations)
	}

	fun getFavorites(): Flow<List<GameDomainModel>> {
		return gameDao.getFavorites().map(gameMapper::toDomain)
	}

	fun getMultiple(ids: List<Long>): Flow<List<GameDomainModel>> {
		return gameDao.getMultiple(ids).map(gameMapper::toDomain)
	}

	suspend fun upsert(game: GameDomainModel) {
		gameDao.upsert(gameMapper.toData(game))
	}

	suspend fun upsertReturningID(game: GameDomainModel): Long {
		return gameDao.upsertReturningID(gameMapper.toData(game))
	}
}
