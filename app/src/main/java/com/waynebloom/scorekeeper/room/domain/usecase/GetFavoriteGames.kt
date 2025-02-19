package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetFavoriteGames @Inject constructor(
	private val gameRepository: GameRepository,
	private val gameDataMapper: GameDataMapper,
) {
	suspend operator fun invoke() = gameRepository
		.getFavorites()
		.map(gameDataMapper::map)
}