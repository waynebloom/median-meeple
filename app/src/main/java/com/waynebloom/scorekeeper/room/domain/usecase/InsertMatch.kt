package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class InsertMatch @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(match: MatchDomainModel): Long {
        return matchRepository.insert(
            MatchDataModel(
                gameId = match.gameId,
                notes = match.notes
            )
        )
    }
}
