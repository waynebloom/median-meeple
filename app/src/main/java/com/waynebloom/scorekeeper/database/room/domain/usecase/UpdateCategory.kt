package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import javax.inject.Inject

class UpdateCategory @Inject constructor(
	private val categoryRepository: CategoryDao
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(category: CategoryDomainModel, gameId: Long) =
		categoryRepository.update(
			CategoryDataModel(
				id = category.id,
				gameId = gameId,
				name = category.name.text,
				position = category.position
			)
		)
}
