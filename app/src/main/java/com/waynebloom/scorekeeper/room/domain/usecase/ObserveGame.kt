package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class ObserveGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(id: Long) = gameRepository.observe(id)
}