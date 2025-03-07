package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGame @Inject constructor(
	private val gameRepository: GameDao,
	private val gameMapper: GameMapper,
) {

	suspend operator fun invoke(id: Long): GameDomainModel = gameRepository.getOne(id)
		.map(gameMapper::toDomain)
		// FIXME: migrate dependents to new pattern
		.first()
}
