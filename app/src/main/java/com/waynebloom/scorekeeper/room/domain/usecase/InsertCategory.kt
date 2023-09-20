package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import javax.inject.Inject

class InsertCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: CategoryUiModel, gameId: Long) =
        categoryRepository.insert(entity = category.toDataModel(gameId = gameId))
}