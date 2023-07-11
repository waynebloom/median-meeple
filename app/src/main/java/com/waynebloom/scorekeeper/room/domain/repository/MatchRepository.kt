package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchRepository {

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    fun getOneWithRelationsAsFlow(id: Long): Flow<MatchDataRelationModel>

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    suspend fun getOneWithRelations(id: Long): MatchDataRelationModel

    @Query("SELECT * FROM `Match` WHERE game_owner_id = :id")
    suspend fun getByGame(id: Long): MatchDataModel

    @Insert
    suspend fun insert(match: MatchDataModel): Long

    @Update
    suspend fun update(match: MatchDataModel)
}
