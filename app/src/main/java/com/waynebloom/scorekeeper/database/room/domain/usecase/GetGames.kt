package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetGames @Inject constructor(
	private val gamesRepository: GameDao,
	private val gameMapper: GameMapper,
) {
	suspend operator fun invoke() = gamesRepository
		.getAll()
		// FIXME: migrate dependents to new pattern
		.first()
		.map(gameMapper::toDomain)
}