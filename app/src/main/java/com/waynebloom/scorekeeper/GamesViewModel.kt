package com.waynebloom.scorekeeper

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.enums.DatabaseAction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class GamesViewModel(appObj: Application) : AndroidViewModel(appObj) {
    // region Data Access

    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<GameObject>>
        get() = _games

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<MatchObject>>
        get() = _matches

    private val dbJobChannel = Channel<Job>(capacity = Channel.UNLIMITED)

    // endregion

    // region Object Caching

    var cachedGameObject: GameObject by mutableStateOf(EMPTY_GAME_OBJECT)
    var cachedMatchObject: MatchObject by mutableStateOf(EMPTY_MATCH_OBJECT)

    private var gameCacheNeedsUpdate = false
    private var matchCacheNeedsUpdate = false

    // endregion

    val adService = AdService(appObj)

    // region Game

    fun deleteGameById(id: Long) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                appRepository.deleteGameById(id)
                if (cachedGameObject.entity.id == id) {
                    cachedGameObject = EMPTY_GAME_OBJECT
                    cachedMatchObject = EMPTY_MATCH_OBJECT
                }
            }
        )
    }

    fun insertGame(game: GameEntity, afterInsert: () -> Unit) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                val insertedGameId = appRepository.insert(game)
                cachedGameObject = GameObject(
                    entity = game.copy(id = insertedGameId)
                )
                afterInsert()
            }
        )
    }

    fun updateGame(newGame: GameEntity) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                appRepository.updateGame(newGame)
                gameCacheNeedsUpdate = true
            }
        )
    }

    // endregion

    // region Match

    fun deleteMatchById(id: Long) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                appRepository.deleteMatchById(id)
                if (cachedMatchObject.entity.id == id) {
                    cachedMatchObject = EMPTY_MATCH_OBJECT
                }
                gameCacheNeedsUpdate = true
            }
        )
    }

    fun insertMatchWithScores(match: MatchEntity, scores: List<ScoreEntity> = listOf()) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                val insertedMatchId = appRepository.insert(match)
                scores.forEach {
                    dbJobChannel.trySend(
                        viewModelScope.launch {
                            appRepository.insert(
                                it.apply { matchId = insertedMatchId }
                            )
                        }
                    )
                }
                cachedMatchObject = MatchObject(
                    entity = match.copy(id = insertedMatchId),
                    scores = scores
                )
                gameCacheNeedsUpdate = true
            }
        )
    }

    fun updateMatch(updatedMatch: MatchEntity) {
        dbJobChannel.trySend(
            viewModelScope.launch {
                appRepository.updateMatch(updatedMatch)
                gameCacheNeedsUpdate = true
                matchCacheNeedsUpdate = true
            }
        )
    }

    // endregion

    // region Score

    fun forwardScoreListUpdatesToDb(updatedScores: List<ScoreObject>) {
        updatedScores.forEach { score ->
            when(score.action) {
                DatabaseAction.UPDATE -> updateScore(score.entity)
                DatabaseAction.INSERT -> insertScore(score.entity)
                DatabaseAction.DELETE -> deleteScoreById(score.entity.id)
                DatabaseAction.NO_ACTION -> {}
            }
        }
    }

    private fun deleteScoreById(id: Long) {
        addJobToDbChannel(
            block = {
                appRepository.deleteScoreById(id)
                gameCacheNeedsUpdate = true
                matchCacheNeedsUpdate = true
            }
        )
    }

    private fun insertScore(score: ScoreEntity) {
        addJobToDbChannel(
            block = {
                appRepository.insert(score)
                gameCacheNeedsUpdate = true
                matchCacheNeedsUpdate = true
            }
        )
    }

    private fun updateScore(updatedScore: ScoreEntity) {
        addJobToDbChannel(
            block = {
                appRepository.updateScore(updatedScore)
                gameCacheNeedsUpdate = true
                matchCacheNeedsUpdate = true
            }
        )
    }

    // endregion

    // region DB Helpers

    private fun addJobToDbChannel(block: suspend () -> Unit) {
        dbJobChannel.trySend(
            element = viewModelScope.launch(start = CoroutineStart.LAZY) {
                block()
            }
        )
    }

    fun executeDbOperation(operation: () -> Unit) {
        operation()
        consumeDbJobs()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun consumeDbJobs() {
        viewModelScope.launch {
            for (job in dbJobChannel) {
                job.join()
                if (dbJobChannel.isEmpty) {
                    updateGameCacheWithChanges()
                    updateMatchCacheWithChanges()
                }
            }
        }
    }

    private fun updateGameCacheWithChanges() {
        val requiredCachedGameObject = cachedGameObject
        if (gameCacheNeedsUpdate) {
            gameCacheNeedsUpdate = false
            addJobToDbChannel(
                block = {
                    cachedGameObject = games
                        .first()
                        .find { it.entity.id == requiredCachedGameObject.entity.id }
                        ?: EMPTY_GAME_OBJECT
                }
            )
        }
    }

    private fun updateMatchCacheWithChanges() {
        val requiredCachedMatchObject = cachedMatchObject
        if (matchCacheNeedsUpdate) {
            matchCacheNeedsUpdate = false
            addJobToDbChannel(
                block = {
                    cachedMatchObject = matches
                        .first()
                        .find { it.entity.id == requiredCachedMatchObject.entity.id }
                        ?: EMPTY_MATCH_OBJECT
                }
            )
        }
    }

    // endregion
}