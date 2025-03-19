package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.waynebloom.scorekeeper.settings.login.LoginScreen
import com.waynebloom.scorekeeper.settings.login.LoginViewModel
import kotlinx.serialization.Serializable

@Serializable
internal object Login

fun NavGraphBuilder.loginDestination(
	onPopBackStack: () -> Unit,
) {
	composable<Login> {
		val viewModel: LoginViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsState()

		LoginScreen(
			uiState = uiState,
			onEmailChange = viewModel::onEmailChange,
			onPwChange = viewModel::onPwChange,
			onLoginClick = {
				viewModel.onLoginClick {
					onPopBackStack()
				}
			}
		)
	}
}

fun NavController.navigateToLogin() {
	navigate(route = Login)
}
