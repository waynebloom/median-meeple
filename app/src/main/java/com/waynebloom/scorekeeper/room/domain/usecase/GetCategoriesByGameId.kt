package com.waynebloom.scorekeeper.room.domain.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import javax.inject.Inject

class GetCategoriesByGameId @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) = categoryRepository.getByGameId(id)
        .map {
            CategoryDomainModel(
                id = it.id,
                name = TextFieldValue(it.name),
                position = it.position
            )
        }
}
