package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import javax.inject.Inject

class InsertGame @Inject constructor(
	private val gameRepository: GameDao,
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(game: GameDomainModel): Long {
		return gameRepository
			.upsert(
				GameDataModel(
					name = game.name.text,
					color = game.displayColorIndex,
					scoringMode = game.scoringMode.ordinal,
				)
			)
	}
}
