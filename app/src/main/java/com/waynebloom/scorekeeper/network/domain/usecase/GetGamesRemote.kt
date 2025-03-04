package com.waynebloom.scorekeeper.network.domain.usecase

import com.waynebloom.scorekeeper.network.domain.repository.GameRepositoryImpl
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import javax.inject.Inject

class GetGamesRemote @Inject constructor(
	private val gameRepository: GameRepositoryImpl,
	private val gameDataMapper: GameDataMapper,
) {

	suspend operator fun invoke(): List<GameDomainModel> {
		return gameRepository.getGames().map(gameDataMapper::map)
	}
}