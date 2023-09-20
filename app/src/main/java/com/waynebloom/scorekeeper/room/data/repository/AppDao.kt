package com.waynebloom.scorekeeper.room.data.repository

import androidx.room.*
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import kotlinx.coroutines.flow.Flow

// TODO: remove after full 'clean' integration
@Dao
interface AppDao {

    // region Game

    @Query("DELETE FROM game WHERE id = :id")
    suspend fun deleteGameById(id: Long)

    @Transaction
    @Query("SELECT * FROM game")
    fun getAllGames(): Flow<List<GameDataRelationModel>>

    @Transaction
    @Query("SELECT * FROM game WHERE id = :id")
    suspend fun getGameById(id: Long): GameDataRelationModel

    @Insert
    suspend fun insert(gameEntity: GameDataModel): Long

    @Update
    suspend fun updateGame(gameEntity: GameDataModel)

    // endregion

    // region Match

    @Query("DELETE FROM `match` WHERE id = :id")
    suspend fun deleteMatchById(id: Long)

    @Transaction
    @Query("SELECT * FROM `match`")
    fun getAllMatches(): Flow<List<MatchDataRelationModel>>

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    suspend fun getMatchById(id: Long): MatchDataRelationModel

    @Insert
    suspend fun insert(matchEntity: MatchDataModel): Long

    @Update
    suspend fun updateMatch(matchEntity: MatchDataModel)

    // endregion

    // region Player

    @Query("DELETE FROM player WHERE id = :id")
    suspend fun deletePlayerById(id: Long)

    @Transaction
    @Query("SELECT * FROM Player WHERE id = :id")
    suspend fun getPlayerById(id: Long): PlayerDataRelationModel

    @Insert
    suspend fun insert(playerEntity: PlayerDataModel): Long

    @Update
    suspend fun updatePlayer(playerEntity: PlayerDataModel)

    // endregion

    // region Subscore

    @Insert
    suspend fun insertCategoryScore(categoryScoreEntity: CategoryScoreDataModel): Long

    @Update
    suspend fun updateCategoryScore(categoryScoreEntity: CategoryScoreDataModel)

    // endregion

    // region SubscoreTitle

    @Query("DELETE FROM subscoretitle WHERE id = :id")
    suspend fun deleteCategoryTitleById(id: Long)

    @Insert
    suspend fun insert(categoryEntity: CategoryDataModel): Long

    @Update
    suspend fun updateCategoryTitle(categoryEntity: CategoryDataModel)

    // endregion
}
