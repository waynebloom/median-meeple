package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.waynebloom.scorekeeper.hub.HubScreen
import com.waynebloom.scorekeeper.hub.HubViewModel
import kotlinx.serialization.Serializable

@Serializable object Hub

fun NavGraphBuilder.hubDestination(
	onNavigateToScoreCard: (Long, Long) -> Unit,
) {
	composable<Hub> {
		val viewModel: HubViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()

		HubScreen(
			uiState,
			onGameClick = { gameID ->
				onNavigateToScoreCard(gameID, -1)
			},
			onAddQuickGameClick = viewModel::fetchNonFavoriteGamesWithMatchCount,
			onGameSelect = viewModel::addQuickGame,
		)
	}
}

fun NavController.navigateToHub(navOptions: NavOptions) {
	navigate(route = Hub, navOptions = navOptions)
}