package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class GetMatchesAsFlow @Inject constructor(
    private val matchRepository: MatchRepository
) {

    operator fun invoke() = matchRepository.getAllAsFlow()
}
