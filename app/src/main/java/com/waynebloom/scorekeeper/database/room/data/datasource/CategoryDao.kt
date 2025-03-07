package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

	@Query("DELETE FROM CATEGORY WHERE id = :id")
	fun deleteById(id: Long)

	@Query("SELECT * FROM CATEGORY WHERE game_id = :id")
	fun getByGameId(id: Long): Flow<List<CategoryDataModel>>

	@Insert
	fun insert(entity: CategoryDataModel): Long

	@Update
	fun update(entity: CategoryDataModel)
}
