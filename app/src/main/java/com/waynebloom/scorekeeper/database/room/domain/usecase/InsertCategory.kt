package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.repository.CategoryRepository
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
