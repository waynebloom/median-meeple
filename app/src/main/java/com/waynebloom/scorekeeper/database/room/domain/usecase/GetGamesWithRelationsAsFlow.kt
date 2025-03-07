package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.domain.mapper.GameMapper
import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGamesWithRelationsAsFlow @Inject constructor(
	private val gameRepository: GameDao,
	private val gameMapper: GameMapper
) {

    operator fun invoke() = gameRepository
        .getAllWithRelations()
        .map { games ->
            games.map(gameMapper::toDomainWithRelations)
        }
}
