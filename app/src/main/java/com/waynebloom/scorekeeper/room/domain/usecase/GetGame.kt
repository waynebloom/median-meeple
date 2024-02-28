package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import javax.inject.Inject

class GetGame @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(id: Long): GameDomainModel = gameRepository.getOne(id)
        .let { game ->
            GameDomainModel(
                id = id,
                color = game.color,
                name = game.name.toTextFieldInput(),
                scoringMode = game.scoringMode.toScoringMode()
            )
        }
}
