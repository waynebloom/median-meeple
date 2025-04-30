package com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model

import java.math.BigDecimal

data class ScoringPlayerDomainModel(
	val name: String,
	val score: BigDecimal
)
