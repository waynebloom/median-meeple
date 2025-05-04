package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameWithMatchCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameRepository @Inject constructor(
	private val gameDao: GameDao,
	private val gameMapper: GameMapper,
) {

	suspend fun deleteBy(id: Long) {
		gameDao.delete(id)
	}

	suspend fun delete(entity: GameDataModel) {
		gameDao.delete(entity)
	}

	fun getOne(id: Long): Flow<GameDomainModel?> {
		return gameDao.getOne(id).map(gameMapper::toDomainOrNull)
	}

	fun getOneWithRelations(id: Long): Flow<GameDomainModel?> {
		return gameDao.getOneWithRelations(id).map(gameMapper::toDomainWithRelations)
	}

	fun getAll(): Flow<List<GameDomainModel?>> {
		return gameDao.getAll().map(gameMapper::toDomain)
	}

	fun getAllWithMatchCount(excludedIds: List<Long>): Flow<List<GameWithMatchCount>> {
		return gameDao.getAllWithMatchCounts(excludedIds).map { games ->
			games.mapNotNull { (game, matchCount) ->
				GameWithMatchCount(gameMapper.toDomain(game), matchCount)
			}
		}
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

	suspend fun upsert(game: GameDomainModel): Long {
		return gameDao.upsertReturningID(gameMapper.toData(game))
	}
}
