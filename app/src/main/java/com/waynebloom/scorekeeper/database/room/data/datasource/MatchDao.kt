package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.database.room.data.model.MatchDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {

	@Query("DELETE FROM `match` WHERE id = :id")
	suspend fun delete(id: Long)

	@Transaction
	@Query("SELECT * FROM `match` WHERE id = :id")
	fun getOneWithRelationsAsFlow(id: Long): Flow<MatchDataRelationModel>

	@Transaction
	@Query("SELECT * FROM `match` WHERE id = :id")
	fun getOneWithRelations(id: Long): Flow<MatchDataRelationModel>

	@Query("SELECT * FROM `Match` WHERE id = :id")
	fun getOne(id: Long): Flow<MatchDataModel>

	@Query("SELECT * FROM `Match` WHERE game_owner_id = :id")
	fun getByGameID(id: Long): Flow<List<MatchDataModel>>

	@Query("SELECT COUNT(CASE WHEN game_owner_id = :id THEN 1 END) FROM `Match`")
	fun getCountByGameID(id: Long): Flow<Int>

	@Query(
		"""
        SELECT * FROM `Match`
        WHERE date_millis > :start AND date_millis < :end
    """
	)
	fun getByDateRange(start: Long, end: Long): Flow<List<MatchDataModel>>

	@Upsert
	suspend fun upsert(match: MatchDataModel)

	@Upsert
	suspend fun upsertReturningID(match: MatchDataModel): Long

	// TODO: remove this
	@Update
	fun update(match: MatchDataModel)
}
