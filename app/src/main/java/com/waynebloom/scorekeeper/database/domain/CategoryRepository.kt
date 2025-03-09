package com.waynebloom.scorekeeper.database.domain

import android.util.Log
import com.waynebloom.scorekeeper.database.domain.model.Action
import com.waynebloom.scorekeeper.database.domain.sync.SyncHandler
import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.CategoryMapper
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.supabase.data.datasource.SupabaseApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

class CategoryRepository @Inject constructor(
	private val categoryDao: CategoryDao,
	private val categoryMapper: CategoryMapper,
	private val supabaseApi: SupabaseApi,
) : SyncHandler {

	override fun sync(change: Pair<Action, JsonObject>) {
		Log.d(this::class.simpleName, "Handling sync for change: $change")
		val entity = categoryMapper.toData(change.second)

		if (change.first == Action.DELETE) {
			categoryDao.delete(entity.id)
		} else {
			categoryDao.upsert(entity)
		}
	}

	fun deleteBy(id: Long) {
		categoryDao.delete(id)
	}

	fun getByGameID(matchID: Long): Flow<List<CategoryDomainModel>> {
		return categoryDao.getByGameID(matchID).map(categoryMapper::toDomain)
	}

	fun upsert(category: CategoryDomainModel, gameID: Long) {
		categoryDao.upsert(categoryMapper.toData(category, gameID))
	}

	suspend fun upsertReturningID(category: CategoryDomainModel, gameID: Long): Long {
		return categoryDao.upsertReturningID(categoryMapper.toData(category, gameID))
	}
}
