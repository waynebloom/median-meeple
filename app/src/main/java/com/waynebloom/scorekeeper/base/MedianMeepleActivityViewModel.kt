package com.waynebloom.scorekeeper.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.admob.AdService
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.room.data.repository.AppRepository
import com.waynebloom.scorekeeper.room.domain.model.DataObjectCache
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreEntityState
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class MedianMeepleActivityViewModel(appObj: Application) : AndroidViewModel(appObj) {
    // region Data Access

    private val appRepository: AppRepository = AppRepository(appObj)

    private val _games = appRepository.getAllGames
    val games: Flow<List<GameDataRelationModel>>
        get() = _games

    private val _matches = appRepository.getAllMatches
    val matches: Flow<List<MatchDataRelationModel>>
        get() = _matches

    private val dbJobChannel = Channel<Job>(capacity = Channel.UNLIMITED)

    // endregion

    // region Object Caching

    var gameCache: DataObjectCache<GameDataRelationModel> = DataObjectCache(GameDataRelationModel())
    var matchCache: DataObjectCache<MatchDataRelationModel> = DataObjectCache(MatchDataRelationModel())
    var playerCache: DataObjectCache<PlayerDataRelationModel> = DataObjectCache(PlayerDataRelationModel())

    // endregion

    val adService = AdService(appObj)

    // region Game

    fun commitGameBundle(bundle: EntityStateBundle<GameDataModel>) {
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
            val insertedGameId = appRepository.insert(GameDataModel(name = defaultName))
            gameCache.databaseEntityId = insertedGameId
        }
    }

    private fun updateGame(gameEntity: GameDataModel) {
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
                MatchDataModel(gameId = gameCache.databaseEntityId)
            )
            matchCache.databaseEntityId = insertedMatchId
        }
    }

    fun updateMatch(updatedMatch: MatchDataModel) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateMatch(updatedMatch)
        }
    }

    // endregion

    // region Player

    fun commitPlayerBundle(bundle: EntityStateBundle<PlayerDataModel>) {
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
                PlayerDataModel(
                    matchId = matchCache.databaseEntityId,
                    name = defaultName
                )
            )
            playerCache.databaseEntityId = insertedPlayerId
        }
    }

    private fun updatePlayer(updatedPlayer: PlayerDataModel) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updatePlayer(updatedPlayer)
        }
    }

    // endregion

    // region Subscore

    fun commitSubscoreBundles(bundles: List<CategoryScoreEntityState>) {
        bundles.forEach {
            when(it.databaseAction) {
                DatabaseAction.INSERT -> { insertSubscore(it.entity) }
                DatabaseAction.UPDATE -> { updateSubscore(it.entity) }
                else -> {}
            }
        }
    }

    private fun insertSubscore(categoryScoreEntity: CategoryScoreDataModel) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.insert(categoryScoreEntity)
        }
    }

    private fun updateSubscore(categoryScoreEntity: CategoryScoreDataModel) {
        gameCache.needsUpdate = true
        matchCache.needsUpdate = true
        playerCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateCategoryScore(categoryScoreEntity)
        }
    }

    // endregion

    // region SubscoreTitle

    fun commitSubscoreTitleBundles(bundles: List<EntityStateBundle<CategoryDataModel>>) {
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
            appRepository.deleteCategoryTitleById(id)
        }
    }

    private fun insertSubscoreTitle(categoryEntity: CategoryDataModel) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.insert(categoryEntity)
        }
    }

    private fun updateSubscoreTitle(categoryEntity: CategoryDataModel) {
        gameCache.needsUpdate = true
        addJobToDbChannel {
            appRepository.updateCategoryTitle(categoryEntity)
        }
    }

    // endregion

    // region Cache Logic

    fun allCachesUpToDate() = !gameCache.needsUpdate
        && !matchCache.needsUpdate
        && !matchCache.needsUpdate

    private fun clearGameCache() {
        gameCache.apply {
            dataObject = GameDataRelationModel()
            databaseEntityId = -1
        }
    }

    private fun clearMatchCache() {
        matchCache.apply {
            dataObject = MatchDataRelationModel()
            databaseEntityId = -1
        }
    }

    private fun clearPlayerCache() {
        playerCache.apply {
            dataObject = PlayerDataRelationModel()
            databaseEntityId = -1
        }
    }

    fun updateGameCacheById(id: Long, games: List<GameDataRelationModel>) {
        gameCache.dataObject = games.find { it.entity.id == id } ?: GameDataRelationModel()
        gameCache.databaseEntityId = id
    }

    fun updateMatchCacheById(id: Long, matches: List<MatchDataRelationModel>) {
        matchCache.dataObject = matches.find { it.entity.id == id } ?: MatchDataRelationModel()
        matchCache.databaseEntityId = id
    }

    fun updatePlayerCacheById(id: Long, players: List<PlayerDataRelationModel>) {
        playerCache.dataObject = players.find { it.entity.id == id } ?: PlayerDataRelationModel()
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
