package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import javax.inject.Inject

class InsertScore @Inject constructor(
	private val scoreRepository: ScoreDao
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(categoryScore: ScoreDomainModel): Long {
		return scoreRepository
			.insert(
				ScoreDataModel(
					categoryId = categoryScore.categoryId,
					playerId = categoryScore.playerId,
					value = categoryScore.scoreAsTextFieldValue.text
				)
			)
	}
}
