package com.waynebloom.scorekeeper.room.domain.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGame @Inject constructor(
	private val gameRepository: GameRepository
) {

	suspend operator fun invoke(id: Long): GameDomainModel = gameRepository.getOne(id)
		.let { game ->
			GameDomainModel(
				id = id,
				displayColorIndex = game.color,
				name = TextFieldValue(game.name),
				scoringMode = game.scoringMode.toScoringMode(),
				isFavorite = game.isFavorite,
			)
		}
}
