package com.waynebloom.highscores.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM score")
    fun getAllScores(): Flow<List<Score>>

    @Query("SELECT * FROM game WHERE id = :id")
    fun getGameById(id: String): Flow<Game?>

    @Query("SELECT * FROM score WHERE id = :id")
    fun getScoreById(id: String): Flow<Score?>

    @Query("SELECT * FROM score WHERE game_owner_id = :gameId")
    fun getScoresByGameId(gameId: String): Flow<List<Score>>

    @Insert
    suspend fun insertGame(game: Game)

    @Insert
    suspend fun insertScore(score: Score)

    @Update
    suspend fun updateGame(game: Game)

    @Update
    suspend fun updateScore(score: Score)

    @Delete
    suspend fun deleteGame(game: Game)

    @Delete
    suspend fun deleteScore(score: Score)

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: String)

    @Query("DELETE FROM score WHERE id = :id")
    suspend fun deleteScoreById(id: String)
}