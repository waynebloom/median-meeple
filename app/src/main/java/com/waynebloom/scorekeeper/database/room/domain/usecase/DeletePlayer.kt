package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import javax.inject.Inject

class DeletePlayer @Inject constructor(
    private val playerRepository: PlayerDao
) {
    suspend operator fun invoke(id: Long) = playerRepository.delete(id)
}
