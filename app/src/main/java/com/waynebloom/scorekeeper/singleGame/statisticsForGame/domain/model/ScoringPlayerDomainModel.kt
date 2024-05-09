package com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model

import java.math.BigDecimal

data class ScoringPlayerDomainModel(
    val name: String,
    val score: BigDecimal
)
