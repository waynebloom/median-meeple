package com.waynebloom.scorekeeper.hub

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun HubRoute(
	navController: NavHostController,
	viewModel: HubViewModel = hiltViewModel()
) {

	val uiState by viewModel.uiState.collectAsState()

	HubScreen(
		uiState,
		onGameClick = { id ->
			navController.navigate("${Destination.ScoreCard.route}/$id/-1")
		},
		onLibraryClick = {
			navController.navigate(Destination.Library.route)
		},
		onSettingsClick = {
			navController.navigate(Destination.Login.route)
		},
	)
}
