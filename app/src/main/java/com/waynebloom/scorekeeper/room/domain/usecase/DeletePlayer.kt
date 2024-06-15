package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository
import javax.inject.Inject

class DeletePlayer @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(id: Long) = playerRepository.delete(id)
}
