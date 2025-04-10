package com.waynebloom.scorekeeper.navigation.graph

import android.R.attr.scheme
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable
import androidx.core.net.toUri
import com.waynebloom.scorekeeper.feature.settings.SettingsScreen
import com.waynebloom.scorekeeper.feature.settings.SettingsViewModel

@Serializable object Settings

@Serializable object SettingsSection

fun NavGraphBuilder.settingsDestination(
	onSendFeedback: () -> Unit,
) {
	composable<Settings> {
		val viewModel: SettingsViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()

		SettingsScreen(
			uiState = uiState,
			onAppearanceModeSelect = viewModel::onAppearanceModeSelect,
			onSendFeedback = onSendFeedback,
		)
	}
}

fun NavGraphBuilder.settingsSection(builder: NavGraphBuilder.() -> Unit) {
	navigation<SettingsSection>(startDestination = Settings, builder = builder)
}

fun NavController.navigateToSettings(navOptions: NavOptions) {
	navigate(route = Settings, navOptions = navOptions)
}

fun Activity.sendFeedbackEmail() {
	val mailUri = "mailto:wayne.bloom224@gmail.com?subject=" + Uri.encode("Median Meeple Feedback")
	Intent(Intent.ACTION_SENDTO, mailUri.toUri()).apply {
		addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
	}.also {
		startActivity(Intent.createChooser(it, "Send Email"))
	}
}
