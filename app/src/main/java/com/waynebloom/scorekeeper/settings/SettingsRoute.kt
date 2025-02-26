package com.waynebloom.scorekeeper.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun SettingsRoute(
	navController: NavHostController,
	viewModel: SettingsViewModel = hiltViewModel()
) {

	val uiState by viewModel.uiState.collectAsStateWithLifecycle()

	SettingsScreen(
		uiState = uiState,
		onSignInClick = { navController.navigate(Destination.Login.route) },
		onSignOutClick = viewModel::logout,
	)
}