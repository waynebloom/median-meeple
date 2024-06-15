package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class UpdateMatch @Inject constructor(
    private val matchRepository: MatchRepository
) {

    suspend operator fun invoke(match: MatchDomainModel) {
        matchRepository.update(MatchDataModel(
            id = match.id,
            gameId = match.gameId,
            notes = match.notes,
            dateMillis = match.dateMillis,
            location = match.location
        ))
    }
}
