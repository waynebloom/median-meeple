package com.waynebloom.scorekeeper.database.room.domain.model

data class PlayerDomainModel(
	val id: Long = -1,
	val matchId: Long = -1,
	val categoryScores: List<ScoreDomainModel> = emptyList(),
	val name: String = "New Player",
	val rank: Int = -1,
)
