package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameScreen
import kotlinx.serialization.Serializable

@Serializable
internal data class StatisticsForGame(val gameID: Long)

fun NavGraphBuilder.statisticsForGameDestination(
	getSharedViewModel: @Composable (NavBackStackEntry) -> SingleGameViewModel,
	onNavigateToEditGame: (Long) -> Unit,
	onNavigateToMatchesForGame: (Long) -> Unit,
) {
	composable<StatisticsForGame> {
		val viewModel = getSharedViewModel(it)
		val uiState by viewModel.statisticsForGameUiState.collectAsState()

		StatisticsForGameScreen(
			uiState = uiState,
			onEditGameClick = {
				// TODO: provide the game ID as an arg in this lambda
				onNavigateToEditGame(viewModel.gameID)
			},
			onMatchesTabClick = {
				// TODO: provide the game ID as an arg in this lambda
				onNavigateToMatchesForGame(viewModel.gameID)
			},
			onBestWinnerButtonClick = viewModel::onBestWinnerButtonClick,
			onHighScoreButtonClick = viewModel::onHighScoreButtonClick,
			onUniqueWinnersButtonClick = viewModel::onUniqueWinnersButtonClick,
			onCategoryClick = viewModel::onCategoryClick
		)
	}
}

fun NavController.navigateToStatisticsForGame(gameID: Long) {
	val route = StatisticsForGame(gameID)
	val opts = navOptions {
		launchSingleTop = true
	}

	navigate(route, opts)
}
