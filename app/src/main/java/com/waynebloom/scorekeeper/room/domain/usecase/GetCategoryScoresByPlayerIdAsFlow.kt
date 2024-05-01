package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.mapper.CategoryScoreDataMapper
import com.waynebloom.scorekeeper.room.domain.repository.CategoryScoreRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCategoryScoresByPlayerIdAsFlow @Inject constructor(
    private val categoryScoreRepository: CategoryScoreRepository,
    private val categoryScoreDataMapper: CategoryScoreDataMapper,
) {
    operator fun invoke(playerId: Long) = categoryScoreRepository
        .getByPlayerIdAsFlow(playerId)
        .map { categoryScores ->
            categoryScores.map(categoryScoreDataMapper::map)
        }
}
