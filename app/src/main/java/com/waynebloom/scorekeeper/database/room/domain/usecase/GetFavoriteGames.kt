package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.database.repository.GameRepository
import javax.inject.Inject

class GetFavoriteGames @Inject constructor(
	private val gameRepository: GameRepository,
	private val gameDataMapper: GameDataMapper,
) {
	suspend operator fun invoke() = gameRepository
		.getFavorites()
		.map(gameDataMapper::map)
}