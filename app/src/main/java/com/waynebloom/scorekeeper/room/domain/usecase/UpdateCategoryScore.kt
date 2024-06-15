package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryScoreRepository
import javax.inject.Inject

class UpdateCategoryScore @Inject constructor(
    private val categoryScoreRepository: CategoryScoreRepository
) {

    suspend operator fun invoke(categoryScore: CategoryScoreDomainModel) {
        categoryScoreRepository.update(
            CategoryScoreDataModel(
                id = categoryScore.id,
                playerId = categoryScore.playerId,
                categoryId = categoryScore.categoryId,
                value = categoryScore.scoreAsTextFieldValue.text
            )
        )
    }
}
