package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class InsertGame @Inject constructor(
    private val gameRepository: GameRepository,
) {

    suspend operator fun invoke(game: GameDomainModel): Long {
        return gameRepository.insert(
            GameDataModel(
                name = game.name.text,
                color = game.displayColorIndex,
                scoringMode = game.scoringMode.ordinal,
            )
        )
    }
}
