package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.ui.model.GameUiModel
import javax.inject.Inject

class GetGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(id: Long): GameUiModel = gameRepository.get(id).entity
        .let { gameEntity ->
            GameUiModel(
                id = id,
                color = gameEntity.color,
                name = gameEntity.name.toTextFieldInput(),
                scoringMode = gameEntity.scoringMode.toScoringMode()
            )
        }
}