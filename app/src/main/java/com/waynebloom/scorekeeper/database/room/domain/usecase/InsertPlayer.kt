package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.PlayerDao
import com.waynebloom.scorekeeper.database.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import javax.inject.Inject

class InsertPlayer @Inject constructor(
	private val playerRepository: PlayerDao
) {

	// FIXME: migrate dependents to new pattern
	suspend operator fun invoke(player: PlayerDomainModel): Long {
		return playerRepository
			.insert(
				PlayerDataModel(
					matchId = player.matchId,
					name = player.name,
					position = player.rank,
				)
			)
	}
}
