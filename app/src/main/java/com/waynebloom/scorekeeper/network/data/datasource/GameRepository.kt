package com.waynebloom.scorekeeper.network.data.datasource

import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel

interface GameRepository {
	suspend fun getGames(): List<GameDataModel>
}