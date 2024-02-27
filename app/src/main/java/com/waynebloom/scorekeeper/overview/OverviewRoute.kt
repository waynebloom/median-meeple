package com.waynebloom.scorekeeper.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun OverviewRoute(
        navController: NavHostController,
        viewModel: OverviewViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    OverviewScreen(
        uiState = uiState,
        onAddGameClick = { viewModel.addEmptyGame(navController) },
        onGoToLibraryClick = { navController.navigate(Destination.Library.route) },
        onGameClick = { id ->
            navController.navigate("${Destination.MatchesForGame.route}/$id")
        },
        onMatchClick = { id ->
            navController.navigate("${Destination.SingleMatch.route}/$id")
        }
    )
}
