package com.waynebloom.highscores.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface AppDao {
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<Game>>

    @Query("SELECT * FROM `match`")
    fun getAllMatches(): Flow<List<Match>>

    @Query("SELECT * FROM score")
    fun getAllScores(): Flow<List<Score>>

    @Query("SELECT * FROM game WHERE id = :id")
    fun getGameById(id: String): Flow<Game?>

    @Query("SELECT * FROM `match` WHERE id = :id")
    fun getMatchById(id: String): Flow<Match?>

    @Query("SELECT * FROM `match` WHERE game_owner_id = :gameId")
    fun getMatchesByGameId(gameId: String): Flow<List<Match>>

    @Query("SELECT * FROM score WHERE match_id = :matchId")
    fun getScoresByMatchId(matchId: String): Flow<List<Score>>

    @Insert
    suspend fun insertGame(game: Game)

    @Insert
    suspend fun insertMatch(match: Match)

    @Insert
    suspend fun insertScore(score: Score)

    @Update
    suspend fun updateGame(game: Game)

    @Update
    suspend fun updateMatch(match: Match)

    @Update
    suspend fun updateScore(score: Score)

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: String)

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun deleteMatchById(id: String)

    @Query("DELETE FROM score WHERE id = :id")
    suspend fun deleteScoreById(id: String)
}