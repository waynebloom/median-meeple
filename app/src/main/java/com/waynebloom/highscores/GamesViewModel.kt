package com.waynebloom.highscores

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.highscores.data.AppRepository
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Score
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<Game>>
        get() = _games

    private val _scores = appRepository.getAllScores
    val scores: Flow<List<Score>>
        get() = _scores

    var currentGame: Game by mutableStateOf(Game())
    var currentScoresList: List<Score> by mutableStateOf(listOf())
    var currentScore: Score by mutableStateOf(Score())

    fun addGame(game: Game) {
        viewModelScope.launch { appRepository.addGame(game) }
    }

    fun addScore(score: Score) {
        viewModelScope.launch { appRepository.addScore(score) }
    }

    fun deleteGame(game: Game) {
        viewModelScope.launch { appRepository.deleteGame(game) }
    }

    fun deleteScore(score: Score) {
        viewModelScope.launch { appRepository.deleteScore(score) }
    }

    fun deleteGameById(id: String) {
        viewModelScope.launch { appRepository.deleteGameById(id) }
    }

    fun deleteScoreById(id: String) {
        viewModelScope.launch { appRepository.deleteScoreById(id) }
    }

    fun getGameById(id: String): Flow<Game?> {
        return appRepository.getGameById(id)
    }

    fun getScoreById(id: String): Flow<Score?> {
        return appRepository.getScoreById(id)
    }

    fun getScoresByGameId(gameId: String): Flow<List<Score>> {
        return appRepository.getScoresByGameId(gameId)
    }

    fun updateScore(newScore: Score) {
        viewModelScope.launch { appRepository.updateScore(newScore) }
    }

    fun updateGame(newGame: Game) {
        viewModelScope.launch { appRepository.updateGame(newGame) }
    }
}