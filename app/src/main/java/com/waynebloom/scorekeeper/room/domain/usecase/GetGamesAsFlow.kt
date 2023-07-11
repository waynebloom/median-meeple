package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGamesAsFlow @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameDataMapper: GameDataMapper
) {

    operator fun invoke() = gameRepository
        .getAllAsFlow()
        .map { games ->
            games.map(gameDataMapper::mapWithRelations)
        }
}
