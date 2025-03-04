package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import javax.inject.Inject

class ScoreRepository @Inject constructor(
    private val scoreDao: ScoreDao
) {

}
