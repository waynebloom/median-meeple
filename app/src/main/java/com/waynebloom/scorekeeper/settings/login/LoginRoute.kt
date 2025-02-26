package com.waynebloom.scorekeeper.settings.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun LoginRoute(
	navController: NavHostController,
	viewModel: LoginViewModel = hiltViewModel()
) {
	val uiState by viewModel.uiState.collectAsState()

	LoginScreen(
		uiState = uiState,
		onEmailChange = viewModel::onEmailChange,
		onPwChange = viewModel::onPwChange,
		onLoginClick = {
			viewModel.onLoginClick {
				navController.popBackStack()
			}
		}
	)
}