package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.CategoryMapper
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepository @Inject constructor(
	private val categoryDao: CategoryDao,
	private val categoryMapper: CategoryMapper,
) {

	suspend fun deleteBy(id: Long) {
		categoryDao.delete(id)
	}

	fun getByGameID(matchID: Long): Flow<List<CategoryDomainModel>> {
		return categoryDao.getByGameID(matchID).map(categoryMapper::toDomain)
	}

	suspend fun upsert(category: CategoryDomainModel, gameID: Long): Long {
		return categoryDao.upsertReturningID(categoryMapper.toData(category, gameID))
	}
}
