package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.database.repository.PlayerRepository
import javax.inject.Inject

class UpdatePlayer @Inject constructor(
    private val playerRepository: PlayerRepository
) {

    suspend operator fun invoke(player: PlayerDomainModel) {
        playerRepository.update(
            PlayerDataModel(
                id = player.id,
                matchId = player.matchId,
                name = player.name,
                position = player.rank,
            )
        )
    }
}
