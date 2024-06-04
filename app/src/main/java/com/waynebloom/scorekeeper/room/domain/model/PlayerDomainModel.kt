package com.waynebloom.scorekeeper.room.domain.model

import java.math.BigDecimal

data class PlayerDomainModel(
    val id: Long = -1,
    val matchId: Long = -1,
    val categoryScores: List<CategoryScoreDomainModel> = emptyList(),
    val name: String = "New Player",
    val rank: Int = -1,
    val useCategorizedScore: Boolean = false,
    val totalScore: BigDecimal = BigDecimal.ZERO,
)
