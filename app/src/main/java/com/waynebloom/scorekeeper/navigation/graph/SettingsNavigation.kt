package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.waynebloom.scorekeeper.settings.SettingsScreen
import com.waynebloom.scorekeeper.settings.SettingsViewModel
import kotlinx.serialization.Serializable

@Serializable object Settings

@Serializable object SettingsSection

fun NavGraphBuilder.settingsDestination(
	onNavigateToLogin: () -> Unit,
) {
	composable<Settings> {
		val viewModel: SettingsViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()

		SettingsScreen(
			uiState = uiState,
			onSignInClick = onNavigateToLogin,
			onSignOutClick = viewModel::logout,
		)
	}
}

fun NavGraphBuilder.settingsSection(builder: NavGraphBuilder.() -> Unit) {
	navigation<SettingsSection>(startDestination = Settings, builder = builder)
}

fun NavController.navigateToSettings(navOptions: NavOptions) {
	navigate(route = Settings, navOptions = navOptions)
}
