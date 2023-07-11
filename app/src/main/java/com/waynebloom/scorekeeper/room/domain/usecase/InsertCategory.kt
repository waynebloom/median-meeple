package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import javax.inject.Inject

class InsertCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(category: CategoryDomainModel, gameId: Long) =
        categoryRepository.insert(entity = category.toDataModel(gameId))
}
