package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.waynebloom.scorekeeper.feature.hub.HubScreen
import com.waynebloom.scorekeeper.feature.hub.HubViewModel
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
			onQuickGameClick = { gameID ->
				onNavigateToScoreCard(gameID, -1)
			},
			onRemoveQuickGame = viewModel::removeQuickGame,
			onAddQuickGameClick = viewModel::fetchNonFavoriteGamesWithMatchCount,
			onPickNewQuickGame = viewModel::addQuickGame,
		)
	}
}

fun NavController.navigateToHub(navOptions: NavOptions) {
	navigate(route = Hub, navOptions = navOptions)
}