package com.waynebloom.scorekeeper.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun LibraryRoute(
    navController: NavHostController,
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LibraryScreen(
        uiState = uiState,
        onSearchInputChanged = { viewModel.onSearchInputChanged(it) },
        onAddGameClick = {
            val route = "${Destination.EditGame.route}/-1"
            navController.navigate(route)
        },
        onGameClick = { id ->
            navController.navigate("${Destination.MatchesForGame.route}/$id")
        },
        onLoginClick = {
            navController.navigate(Destination.Login.route)
        },
    )
}
