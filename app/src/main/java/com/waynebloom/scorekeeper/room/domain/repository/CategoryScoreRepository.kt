package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryScoreRepository {
    @Query("DELETE FROM SUBSCORE WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM SUBSCORE WHERE player_id = :id")
    fun getByPlayerIdAsFlow(id: Long): Flow<List<CategoryScoreDataModel>>

    @Insert
    suspend fun insert(categoryScore: CategoryScoreDataModel): Long

    @Update
    suspend fun update(categoryScore: CategoryScoreDataModel)
}
