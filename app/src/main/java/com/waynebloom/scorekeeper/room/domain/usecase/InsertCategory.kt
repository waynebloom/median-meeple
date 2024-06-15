package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import javax.inject.Inject

class InsertCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: CategoryDomainModel, gameId: Long) =
        categoryRepository.insert(
            CategoryDataModel(
                gameId = gameId,
                name = category.name.text,
                position = category.position
            )
        )
}
