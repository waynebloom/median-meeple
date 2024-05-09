package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.MatchDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.MatchRepository
import javax.inject.Inject

class GetMatchWithRelations @Inject constructor(
    private val matchRepository: MatchRepository,
    private val matchDataMapper: MatchDataMapper,
) {

    suspend operator fun invoke(id: Long) = matchDataMapper.mapWithRelations(
        matchRepository.getOneWithRelations(id)
    )
}
