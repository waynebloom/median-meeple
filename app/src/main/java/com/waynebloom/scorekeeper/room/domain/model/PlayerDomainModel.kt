package com.waynebloom.scorekeeper.room.domain.model

data class PlayerDomainModel(
    val id: Long = -1,
    val matchId: Long = -1,
    val categoryScores: List<CategoryScoreDomainModel> = emptyList(),
    val name: String = "New Player",
    val rank: Int = -1,
)
