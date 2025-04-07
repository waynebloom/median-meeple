package com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame

import com.waynebloom.scorekeeper.singleGame.StatisticsForGameUiState
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.ScoringPlayerDomainModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.WinningPlayerDomainModel
import java.math.BigDecimal

object StatisticsForGameSampleData {
	val DefaultState = StatisticsForGameUiState.Content(
		screenTitle = "Settlers of Catan",
		ads = emptyList(),
		matchCount = 100,
		playCount = 200,
		uniquePlayerCount = 10,
		isBestWinnerExpanded = true,
		playersWithMostWins = listOf(
			WinningPlayerDomainModel("Player 1", 8),
			WinningPlayerDomainModel("Player 2", 8),
		),
		playersWithMostWinsOverflow = 2,
		isHighScoreExpanded = false,
		playersWithHighScore = listOf(
			ScoringPlayerDomainModel("Player 1", BigDecimal(10)),
			ScoringPlayerDomainModel("Player 2", BigDecimal(10)),
			ScoringPlayerDomainModel("Player 3", BigDecimal(10))
		),
		playersWithHighScoreOverflow = 2,
		isUniqueWinnersExpanded = false,
		winners = listOf(
			WinningPlayerDomainModel("Player 1", 7),
			WinningPlayerDomainModel("Player 2", 7),
			WinningPlayerDomainModel("Player 3", 7)
		),
		winnersOverflow = 7,
		categoryNames = listOf("Longest Road", "Largest Army", "Buildings", "Development Cards"),
		indexOfSelectedCategory = 2,
		isCategoryDataEmpty = false,
		categoryTopScorers = listOf(
			ScoringPlayerDomainModel("Player 1", BigDecimal(10)),
			ScoringPlayerDomainModel("Player 2", BigDecimal(9)),
			ScoringPlayerDomainModel("Player 3", BigDecimal(8))
		),
		categoryLow = "2",
		categoryMean = "5",
		categoryRange = "8"
	)
}