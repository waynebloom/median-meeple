package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.PlayerMapper
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerRepository @Inject constructor(
	private val playerDao: PlayerDao,
	private val playerMapper: PlayerMapper,
) {

	suspend fun deleteBy(id: Long) {
		playerDao.delete(id)
	}

	fun getByMatchIDWithRelations(matchID: Long): Flow<List<PlayerDomainModel>> {
		return playerDao.getByMatchIDWithRelations(matchID).map(playerMapper::toDomainWithRelations)
	}

	suspend fun upsert(player: PlayerDomainModel): Long {
		return playerDao.upsertReturningID(playerMapper.toData(player))
	}
}
