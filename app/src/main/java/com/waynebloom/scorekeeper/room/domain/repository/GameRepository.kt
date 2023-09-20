package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRepository {

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(entity: GameDataModel)

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun get(id: Long): GameDataRelationModel

    @Transaction
    @Query("SELECT * FROM game")
    suspend fun getAll(): List<GameDataRelationModel>

    @Insert
    suspend fun insert(game: GameDataModel): Long

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    fun observe(id: Long): Flow<GameDataRelationModel>

    @Update
    suspend fun update(game: GameDataModel)
}