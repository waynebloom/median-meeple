package com.waynebloom.scorekeeper.room.data.repository

import android.app.Application
import com.waynebloom.scorekeeper.room.data.datasource.AppDatabase
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import kotlinx.coroutines.flow.Flow

// TODO: remove after full 'clean' integration
class AppRepository(application: Application) {

    private var appDao: AppDao

    init {
        val database = AppDatabase.getDatabase(application)
        appDao = database.appDao()
    }

    val getAllGames : Flow<List<GameDataRelationModel>> = appDao.getAllGames()
    val getAllMatches : Flow<List<MatchDataRelationModel>> = appDao.getAllMatches()

    // region Game

    suspend fun deleteGameById(id: Long) {
        appDao.deleteGameById(id)
    }

    suspend fun getGameById(id: Long) = appDao.getGameById(id)

    suspend fun insert(gameEntity: GameDataModel): Long {
        return appDao.insert(gameEntity)
    }

    suspend fun updateGame(gameEntity: GameDataModel) {
        appDao.updateGame(gameEntity)
    }

    // endregion

    // region Match

    suspend fun deleteMatchById(id: Long) {
        appDao.deleteMatchById(id)
    }

    suspend fun getMatchById(id: Long) = appDao.getMatchById(id)

    suspend fun insert(matchEntity: MatchDataModel) = appDao.insert(matchEntity)

    suspend fun updateMatch(matchEntity: MatchDataModel) {
        appDao.updateMatch(matchEntity)
    }

    // endregion

    // region Player

    suspend fun deletePlayerById(id: Long) {
        appDao.deletePlayerById(id)
    }

    suspend fun getPlayerById(id: Long) = appDao.getPlayerById(id)

    suspend fun insert(playerEntity: PlayerDataModel) = appDao.insert(playerEntity)

    suspend fun updatePlayer(playerEntity: PlayerDataModel) {
        appDao.updatePlayer(playerEntity)
    }

    // endregion

    // region Subscore

    suspend fun insert(categoryScoreEntity: CategoryScoreDataModel) = appDao.insertCategoryScore(categoryScoreEntity)

    suspend fun updateCategoryScore(categoryScoreEntity: CategoryScoreDataModel) {
        appDao.updateCategoryScore(categoryScoreEntity)
    }

    // endregion

    // region SubscoreTitle

    suspend fun deleteCategoryTitleById(id: Long) {
        appDao.deleteCategoryTitleById(id)
    }

    suspend fun insert(categoryEntity: CategoryDataModel) = appDao.insert(categoryEntity)

    suspend fun updateCategoryTitle(categoryEntity: CategoryDataModel) {
        appDao.updateCategoryTitle(categoryEntity)
    }

    // endregion
}
