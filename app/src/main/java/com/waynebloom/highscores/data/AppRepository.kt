package com.waynebloom.highscores.data

import android.app.Application
import kotlinx.coroutines.flow.Flow

class AppRepository(application: Application) {
    private var appDao: AppDao

    init {
        val database = AppDatabase.getDatabase(application)
        appDao = database.appDao()
    }

    val getAllGames : Flow<List<GameObject>> = appDao.getAllGames()
    val getAllMatches : Flow<List<MatchObject>> = appDao.getAllMatches()
    val getAllScores : Flow<List<ScoreEntity>> = appDao.getAllScores()

    fun getGameById(id: String): Flow<GameObject?> {
        return appDao.getGameById(id)
    }

    fun getMatchById(id: String): Flow<MatchObject?> {
        return appDao.getMatchById(id)
    }

    fun getMatchesByGameId(gameId: String): Flow<List<MatchEntity>> {
        return appDao.getMatchesByGameId(gameId)
    }

    fun getScoresByMatchId(matchId: String): Flow<List<ScoreEntity>> {
        return appDao.getScoresByMatchId(matchId)
    }

    suspend fun addGame(game: GameEntity) {
        appDao.insertGame(game)
    }

    suspend fun addMatch(match: MatchEntity) {
        appDao.insertMatch(match)
    }

    suspend fun addScore(score: ScoreEntity) {
        appDao.insertScore(score)
    }

    suspend fun updateGame(game: GameEntity) {
        appDao.updateGame(game)
    }

    suspend fun updateMatch(match: MatchEntity) {
        appDao.updateMatch(match)
    }

    suspend fun updateScore(score: ScoreEntity) {
        appDao.updateScore(score)
    }

    suspend fun deleteGameById(id: String) {
        appDao.deleteGameById(id)
    }

    suspend fun deleteMatchById(id: String) {
        appDao.deleteMatchById(id)
    }

    suspend fun deleteMatchesByGameId(id: String) {
        appDao.deleteMatchesByGameId(id)
    }

    suspend fun deleteScoreById(id: String) {
        appDao.deleteScoreById(id)
    }

    suspend fun deleteScoresByMatchId(id: String) {
        appDao.deleteScoresByMatchId(id)
    }
}