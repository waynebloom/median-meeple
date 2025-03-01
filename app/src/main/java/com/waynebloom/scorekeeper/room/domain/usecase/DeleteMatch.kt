package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class DeleteMatch @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(id: Long) = matchRepository.delete(id)
}
