package com.waynebloom.scorekeeper.database.room.data.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

	@Query("DELETE FROM Player WHERE id = :id")
	fun delete(id: Long)

	@Transaction
	@Query("SELECT * FROM Player WHERE id = :id")
	fun getOneWithRelations(id: Long): Flow<PlayerDataRelationModel>

	@Query("SELECT * FROM Player WHERE match_id = :id")
	fun getByMatchId(id: Long): Flow<List<PlayerDataModel>>

	@Transaction
	@Query("SELECT * FROM Player WHERE match_id = :id")
	fun getByMatchIdWithRelations(id: Long): Flow<List<PlayerDataRelationModel>>

	@Insert
	fun insert(player: PlayerDataModel): Long

	@Update
	fun update(player: PlayerDataModel)
}
