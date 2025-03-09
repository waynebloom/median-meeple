package com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model

import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameConstants
import java.math.BigDecimal
import java.math.RoundingMode

class StatisticsForCategory(
	val category: CategoryDomainModel,
	data: List<ScoringPlayerDomainModel>
) {

	val dataHighToLow: List<ScoringPlayerDomainModel>
	val topScorers: List<ScoringPlayerDomainModel>
	val low: BigDecimal
	val mean: BigDecimal
	val range: BigDecimal

	init {
		dataHighToLow = data.sortedByDescending { it.score }
		topScorers = dataHighToLow
			.take(StatisticsForGameConstants.numberOfTopScorers)
		low = dataHighToLow.last().score
		mean = calculateMean()
		range = calculateRange()
	}

	private fun calculateMean() = dataHighToLow
		.sumOf { it.score }
		.divide(dataHighToLow.size.toBigDecimal(), Constants.maximumDecimalPlaces, RoundingMode.HALF_UP)

	private fun calculateRange() = dataHighToLow.first().score - dataHighToLow.last().score
}
