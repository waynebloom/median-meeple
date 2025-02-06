package com.waynebloom.scorekeeper.meepleBase

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun MeepleBaseRoute(
    navController: NavHostController,
    viewModel: MeepleBaseViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    MeepleBaseScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPwChange = viewModel::onPwChange,
        onLoginClick = viewModel::onLoginClick,
        onRequestGames = viewModel::onRequestGames,
    )
}
