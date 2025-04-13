package com.waynebloom.scorekeeper.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.feature.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.ui.components.BasicTopBar
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun SettingsScreen(
	uiState: SettingsUiState,
	onAppearanceModeSelect: (AppearanceMode) -> Unit,
	onSendFeedback: () -> Unit,
) {
	var showAppearanceDialog by remember { mutableStateOf(false) }
	var showFeedbackDialog by remember { mutableStateOf(false) }

	if (showAppearanceDialog) {
		AppearanceDialog(
			selectedMode = uiState.appearanceMode,
			onSelectMode = onAppearanceModeSelect,
			onDismiss = { showAppearanceDialog = false }
		)
	}

	if (showFeedbackDialog) {
		FeedbackDialog(
			onSendFeedback = onSendFeedback,
			onDismiss = { showFeedbackDialog = false }
		)
	}

	Scaffold(
		topBar = { BasicTopBar("Settings") },
		contentWindowInsets = WindowInsets(0.dp)
	) { innerPadding ->
		Surface(
			tonalElevation = 2.dp,
			shape = MaterialTheme.shapes.large,
			modifier = Modifier.padding(16.dp).padding(innerPadding),
		) {
			SettingsScreen(
				onAppearanceClick = { showAppearanceDialog = true },
				onFeedbackClick = { showFeedbackDialog = true },
				modifier = Modifier.padding(vertical = 8.dp)
			)
		}
	}
}

@Composable
fun SettingsScreen(
	onAppearanceClick: () -> Unit,
	onFeedbackClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier) {
		SettingsListItem(
			leadingIconPainter = painterResource(R.drawable.ic_image),
			text = stringResource(R.string.text_appearance),
			onClick = onAppearanceClick,
		)

		SettingsListItem(
			leadingIconPainter = painterResource(R.drawable.ic_edit_page),
			text = "Feedback",
			onClick = onFeedbackClick,
		)
	}
}

@Composable
fun SettingsListItem(
	leadingIconPainter: Painter,
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.clickable(onClick = onClick)
			.padding(horizontal = 16.dp, vertical = 12.dp)
			.fillMaxWidth()
	) {
		Icon(
			leadingIconPainter,
			null,
			modifier = Modifier
				.padding(end = 16.dp)
				.size(20.dp)
		)

		Text(
			text = text,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.SemiBold,
		)
	}
}

@PreviewLightDark
@Composable
private fun SettingsScreenPreview() {
	MedianMeepleTheme {
		SettingsScreen(uiState = SettingsSampleData.Default, {}, {})
	}
}
