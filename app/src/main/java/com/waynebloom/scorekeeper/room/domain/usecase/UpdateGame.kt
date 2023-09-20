package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.ui.model.GameUiModel
import javax.inject.Inject

class UpdateGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(game: GameUiModel) =
        gameRepository.update(game.toDataModel())

    private fun GameUiModel.toDataModel() = GameDataModel(
        id = id,
        color = color,
        name = name.value.text,
        scoringMode = scoringMode.ordinal
    )
}