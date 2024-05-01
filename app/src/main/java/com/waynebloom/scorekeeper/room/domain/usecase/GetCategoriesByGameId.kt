package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.ext.toTextFieldValue
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import javax.inject.Inject

class GetCategoriesByGameId @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) = categoryRepository.getByGameId(id)
        .map {
            CategoryDomainModel(
                id = it.id,
                name = it.name.toTextFieldValue(),
                position = it.position
            )
        }
}
