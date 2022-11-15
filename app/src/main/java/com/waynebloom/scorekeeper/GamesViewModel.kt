package com.waynebloom.scorekeeper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.waynebloom.scorekeeper.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    // region Data Access

    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<GameObject>>
        get() = _games

    val rxGames = appRepository.rxGetAllGames

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<MatchObject>>
        get() = _matches

    // endregion

    // region Object Caching

    var cachedGameObject: GameObject? = EMPTY_GAME_OBJECT
    var cachedMatchObject: MatchObject? = EMPTY_MATCH_OBJECT

    // endregion

    val adService = AdService(appObj)

    suspend fun insert(game: GameEntity, afterInsert: () -> Unit) {
        withContext(Dispatchers.IO) {
            val insertedGameId = appRepository.insert(game)
            cachedGameObject = GameObject(entity = game.copy(id = insertedGameId))
        }
        withContext(Dispatchers.Main) { afterInsert() }
    }

    suspend fun insertMatchWithScores(match: MatchEntity, scores: List<ScoreEntity> = listOf()) {
        withContext(Dispatchers.IO) {
            val insertedMatchId = appRepository.insert(match)
            scores.forEach { appRepository.insert(it.apply { matchId = insertedMatchId }) }
            cachedMatchObject = MatchObject(
                entity = match.copy(id = insertedMatchId),
                scores = scores
            )
            updateGameCacheWithChanges()
        }
    }

    suspend fun insert(score: ScoreEntity) {
        withContext(Dispatchers.IO) {
            appRepository.insert(score)
            updateCachesWithChanges()
        }
    }

    suspend fun deleteGameById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteGameById(id)
            if (cachedGameObject?.entity?.id == id) {
                cachedGameObject = null
                cachedMatchObject = null
            }
        }
    }

    suspend fun deleteMatchById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteMatchById(id)
            updateGameCacheWithChanges()
            if (cachedMatchObject?.entity?.id == id) {
                cachedMatchObject = null
            }
        }
    }

    suspend fun deleteScoreById(id: Long) {
        withContext(Dispatchers.IO) {
            appRepository.deleteScoreById(id)
            updateCachesWithChanges()
        }
    }

    suspend fun updateGame(newGame: GameEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateGame(newGame)
            updateGameCacheWithChanges()
        }
    }

    suspend fun updateMatch(newMatch: MatchEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateMatch(newMatch)
            updateCachesWithChanges()
        }
    }

    suspend fun updateScore(newScore: ScoreEntity) {
        withContext(Dispatchers.IO) {
            appRepository.updateScore(newScore)
            updateCachesWithChanges()
        }
    }

    private suspend fun updateGameCacheWithChanges() {
        games.collectLatest { games ->
            cachedGameObject = games.find { it.entity.id == cachedGameObject?.entity?.id }
        }
    }

    suspend fun updateCachesWithChanges() {
        updateGameCacheWithChanges()
        matches.collectLatest { matches ->
            cachedMatchObject = matches.find { it.entity.id == cachedMatchObject?.entity?.id }
        }
    }
}