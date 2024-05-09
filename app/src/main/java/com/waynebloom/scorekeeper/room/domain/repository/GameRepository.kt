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

    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun getOne(id: Long): GameDataModel

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun getOneWithRelations(id: Long): GameDataRelationModel

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    fun getOneWithRelationsAsFlow(id: Long): Flow<GameDataRelationModel?>

    @Transaction
    @Query("SELECT * FROM game")
    suspend fun getAll(): List<GameDataRelationModel>

    @Transaction
    @Query("SELECT * FROM game")
    fun getAllAsFlow(): Flow<List<GameDataRelationModel>>

    @Insert
    suspend fun insert(game: GameDataModel): Long

    @Update
    suspend fun update(game: GameDataModel)
}
