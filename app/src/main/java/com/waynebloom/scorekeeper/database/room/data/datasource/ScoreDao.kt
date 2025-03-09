package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

	@Query("DELETE FROM CATEGORYSCORE WHERE id = :id")
	fun delete(id: Long)

	@Query("SELECT * FROM CATEGORYSCORE WHERE player_id = :id")
	fun getByPlayerID(id: Long): Flow<List<ScoreDataModel>>

	@Upsert
	suspend fun upsertReturningID(score: ScoreDataModel): Long

	@Upsert
	fun upsert(score: ScoreDataModel)

	// TODO: remove this
	@Update
	fun update(score: ScoreDataModel)
}
