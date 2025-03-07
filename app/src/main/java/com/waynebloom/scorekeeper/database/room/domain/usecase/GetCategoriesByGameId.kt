package com.waynebloom.scorekeeper.database.room.domain.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCategoriesByGameId @Inject constructor(
    private val categoryRepository: CategoryDao
) {

    suspend operator fun invoke(id: Long) = categoryRepository.getByGameId(id)
        // FIXME: migrate dependents to new pattern
        .first()
        .map {
            CategoryDomainModel(
                id = it.id,
                name = TextFieldValue(it.name),
                position = it.position
            )
        }
}
