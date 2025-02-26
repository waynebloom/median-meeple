package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class UpdateGame @Inject constructor(
	private val gameRepository: GameRepository
) {

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
