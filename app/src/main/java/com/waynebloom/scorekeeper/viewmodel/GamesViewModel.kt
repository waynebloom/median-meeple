package com.waynebloom.scorekeeper.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.AdService
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.data.database.AppRepository
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
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

    var gameCache: DataObjectCache<GameObject> = DataObjectCache(GameObject())
    var matchCache: DataObjectCache<MatchObject> = DataObjectCache(MatchObject())
    var playerCache: DataObjectCache<PlayerObject> = DataObjectCache(PlayerObject())

    // endregion

    val adService = AdService(appObj)

    // region Game

    fun commitGameBundle(bundle: EntityStateBundle<GameEntity>) {
        when(bundle.databaseAction) {
            DatabaseAction.DELETE -> { deleteGameById(bundle.entity.id) }
            DatabaseAction.UPDATE -> { updateGame(bundle.entity) }
            else -> {}
        }
    }

    fun deleteGameById(id: Long) {
        addJobToDbChannel {
            appRepository.deleteGameById(id)
            clearGameCache()
        }
    }

    fun insertNewEmptyGame() {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            val defaultName = getApplication<Application>()
                .resources
                .getString(R.string.default_new_game_name)
            val insertedGameId = appRepository.insert(GameEntity(name = defaultName))
            gameCache.databaseEntityId = insertedGameId
        }
    }

    private fun updateGame(gameEntity: GameEntity) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateGame(gameEntity)
            /*gameCacheNeedsUpdate = true*/
        }
    }

    // endregion

    // region Match

    fun deleteMatchById(id: Long) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.deleteMatchById(id)
            clearMatchCache()
        }
    }

    fun insertNewEmptyMatch() {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        addJobToDbChannel {
            val insertedMatchId = appRepository.insert(
                MatchEntity(gameId = gameCache.databaseEntityId)
            )
            matchCache.databaseEntityId = insertedMatchId
        }
    }

    fun updateMatch(updatedMatch: MatchEntity) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateMatch(updatedMatch)
        }
    }

    // endregion

    // region Player

    fun commitPlayerBundle(bundle: EntityStateBundle<PlayerEntity>) {
        when(bundle.databaseAction) {
            DatabaseAction.DELETE -> { deletePlayerById(bundle.entity.id) }
            DatabaseAction.UPDATE -> { updatePlayer(bundle.entity) }
            else -> {}
        }
    }

    fun deletePlayerById(id: Long) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.deletePlayerById(id)
            clearPlayerCache()
        }
    }

    fun insertNewEmptyPlayer() {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            val defaultName = getApplication<Application>()
                .resources
                .getString(R.string.default_new_player_name)
            val insertedPlayerId = appRepository.insert(
                PlayerEntity(
                    matchId = matchCache.databaseEntityId,
                    name = defaultName
                )
            )
            playerCache.databaseEntityId = insertedPlayerId
        }
    }

    private fun updatePlayer(updatedPlayer: PlayerEntity) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updatePlayer(updatedPlayer)
        }
    }

    // endregion

    // region Subscore

    fun commitSubscoreBundles(bundles: List<SubscoreStateBundle>) {
        bundles.forEach {
            when(it.databaseAction) {
                DatabaseAction.INSERT -> { insertSubscore(it.entity) }
                DatabaseAction.UPDATE -> { updateSubscore(it.entity) }
                else -> {}
            }
        }
    }

    private fun insertSubscore(subscoreEntity: SubscoreEntity) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.insert(subscoreEntity)
        }
    }

    private fun updateSubscore(subscoreEntity: SubscoreEntity) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateSubscore(subscoreEntity)
        }
    }

    // endregion

    // region SubscoreTitle

    fun commitSubscoreTitleBundles(bundles: List<EntityStateBundle<SubscoreTitleEntity>>) {
        bundles.forEach {
            when(it.databaseAction) {
                DatabaseAction.DELETE -> { deleteSubscoreTitleById(it.entity.id) }
                DatabaseAction.INSERT -> { insertSubscoreTitle(it.entity) }
                DatabaseAction.UPDATE -> { updateSubscoreTitle(it.entity) }
                else -> {}
            }
        }
    }

    private fun deleteSubscoreTitleById(id: Long) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.deleteSubscoreTitleById(id)
        }
    }

    private fun insertSubscoreTitle(subscoreTitleEntity: SubscoreTitleEntity) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.insert(subscoreTitleEntity)
        }
    }

    private fun updateSubscoreTitle(subscoreTitleEntity: SubscoreTitleEntity) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateSubscoreTitle(subscoreTitleEntity)
        }
    }

    // endregion

    // region Cache Logic

    fun allCachesUpToDate() = !gameCache.needsUpdate
        && !matchCache.needsUpdate
        && !matchCache.needsUpdate

    private fun clearGameCache() {
        gameCache.apply {
            dataObject = GameObject()
            databaseEntityId = -1
        }
    }

    private fun clearMatchCache() {
        matchCache.apply {
            dataObject = MatchObject()
            databaseEntityId = -1
        }
    }

    private fun clearPlayerCache() {
        playerCache.apply {
            dataObject = PlayerObject()
            databaseEntityId = -1
        }
    }

    fun updateGameCacheById(id: Long, games: List<GameObject>) {
        gameCache.dataObject = games.find { it.entity.id == id } ?: GameObject()
        gameCache.databaseEntityId = id
    }

    fun updateMatchCacheById(id: Long, matches: List<MatchObject>) {
        matchCache.dataObject = matches.find { it.entity.id == id } ?: MatchObject()
        matchCache.databaseEntityId = id
    }

    fun updatePlayerCacheById(id: Long, players: List<PlayerObject>) {
        playerCache.dataObject = players.find { it.entity.id == id } ?: PlayerObject()
        playerCache.databaseEntityId = id
    }

    private fun updateGameCacheFromDatabase() {
        if (gameCache.needsUpdate) {
            addJobToDbChannel {
                gameCache.apply {
                    dataObject = appRepository.getGameById(databaseEntityId)
                    needsUpdate = false
                }
            }
        }
    }

    private fun updateMatchCacheFromDatabase() {
        if (matchCache.needsUpdate) {
            addJobToDbChannel {
                matchCache.apply {
                    dataObject = appRepository.getMatchById(databaseEntityId)
                    needsUpdate = false
                }
            }
        }
    }

    private fun updatePlayerCacheFromDatabase() {
        if (playerCache.needsUpdate) {
            addJobToDbChannel {
                playerCache.apply {
                    dataObject = appRepository.getPlayerById(databaseEntityId)
                    needsUpdate = false
                }
            }
        }
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
                    updateGameCacheFromDatabase()
                    updateMatchCacheFromDatabase()
                    updatePlayerCacheFromDatabase()
                }
            }
        }
    }

    // endregion
}