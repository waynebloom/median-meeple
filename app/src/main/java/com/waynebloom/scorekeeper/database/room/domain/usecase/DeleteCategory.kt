package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) = categoryRepository.deleteById(id)
}