package com.waynebloom.scorekeeper.room.domain.repository

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel

@Dao
interface MatchRepository {

    @Transaction
    @Query("SELECT * FROM `match` WHERE id = :id")
    suspend fun get(id: Long): MatchDataRelationModel

    @Transaction
    @Query("SELECT * FROM `match`")
    suspend fun getAll(): List<MatchDataRelationModel>
}