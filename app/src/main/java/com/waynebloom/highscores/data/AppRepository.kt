package com.waynebloom.highscores.data

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class AppRepository(application: Application) {
    private var appDao: AppDao

    init {
        val database = AppDatabase.getDatabase(application)
        appDao = database.appDao()
    }

    val getAllGames : Flow<List<Game>> = appDao.getAllGames()
    val getAllMatches : Flow<List<Match>> = appDao.getAllMatches()
    val getAllScores : Flow<List<Score>> = appDao.getAllScores()

    fun getGameById(id: String): Flow<Game?> {
        return appDao.getGameById(id)
    }

    fun getMatchById(id: String): Flow<Match?> {
        return appDao.getMatchById(id)
    }

    fun getMatchesByGameId(gameId: String): Flow<List<Match>> {
        return appDao.getMatchesByGameId(gameId)
    }

    fun getScoresByMatchId(matchId: String): Flow<List<Score>> {
        return appDao.getScoresByMatchId(matchId)
    }

    suspend fun addGame(game: Game) {
        appDao.insertGame(game)
    }

    suspend fun addMatch(match: Match) {
        appDao.insertMatch(match)
    }

    suspend fun addScore(score: Score) {
        appDao.insertScore(score)
    }

    suspend fun updateGame(game: Game) {
        appDao.updateGame(game)
    }

    suspend fun updateMatch(match: Match) {
        appDao.updateMatch(match)
    }

    suspend fun updateScore(score: Score) {
        appDao.updateScore(score)
    }

    suspend fun deleteGameById(id: String) {
        appDao.deleteGameById(id)
    }

    suspend fun deleteMatchById(id: String) {
        appDao.deleteMatchById(id)
    }

    suspend fun deleteScoreById(id: String) {
        appDao.deleteScoreById(id)
    }
}