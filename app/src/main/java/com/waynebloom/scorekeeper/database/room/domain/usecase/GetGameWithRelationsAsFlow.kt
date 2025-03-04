package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.database.repository.GameRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGameWithRelationsAsFlow @Inject constructor(
	private val gameRepository: GameRepository,
	private val gameDataMapper: GameDataMapper,
) {

    operator fun invoke(id: Long) = gameRepository
        .getOneWithRelationsAsFlow(id)
        .map(gameDataMapper::mapWithRelations)
}
