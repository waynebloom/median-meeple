package com.waynebloom.scorekeeper.network.domain.usecase

import com.waynebloom.scorekeeper.network.domain.repository.GameRepositoryImpl
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import javax.inject.Inject

class GetGamesRemote @Inject constructor(
	private val gameRepository: GameRepositoryImpl,
	private val gameMapper: GameMapper,
) {

	suspend operator fun invoke(): List<GameDomainModel> {
		return gameRepository.getGames().map(gameMapper::toDomain)
	}
}