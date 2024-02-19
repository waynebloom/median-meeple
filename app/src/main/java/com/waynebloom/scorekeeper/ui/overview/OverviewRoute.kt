package com.waynebloom.scorekeeper.ui.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.ui.navigation.Destination
import com.waynebloom.scorekeeper.ui.navigation.Destinations
import com.waynebloom.scorekeeper.viewmodel.OverviewViewModel

@Composable
fun OverviewRoute(
    navController: NavHostController,
    viewModel: OverviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    OverviewScreen(
        uiState = uiState,
        onAddGameClick = { viewModel.addEmptyGame(navController) },
        onGoToLibraryClick = { navController.navigate(Destinations.Library) },
        onGameClick = { id ->
            navController.navigate("${Destination.MatchesForGame.route}/$id")
        },
        onMatchClick = { id ->
            navController.navigate("${Destinations.SingleMatch}/$id")
        }
    )
}