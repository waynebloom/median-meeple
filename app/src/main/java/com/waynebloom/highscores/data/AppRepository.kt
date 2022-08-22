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

    fun getGameById(id: Long): Flow<GameObject?> {
        return appDao.getGameById(id)
    }

    fun getMatchById(id: Long): Flow<MatchObject?> {
        return appDao.getMatchById(id)
    }

    fun getMatchesByGameId(gameId: Long): Flow<List<MatchEntity>> {
        return appDao.getMatchesByGameId(gameId)
    }

    fun getScoresByMatchId(matchId: Long): Flow<List<ScoreEntity>> {
        return appDao.getScoresByMatchId(matchId)
    }

    suspend fun insert(game: GameEntity): Long {
        return appDao.insert(game)
    }

    suspend fun insert(match: MatchEntity): Long {
        return appDao.insert(match)
    }

    suspend fun insert(score: ScoreEntity): Long {
        return appDao.insert(score)
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

    suspend fun deleteGameById(id: Long) {
        appDao.deleteGameById(id)
    }

    suspend fun deleteMatchById(id: Long) {
        appDao.deleteMatchById(id)
    }

    suspend fun deleteMatchesByGameId(id: Long) {
        appDao.deleteMatchesByGameId(id)
    }

    suspend fun deleteScoreById(id: Long) {
        appDao.deleteScoreById(id)
    }

    suspend fun deleteScoresByMatchId(id: Long) {
        appDao.deleteScoresByMatchId(id)
    }
}