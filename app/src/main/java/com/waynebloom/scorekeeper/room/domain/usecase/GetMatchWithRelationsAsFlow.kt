package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.MatchDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMatchWithRelationsAsFlow @Inject constructor(
    private val matchRepository: MatchRepository,
    private val matchDataMapper: MatchDataMapper,
) {

    operator fun invoke(id: Long) = matchRepository
        .getOneWithRelationsAsFlow(id)
        .map(matchDataMapper::mapWithRelations)
}
