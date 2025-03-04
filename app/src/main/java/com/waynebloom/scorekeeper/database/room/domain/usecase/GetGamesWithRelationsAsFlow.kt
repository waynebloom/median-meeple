package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.database.repository.GameRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGamesWithRelationsAsFlow @Inject constructor(
	private val gameRepository: GameRepository,
	private val gameDataMapper: GameDataMapper
) {

    operator fun invoke() = gameRepository
        .getAllAsFlow()
        .map { games ->
            games.map(gameDataMapper::mapWithRelations)
        }
}
