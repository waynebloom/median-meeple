package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

	@Query("DELETE FROM CATEGORY WHERE id = :id")
	suspend fun delete(id: Long)

	@Query("SELECT * FROM CATEGORY WHERE game_id = :id")
	fun getByGameID(id: Long): Flow<List<CategoryDataModel>>

	@Upsert
	suspend fun upsertReturningID(entity: CategoryDataModel): Long

	@Upsert
	suspend fun upsert(entity: CategoryDataModel)
}
