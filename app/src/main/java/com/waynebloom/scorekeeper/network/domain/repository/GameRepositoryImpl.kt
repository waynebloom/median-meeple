package com.waynebloom.scorekeeper.network.domain.repository

import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.network.data.datasource.GameRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
	private val postgrest: Postgrest
) : GameRepository {

	override suspend fun getGames(): List<GameDataModel> {
		return withContext(Dispatchers.IO) {
			val columns = Columns.raw(
				"""
				id,
				name,
				color,
				scoring_mode,
				is_favorite
			""".trimIndent()
			)
			postgrest.from("GAME")
				.select(columns)
				.decodeList<GameDataModel>()
		}
	}
}
