package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel

@Dao
interface CategoryDao {

	@Query("DELETE FROM CATEGORY WHERE id = :id")
	suspend fun deleteById(id: Long)

	@Query("SELECT * FROM CATEGORY WHERE game_id = :id")
	suspend fun getByGameId(id: Long): List<CategoryDataModel>

	@Insert
	suspend fun insert(entity: CategoryDataModel): Long

	@Update
	suspend fun update(entity: CategoryDataModel)
}
