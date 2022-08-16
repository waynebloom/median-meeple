package com.waynebloom.highscores.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Transaction
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<GameObject>>

    @Transaction
    @Query("SELECT * FROM `match`")
    fun getAllMatches(): Flow<List<MatchObject>>

    @Query("SELECT * FROM score")
    fun getAllScores(): Flow<List<ScoreEntity>>

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    fun getGameById(id: String): Flow<GameObject?>

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    fun getMatchById(id: String): Flow<MatchObject?>

    @Transaction
    @Query("SELECT * FROM `match` WHERE game_owner_id = :gameId")
    fun getMatchesByGameId(gameId: String): Flow<List<MatchEntity>>

    @Query("SELECT * FROM score WHERE match_id = :matchId")
    fun getScoresByMatchId(matchId: String): Flow<List<ScoreEntity>>

    @Insert
    suspend fun insertGame(game: GameEntity)

    @Insert
    suspend fun insertMatch(match: MatchEntity)

    @Insert
    suspend fun insertScore(score: ScoreEntity)

    @Update
    suspend fun updateGame(game: GameEntity)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Update
    suspend fun updateScore(score: ScoreEntity)

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: String)

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun deleteMatchById(id: String)

    @Query("DELETE FROM `match` WHERE game_owner_id = :id")
    suspend fun deleteMatchesByGameId(id: String)

    @Query("DELETE FROM score WHERE id = :id")
    suspend fun deleteScoreById(id: String)

    @Query("DELETE FROM score WHERE match_id = :id")
    suspend fun deleteScoresByMatchId(id: String)
}