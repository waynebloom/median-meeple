package com.waynebloom.scorekeeper.data.database

import android.app.Application
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.subscore.CategoryScoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import kotlinx.coroutines.flow.Flow

class AppRepository(application: Application) {
    private var appDao: AppDao

    init {
        val database = AppDatabase.getDatabase(application)
        appDao = database.appDao()
    }

    val getAllGames : Flow<List<GameObject>> = appDao.getAllGames()
    val getAllMatches : Flow<List<MatchObject>> = appDao.getAllMatches()

    // region Game

    suspend fun deleteGameById(id: Long) {
        appDao.deleteGameById(id)
    }

    suspend fun getGameById(id: Long) = appDao.getGameById(id)

    suspend fun insert(gameEntity: GameEntity): Long {
        return appDao.insert(gameEntity)
    }

    suspend fun updateGame(gameEntity: GameEntity) {
        appDao.updateGame(gameEntity)
    }

    // endregion

    // region Match

    suspend fun deleteMatchById(id: Long) {
        appDao.deleteMatchById(id)
    }

    suspend fun getMatchById(id: Long) = appDao.getMatchById(id)

    suspend fun insert(matchEntity: MatchEntity) = appDao.insert(matchEntity)

    suspend fun updateMatch(matchEntity: MatchEntity) {
        appDao.updateMatch(matchEntity)
    }

    // endregion

    // region Player

    suspend fun deletePlayerById(id: Long) {
        appDao.deletePlayerById(id)
    }

    suspend fun getPlayerById(id: Long) = appDao.getPlayerById(id)

    suspend fun insert(playerEntity: PlayerEntity) = appDao.insert(playerEntity)

    suspend fun updatePlayer(playerEntity: PlayerEntity) {
        appDao.updatePlayer(playerEntity)
    }

    // endregion

    // region Subscore

    suspend fun insert(categoryScoreEntity: CategoryScoreEntity) = appDao.insertCategoryScore(categoryScoreEntity)

    suspend fun updateCategoryScore(categoryScoreEntity: CategoryScoreEntity) {
        appDao.updateCategoryScore(categoryScoreEntity)
    }

    // endregion

    // region SubscoreTitle

    suspend fun deleteCategoryTitleById(id: Long) {
        appDao.deleteCategoryTitleById(id)
    }

    suspend fun insert(categoryTitleEntity: CategoryTitleEntity) = appDao.insert(categoryTitleEntity)

    suspend fun updateCategoryTitle(categoryTitleEntity: CategoryTitleEntity) {
        appDao.updateCategoryTitle(categoryTitleEntity)
    }

    // endregion
}
