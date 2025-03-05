package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.database.repository.MatchRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import javax.inject.Inject

class InsertMatch @Inject constructor(
    private val matchRepository: MatchDao
) {

    suspend operator fun invoke(match: MatchDomainModel): Long {
        return matchRepository.insert(
            MatchDataModel(
                gameId = match.gameId,
                notes = match.notes,
                dateMillis = match.dateMillis,
                location = match.location
            )
        )
    }
}
