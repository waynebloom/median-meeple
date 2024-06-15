package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerRepository {

    @Query("DELETE FROM Player WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM Player WHERE id = :id")
    fun getOneWithRelations(id: Long): Flow<PlayerDataRelationModel>

    @Query("SELECT * FROM Player WHERE match_id = :id")
    suspend fun getByMatchId(id: Long): List<PlayerDataModel>

    @Transaction
    @Query("SELECT * FROM Player WHERE match_id = :id")
    suspend fun getByMatchIdWithRelations(id: Long): List<PlayerDataRelationModel>

    @Insert
    suspend fun insert(player: PlayerDataModel): Long

    @Update
    suspend fun update(player: PlayerDataModel)
}
