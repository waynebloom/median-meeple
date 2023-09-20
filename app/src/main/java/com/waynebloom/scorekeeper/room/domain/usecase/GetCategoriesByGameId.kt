package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import javax.inject.Inject

class GetCategoriesByGameId @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) = categoryRepository.getByGameId(id)
        .map {
            CategoryUiModel(
                id = it.id,
                name = it.name.toTextFieldInput(),
                position = it.position
            )
        }
}