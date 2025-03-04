package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.repository.MatchRepository
import javax.inject.Inject

class DeleteMatch @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(id: Long) = matchRepository.delete(id)
}
