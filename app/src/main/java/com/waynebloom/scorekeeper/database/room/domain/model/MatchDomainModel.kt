package com.waynebloom.scorekeeper.database.room.domain.model

data class MatchDomainModel(
    val id: Long = -1,
    val gameId: Long = -1,
    val notes: String = "",
    val location: String = "",
    val dateMillis: Long = 0,
    val players: List<PlayerDomainModel> = listOf()
)
