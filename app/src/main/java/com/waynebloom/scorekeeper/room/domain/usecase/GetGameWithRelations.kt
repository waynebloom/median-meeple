package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.GameDataMapper
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGameWithRelations @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameDataMapper: GameDataMapper
) {

    suspend operator fun invoke(id: Long): GameDomainModel {
        return gameDataMapper.mapWithRelations(gameData = gameRepository.getOneWithRelations(id))
    }
}
