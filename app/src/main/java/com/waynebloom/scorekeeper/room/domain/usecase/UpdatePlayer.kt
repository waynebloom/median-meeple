package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.PlayerRepository
import javax.inject.Inject

class UpdatePlayer @Inject constructor(
    private val playerRepository: PlayerRepository
) {

    suspend operator fun invoke(player: PlayerDomainModel) {
        playerRepository.update(
            PlayerDataModel(
                id = player.id,
                matchId = player.matchId,
                name = player.name.text,
                position = player.position,
                showDetailedScore = player.useCategorizedScore,
                totalScore = player.totalScore.toString()
            )
        )
    }
}
