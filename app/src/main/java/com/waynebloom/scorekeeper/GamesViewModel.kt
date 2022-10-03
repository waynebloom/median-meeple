package com.waynebloom.scorekeeper

import android.app.Application
import androidx.compose.runtime.remember
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.data.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<GameObject>>
        get() = _games

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<MatchObject>>
        get() = _matches

    val adService = AdService(appObj)

    suspend fun insert(game: GameEntity, afterInsert: (Long) -> Unit) {
        var insertedGameId: Long
        withContext(Dispatchers.IO) { insertedGameId = appRepository.insert(game) }
        withContext(Dispatchers.Main) { afterInsert(insertedGameId) }
    }

    suspend fun insertMatchWithScores(match: MatchEntity, scores: List<ScoreEntity> = listOf()) {
        withContext(Dispatchers.IO) {
            val newMatchId = appRepository.insert(match)
            scores.forEach { score ->
                appRepository.insert(score.apply { matchId = newMatchId })
            }
        }
    }

    suspend fun insert(score: ScoreEntity, afterInsert: (Long) -> Unit) {
        var insertedScoreId: Long
        withContext(Dispatchers.IO) { insertedScoreId = appRepository.insert(score) }
        withContext(Dispatchers.Main) { afterInsert(insertedScoreId) }
    }

    suspend fun deleteGameById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteGameById(id)
        }
    }

    suspend fun deleteMatchById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteMatchById(id)
        }
    }

    suspend fun deleteScoreById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteScoreById(id)
        }
    }

    fun getGameById(id: Long): Flow<GameObject?> {
        return appRepository.getGameById(id)
    }

    fun getMatchById(id: Long): Flow<MatchObject?> {
        return appRepository.getMatchById(id)
    }

    suspend fun updateGame(newGame: GameEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateGame(newGame)
        }
    }

    suspend fun updateMatch(newMatch: MatchEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateMatch(newMatch)
        }
    }

    suspend fun updateScore(newScore: ScoreEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateScore(newScore)
        }
    }
}