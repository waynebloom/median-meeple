package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGamesAsFlow @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke() = gameRepository.getAllAsFlow()
}
