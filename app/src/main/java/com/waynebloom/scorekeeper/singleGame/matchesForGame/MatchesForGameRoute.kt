package com.waynebloom.scorekeeper.singleGame.matchesForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun MatchesForGameRoute(
	navController: NavHostController,
	viewModel: SingleGameViewModel,
) {

	val uiState by viewModel.matchesForGameUiState.collectAsState()

	MedianMeepleTheme {
		MatchesForGameScreen(
			uiState = uiState,
			onSearchInputChanged = viewModel::onSearchInputChanged,
			onSortModeChanged = viewModel::onSortModeChanged,
			onSortDirectionChanged = viewModel::onSortDirectionChanged,
			onEditGameClick = {
				navController.navigate("${Destination.EditGame.route}/${viewModel.gameId}")
			},
			onStatisticsTabClick = {
				val route = "${Destination.StatisticsForGame.route}/${viewModel.gameId}"
				if (!navController.popBackStack(route = route, inclusive = false)) {
					navController.navigate(route)
				}
			},
			onSortButtonClick = viewModel::onSortButtonClick,
			onMatchClick = {
				val route = "${Destination.ScoreCard.route}/${viewModel.gameId}/$it"
				navController.navigate(route)
			},
			onAddMatchClick = {
				val route = "${Destination.ScoreCard.route}/${viewModel.gameId}/-1"
				navController.navigate(route)
			},
			onSortDialogDismiss = viewModel::onSortDialogDismiss,
		)
	}
}
