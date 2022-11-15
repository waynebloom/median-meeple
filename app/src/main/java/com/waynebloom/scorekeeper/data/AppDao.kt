package com.waynebloom.scorekeeper.data

import androidx.room.*
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Transaction
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<GameObject>>

    @Transaction
    @Query("SELECT * FROM game")
    fun rxGetAllGames(): Single<List<GameObject>>

    @Transaction
    @Query("SELECT * FROM `match`")
    fun getAllMatches(): Flow<List<MatchObject>>

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    fun getGameById(id: Long): Flow<GameObject?>

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    fun getMatchById(id: Long): Flow<MatchObject?>

    @Insert
    suspend fun insert(game: GameEntity): Long

    @Insert
    suspend fun insert(match: MatchEntity): Long

    @Insert
    suspend fun insert(score: ScoreEntity): Long

    @Update
    suspend fun updateGame(game: GameEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Update
    suspend fun updateScore(score: ScoreEntity)

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun deleteMatchById(id: Long)

    @Query("DELETE FROM score WHERE id = :id")
    suspend fun deleteScoreById(id: Long)
}