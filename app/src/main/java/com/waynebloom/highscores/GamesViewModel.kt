package com.waynebloom.highscores

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.highscores.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<GameObject>>
        get() = _games

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<MatchObject>>
        get() = _matches

    private val _scores = appRepository.getAllScores
    val scores: Flow<List<ScoreEntity>>
        get() = _scores

    fun addGame(game: GameEntity) {
        viewModelScope.launch { appRepository.addGame(game) }
    }

    fun addMatch(match: MatchEntity) {
        viewModelScope.launch { appRepository.addMatch(match) }
    }

    fun addScore(score: ScoreEntity) {
        viewModelScope.launch { appRepository.addScore(score) }
    }

    suspend fun deleteGameById(id: String) {
        appRepository.deleteGameById(id)
    }

    fun deleteMatchById(id: String) {
        viewModelScope.launch { appRepository.deleteMatchById(id) }
    }

    fun deleteMatchesByGameId(id: String) {
        viewModelScope.launch { appRepository.deleteMatchesByGameId(id) }
    }

    fun deleteScoreById(id: String) {
        viewModelScope.launch { appRepository.deleteScoreById(id) }
    }

    fun deleteScoresByMatchId(id: String) {
        viewModelScope.launch { appRepository.deleteScoresByMatchId(id) }
    }

    fun getGameById(id: String): Flow<GameObject?> {
        return appRepository.getGameById(id)
    }

    fun getMatchById(id: String): Flow<MatchObject?> {
        return appRepository.getMatchById(id)
    }

    fun getMatchesByGameId(gameId: String): Flow<List<MatchEntity>> {
        return appRepository.getMatchesByGameId(gameId)
    }

    fun getScoresByMatchId(matchId: String): Flow<List<ScoreEntity>> {
        return appRepository.getScoresByMatchId(matchId)
    }

    fun updateGame(newGame: GameEntity) {
        viewModelScope.launch { appRepository.updateGame(newGame) }
    }

    fun updateMatch(newMatch: MatchEntity) {
        viewModelScope.launch { appRepository.updateMatch(newMatch) }
    }

    fun updateScore(newScore: ScoreEntity) {
        viewModelScope.launch { appRepository.updateScore(newScore) }
    }
}