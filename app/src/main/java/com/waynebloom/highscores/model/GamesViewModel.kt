package com.waynebloom.highscores.model

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.waynebloom.highscores.PreviewGameData
import com.waynebloom.highscores.PreviewScoreData

class GamesViewModel : ViewModel() {
    private val _games = PreviewGameData.toMutableStateList()
    val games: List<Game>
        get() = _games

    private val _recentScores = PreviewScoreData.toMutableStateList()
    val recentScores: List<Score>
        get() = _recentScores

    fun addGame(game: Game) {
        _games.add(game)
    }

    fun addScore(game: Game, score: Score) {
        game.scores.add(0, score)
        _recentScores.add(0, score)
        if (_recentScores.size > 4) _recentScores.removeLast()
    }

    fun removeGame(game: Game) {
        _games.remove(game)
    }

    fun removeScore(game: Game, score: Score) {
        game.scores.remove(score)
    }
}