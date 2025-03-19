package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import kotlinx.serialization.Serializable

@Serializable data class SingleGame(val gameID: Long)

fun NavGraphBuilder.singleGameSection(
	getSharedViewModel: @Composable (NavBackStackEntry) -> SingleGameViewModel,
	onNavigateToEditGame: (Long) -> Unit,
	onNavigateToMatchesForGame: (Long) -> Unit,
	onNavigateToStatisticsForGame: (Long) -> Unit,
	onNavigateToScoreCard: (Long, Long) -> Unit,
) {

	navigation<SingleGame>(startDestination = MatchesForGame::class) {

		matchesForGameDestination(
			getSharedViewModel,
			onNavigateToEditGame,
			onNavigateToStatisticsForGame,
			onNavigateToScoreCard,
		)

		statisticsForGameDestination(
			getSharedViewModel,
			onNavigateToEditGame,
			onNavigateToMatchesForGame,
		)
	}
}