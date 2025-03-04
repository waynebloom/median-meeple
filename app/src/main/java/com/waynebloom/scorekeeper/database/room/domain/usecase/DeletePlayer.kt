package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.repository.PlayerRepository
import javax.inject.Inject

class DeletePlayer @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    suspend operator fun invoke(id: Long) = playerRepository.delete(id)
}
