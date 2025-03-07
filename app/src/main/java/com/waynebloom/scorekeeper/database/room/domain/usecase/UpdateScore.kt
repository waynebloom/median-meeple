package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import javax.inject.Inject

class UpdateScore @Inject constructor(
	private val scoreRepository: ScoreDao
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(categoryScore: ScoreDomainModel) {
		scoreRepository.update(
			ScoreDataModel(
				id = categoryScore.id,
				playerId = categoryScore.playerId,
				categoryId = categoryScore.categoryId,
				value = categoryScore.scoreAsTextFieldValue.text
			)
		)
	}
}
