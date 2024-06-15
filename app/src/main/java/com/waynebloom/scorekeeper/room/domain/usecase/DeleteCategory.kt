package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategory @Inject constructor(
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(id: Long) = categoryRepository.deleteById(id)
}