package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import javax.inject.Inject

class UpdateCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: CategoryUiModel, gameId: Long) =
        categoryRepository.update(entity = category.toDataModel(gameId))
}

internal fun CategoryUiModel.toDataModel(gameId: Long) = CategoryDataModel(
    id = id,
    gameId = gameId,
    name = name.value.text,
    position = position
)