package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.MapColumn
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.data.model.GameDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

	@Query("DELETE FROM GAME WHERE ID = :id")
	suspend fun delete(id: Long)

	@Delete
	suspend fun delete(entity: GameDataModel)

	@Query("SELECT * FROM GAME")
	fun getAll(): Flow<List<GameDataModel>>

	@Query("""
		select
  		*,
  		(select count(*) from `Match` m where m.game_owner_id = g.id) matchCount 
		from Game g where id not in (:excludedIds)
	""")
	fun getAllWithMatchCounts(excludedIds: List<Long>):
		Flow<
			Map<
				GameDataModel,
				@MapColumn("matchCount") Int
			>
		>

	@Transaction
	@Query("SELECT * FROM GAME")
	fun getAllWithRelations(): Flow<List<GameDataRelationModel>>

	@Query("SELECT * FROM GAME WHERE is_favorite = 1")
	fun getFavorites(): Flow<List<GameDataModel>>

	@Query("SELECT * FROM GAME WHERE ID = :id")
	fun getOne(id: Long): Flow<GameDataModel?>

	@Transaction
	@Query("SELECT * FROM GAME WHERE ID = :id")
	fun getOneWithRelations(id: Long): Flow<GameDataRelationModel?>

	@Query("SELECT * FROM GAME WHERE ID IN (:ids)")
	fun getMultiple(ids: List<Long>): Flow<List<GameDataModel>>

	@Upsert
	suspend fun upsert(game: GameDataModel)

	@Upsert
	suspend fun upsertReturningID(game: GameDataModel): Long
}
