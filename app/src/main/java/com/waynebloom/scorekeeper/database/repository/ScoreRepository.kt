package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.ScoreMapper
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import javax.inject.Inject

class ScoreRepository @Inject constructor(
	private val scoreDao: ScoreDao,
	private val scoreMapper: ScoreMapper,
) {

	suspend fun upsert(score: ScoreDomainModel): Long {
		return scoreDao.upsertReturningID(scoreMapper.toData(score))
	}
}
