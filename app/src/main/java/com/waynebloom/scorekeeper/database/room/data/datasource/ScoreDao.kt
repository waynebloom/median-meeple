package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
	@Query("DELETE FROM CATEGORYSCORE WHERE id = :id")
	fun deleteById(id: Long)

	@Query("SELECT * FROM CATEGORYSCORE WHERE player_id = :id")
	fun getByPlayerIdAsFlow(id: Long): Flow<List<ScoreDataModel>>

	@Insert
	fun insert(categoryScore: ScoreDataModel): Long

	@Update
	fun update(categoryScore: ScoreDataModel)
}
