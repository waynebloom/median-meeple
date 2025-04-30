package com.waynebloom.scorekeeper.database.room.domain.model

data class PlayerDomainModel(
	val id: Long = -1,
	val matchID: Long = -1,
	val categoryScores: List<ScoreDomainModel> = emptyList(),
	val name: String = "New Player",
	val position: Int = -1,
)
