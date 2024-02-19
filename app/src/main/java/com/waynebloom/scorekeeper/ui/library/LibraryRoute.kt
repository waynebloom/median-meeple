package com.waynebloom.scorekeeper.ui.library

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.waynebloom.scorekeeper.ui.navigation.Destinations

@Composable
fun LibraryRoute(
    navController: NavController,
    viewModel: LibraryViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LibraryScreen(
        uiState = uiState,
        onSearchInputChanged = { viewModel.onSearchInputChanged(it) },
        onAddGameClick = { viewModel.addEmptyGame() },
        onGameClick = { id -> navController.navigate("${Destinations.SingleGame}/$id") },
    )
}