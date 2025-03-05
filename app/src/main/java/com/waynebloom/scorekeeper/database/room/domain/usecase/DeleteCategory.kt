package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import javax.inject.Inject

class DeleteCategory @Inject constructor(
    private val categoryRepository: CategoryDao
) {

    suspend operator fun invoke(id: Long) = categoryRepository.deleteById(id)
}