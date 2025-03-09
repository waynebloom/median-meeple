package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

	@Query("DELETE FROM Player WHERE id = :id")
	suspend fun delete(id: Long)

	@Transaction
	@Query("SELECT * FROM Player WHERE id = :id")
	fun getOneWithRelations(id: Long): Flow<PlayerDataRelationModel>

	@Query("SELECT * FROM Player WHERE match_id = :id")
	fun getByMatchID(id: Long): Flow<List<PlayerDataModel>>

	@Transaction
	@Query("SELECT * FROM Player WHERE match_id = :id")
	fun getByMatchIDWithRelations(id: Long): Flow<List<PlayerDataRelationModel>>

	@Upsert
	suspend fun upsert(player: PlayerDataModel)

	@Upsert
	suspend fun upsertReturningID(player: PlayerDataModel): Long

	// TODO: remove this
	@Update
	fun update(player: PlayerDataModel)
}
