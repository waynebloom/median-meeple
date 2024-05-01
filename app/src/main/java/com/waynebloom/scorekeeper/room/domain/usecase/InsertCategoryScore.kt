package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryScoreRepository
import javax.inject.Inject

class InsertCategoryScore @Inject constructor(
    private val categoryScoreRepository: CategoryScoreRepository
) {
    suspend operator fun invoke(categoryScore: CategoryScoreDomainModel): Long {
        return categoryScoreRepository.insert(
            CategoryScoreDataModel(
                categoryId = categoryScore.categoryId,
                playerId = categoryScore.playerId,
                value = categoryScore.score.toString()
            )
        )
    }
}
