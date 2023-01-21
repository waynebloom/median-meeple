package com.waynebloom.scorekeeper.data.database

import androidx.room.*
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // region Game

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Transaction
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<GameObject>>

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun getGameById(id: Long): GameObject

    @Insert
    suspend fun insert(gameEntity: GameEntity): Long

    @Update
    suspend fun updateGame(gameEntity: GameEntity)

    // endregion

    // region Match

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun deleteMatchById(id: Long)

    @Transaction
    @Query("SELECT * FROM `match`")
    fun getAllMatches(): Flow<List<MatchObject>>

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    suspend fun getMatchById(id: Long): MatchObject

    @Insert
    suspend fun insert(matchEntity: MatchEntity): Long

    @Update
    suspend fun updateMatch(matchEntity: MatchEntity)

    // endregion

    // region Player

    @Query("DELETE FROM player WHERE id = :id")
    suspend fun deletePlayerById(id: Long)

    @Transaction
    @Query("SELECT * FROM Player WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerObject

    @Insert
    suspend fun insert(playerEntity: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(playerEntity: PlayerEntity)

    // endregion

    // region Subscore

    @Insert
    suspend fun insertSubscore(subscoreEntity: SubscoreEntity): Long

    @Update
    suspend fun updateSubscore(subscoreEntity: SubscoreEntity)

    // endregion

    // region SubscoreTitle

    @Query("DELETE FROM subscoretitle WHERE id = :id")
    suspend fun deleteSubscoreTitleById(id: Long)

    @Insert
    suspend fun insert(subscoreTitleEntity: SubscoreTitleEntity): Long

    @Update
    suspend fun updateSubscoreTitle(subscoreTitleEntity: SubscoreTitleEntity)

    // endregion
}