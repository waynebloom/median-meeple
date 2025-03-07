package com.waynebloom.scorekeeper.database.domain

import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import javax.inject.Inject

class PlayerRepository @Inject constructor(
    private val playerDao: PlayerDao,
) {

}
