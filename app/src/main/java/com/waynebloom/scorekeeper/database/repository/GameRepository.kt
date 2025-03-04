package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import javax.inject.Inject

class GameRepository @Inject constructor(
    private val gameDao: GameDao,
) {

}
