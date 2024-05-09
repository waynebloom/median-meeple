package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.PlayerDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetPlayerWithRelationsAsFlow @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val playerDataMapper: PlayerDataMapper,
) {

    operator fun invoke(id: Long) = playerRepository
        .getOneWithRelations(id)
        .map(playerDataMapper::mapWithRelations)
}
