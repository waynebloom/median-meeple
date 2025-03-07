package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.datasource.MatchDao
import com.waynebloom.scorekeeper.database.room.domain.mapper.MatchDataMapper
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetMatch @Inject constructor(
	private val matchRepository: MatchDao,
	private val matchDataMapper: MatchDataMapper,
) {

	suspend operator fun invoke(id: Long) = matchDataMapper.map(
		matchRepository
			.getOne(id)
			// FIXME: migrate dependents to new pattern
			.first()
	)
}
