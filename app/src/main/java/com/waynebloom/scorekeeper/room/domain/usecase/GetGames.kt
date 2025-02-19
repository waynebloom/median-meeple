package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGames @Inject constructor(
	private val gamesRepository: GameRepository,
	private val gameDataMapper: GameDataMapper,
) {
	suspend operator fun invoke() = gamesRepository
		.getAll()
		.map(gameDataMapper::map)
}