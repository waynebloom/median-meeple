package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.waynebloom.scorekeeper.feature.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.feature.singleGame.matchesForGame.MatchesForGameScreen
import kotlinx.serialization.Serializable

@Serializable data class MatchesForGame(
	val gameID: Long,
)

fun NavGraphBuilder.matchesForGameDestination(
	getSharedViewModel: @Composable (NavBackStackEntry) -> SingleGameViewModel,
	onNavigateToEditGame: (Long) -> Unit,
	onNavigateToStatisticsForGame: (Long) -> Unit,
	onNavigateToScoreCard: (Long, Long) -> Unit,
) {
	composable<MatchesForGame> {
		val viewModel = getSharedViewModel(it)
		val uiState by viewModel.matchesForGameUiState.collectAsState()

		MatchesForGameScreen(
			uiState = uiState,
			onSearchInputChanged = viewModel::onSearchInputChanged,
			onSortModeChanged = viewModel::onSortModeChanged,
			onSortDirectionChanged = viewModel::onSortDirectionChanged,
			onEditGameClick = {
				onNavigateToEditGame(viewModel.gameID)
			},
			onStatisticsTabClick = {
				onNavigateToStatisticsForGame(viewModel.gameID)
			},
			onSortButtonClick = viewModel::onSortButtonClick,
			onMatchClick = { matchID ->
				onNavigateToScoreCard(viewModel.gameID, matchID)
			},
			onAddMatchClick = {
				onNavigateToScoreCard(viewModel.gameID, -1)
			},
			onSortDialogDismiss = viewModel::onSortDialogDismiss,
		)
	}
}

fun NavController.navigateToMatchesForGame(gameID: Long) {
	val route = MatchesForGame(gameID)
	val opts = navOptions {
		launchSingleTop = true
	}

	navigate(route, opts)
}
