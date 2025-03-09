package com.waynebloom.scorekeeper.singleGame.statisticsForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun StatisticsForGameRoute(
	navController: NavHostController,
	viewModel: SingleGameViewModel,
) {

	val uiState by viewModel.statisticsForGameUiState.collectAsState()

	MedianMeepleTheme {
		StatisticsForGameScreen(
			uiState = uiState,
			onEditGameClick = {
				navController.navigate("${Destination.EditGame.route}/${viewModel.gameId}")
			},
			onMatchesTabClick = {
				val route = "${Destination.MatchesForGame.route}/${viewModel.gameId}"
				if (!navController.popBackStack(route = route, inclusive = false)) {
					navController.navigate(route)
				}
			},
			onBestWinnerButtonClick = viewModel::onBestWinnerButtonClick,
			onHighScoreButtonClick = viewModel::onHighScoreButtonClick,
			onUniqueWinnersButtonClick = viewModel::onUniqueWinnersButtonClick,
			onCategoryClick = viewModel::onCategoryClick
		)
	}
}
