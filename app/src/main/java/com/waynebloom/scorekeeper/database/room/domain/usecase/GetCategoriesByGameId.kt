package com.waynebloom.scorekeeper.database.room.domain.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.repository.CategoryRepository
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
