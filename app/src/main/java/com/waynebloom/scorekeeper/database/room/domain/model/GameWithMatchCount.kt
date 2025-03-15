package com.waynebloom.scorekeeper.database.room.domain.model

data class GameWithMatchCount(
	val game: GameDomainModel,
	val matchCount: Int,
)
