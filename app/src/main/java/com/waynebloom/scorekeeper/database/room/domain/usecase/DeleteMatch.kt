package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.repository.MatchRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import javax.inject.Inject

class DeleteMatch @Inject constructor(
    private val matchRepository: MatchDao
) {
    suspend operator fun invoke(id: Long) = matchRepository.delete(id)
}
