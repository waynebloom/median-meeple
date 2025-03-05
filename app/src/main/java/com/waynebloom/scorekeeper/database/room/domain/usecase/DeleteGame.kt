package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.repository.GameRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import javax.inject.Inject

class DeleteGame @Inject constructor(
    private val gameRepository: GameDao
) {
    suspend operator fun invoke(game: GameDataModel) = gameRepository.delete(game)

    suspend operator fun invoke(id: Long) = gameRepository.delete(id)
}