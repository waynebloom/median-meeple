package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.GameDao
import com.waynebloom.scorekeeper.database.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import javax.inject.Inject

class UpdateGame @Inject constructor(
	private val gameRepository: GameDao
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(game: GameDomainModel) {
		gameRepository.update(game.toDataModel())
	}

	private fun GameDomainModel.toDataModel() = GameDataModel(
		id = id,
		color = displayColorIndex,
		name = name.text,
		scoringMode = scoringMode.ordinal,
		isFavorite = isFavorite
	)
}
