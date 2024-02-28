package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchRepository {

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    suspend fun get(id: Long): MatchDataRelationModel

    @Query("SELECT * FROM `Match` WHERE game_owner_id = :id")
    suspend fun getByGame(id: Long): MatchDataModel

    @Transaction
    @Query("SELECT * FROM `match`")
    suspend fun getAll(): List<MatchDataRelationModel>

    @Transaction
    @Query("SELECT * FROM `match`")
    fun getAllAsFlow(): Flow<List<MatchDataRelationModel>>
}
