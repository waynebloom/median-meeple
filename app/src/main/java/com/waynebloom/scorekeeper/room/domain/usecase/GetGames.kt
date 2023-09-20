package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGames @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke() = gameRepository.getAll()
}