package com.waynebloom.highscores.data

import android.app.Application
import kotlinx.coroutines.flow.Flow

class AppRepository(application: Application) {
    private var appDao: AppDao

    init {
        val database = AppDatabase.getDatabase(application)
        appDao = database.appDao()
    }

    val getAllGames : Flow<List<Game>> = appDao.getAllGames()
    val getAllScores : Flow<List<Score>> = appDao.getAllScores()

    fun getGameById(id: String): Flow<Game?> {
        return appDao.getGameById(id)
    }

    fun getScoresByGameId(gameId: String): Flow<List<Score>> {
        return appDao.getScoresByGameId(gameId)
    }

    fun getScoreById(id: String): Flow<Score?> {
        return appDao.getScoreById(id)
    }

    suspend fun addGame(game: Game) {
        appDao.insertGame(game)
    }

    suspend fun addScore(score: Score) {
        appDao.insertScore(score)
    }

    suspend fun updateGame(game: Game) {
        appDao.updateGame(game)
    }

    suspend fun updateScore(score: Score) {
        appDao.updateScore(score)
    }

    suspend fun deleteGame(game: Game) {
        appDao.deleteGame(game)
    }

    suspend fun deleteGameById(id: String) {
        appDao.deleteGameById(id)
    }

    suspend fun deleteScore(score: Score) {
        appDao.deleteScore(score)
    }

    suspend fun deleteScoreById(id: String) {
        appDao.deleteScoreById(id)
    }
}