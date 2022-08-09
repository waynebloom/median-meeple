package com.waynebloom.highscores

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.highscores.data.AppRepository
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Match
import com.waynebloom.highscores.data.Score
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<Game>>
        get() = _games

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<Match>>
        get() = _matches

    private val _scores = appRepository.getAllScores
    val scores: Flow<List<Score>>
        get() = _scores

    fun addGame(game: Game) {
        viewModelScope.launch { appRepository.addGame(game) }
    }

    fun addMatch(match: Match) {
        viewModelScope.launch { appRepository.addMatch(match) }
    }

    fun addScore(score: Score) {
        viewModelScope.launch { appRepository.addScore(score) }
    }

    fun deleteGameById(id: String) {
        viewModelScope.launch { appRepository.deleteGameById(id) }
    }

    fun deleteMatchById(id: String) {
        viewModelScope.launch { appRepository.deleteMatchById(id) }
    }

    fun deleteScoreById(id: String) {
        viewModelScope.launch { appRepository.deleteScoreById(id) }
    }

    fun getGameById(id: String): Flow<Game?> {
        return appRepository.getGameById(id)
    }

    fun getMatchById(id: String): Flow<Match?> {
        return appRepository.getMatchById(id)
    }

    fun getMatchesByGameId(gameId: String): Flow<List<Match>> {
        return appRepository.getMatchesByGameId(gameId)
    }

    fun getScoresByMatchId(matchId: String): Flow<List<Score>> {
        return appRepository.getScoresByMatchId(matchId)
    }

    fun updateGame(newGame: Game) {
        viewModelScope.launch { appRepository.updateGame(newGame) }
    }

    fun updateMatch(newMatch: Match) {
        viewModelScope.launch { appRepository.updateMatch(newMatch) }
    }

    fun updateScore(newScore: Score) {
        viewModelScope.launch { appRepository.updateScore(newScore) }
    }
}