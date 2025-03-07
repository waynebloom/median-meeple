package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.CategoryDao
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import javax.inject.Inject

class InsertCategory @Inject constructor(
	private val categoryRepository: CategoryDao
) {

	operator fun invoke(category: CategoryDomainModel, gameId: Long) =
		categoryRepository.insert(
			CategoryDataModel(
				gameId = gameId,
				name = category.name.text,
				position = category.position
			)
		)
}
