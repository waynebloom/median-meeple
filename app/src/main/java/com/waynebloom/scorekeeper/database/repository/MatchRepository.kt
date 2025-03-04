package com.waynebloom.scorekeeper.database.repository

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import javax.inject.Inject

class MatchRepository @Inject constructor(
    private val matchDao: MatchDao,
) {

}
