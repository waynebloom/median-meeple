package com.waynebloom.scorekeeper.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun SettingsScreen(
	uiState: SettingsUiState,
	onSignInClick: () -> Unit,
	onSignOutClick: () -> Unit,
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

	Scaffold() { innerPadding ->
		when (uiState) {
			is SettingsUiState.SignedOut -> {
				SettingsScreenSignedOut(
					onLoginClick = onSignInClick,
					onAppearanceClick = { showAppearanceDialog = true },
					onFeedbackClick = { showFeedbackDialog = true },
					modifier = Modifier.padding(innerPadding)
				)
			}

			is SettingsUiState.SignedIn -> {
				SettingsScreenSignedIn(
					uiState.name,
					uiState.email,
					uiState.subDays,
					onSignOutClick,
					Modifier.padding(innerPadding),
				)
			}
		}
	}
}

@Composable
fun SettingsScreenSignedOut(
	onLoginClick: () -> Unit,
	onAppearanceClick: () -> Unit,
	onFeedbackClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier) {

		NoAccountConnected(onLoginClick)

		HorizontalDivider(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp))

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
private fun SettingsScreenSignedIn(
	name: String,
	email: String,
	subDays: Int,
	onSignOutClick: () -> Unit,
	modifier: Modifier = Modifier,
) {

	Column(modifier) {

		AccountInformation(
			name,
			email,
			subDays,
			Modifier.padding(16.dp)
		)

		HorizontalDivider(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp))

		SettingsListItem(
			leadingIconPainter = painterResource(R.drawable.ic_log_out),
			text = "Sign Out",
			onClick = onSignOutClick,
		)

		// Delete account (required by Apple store I believe)
		// Patreon
		// Appearance (light/dark mode)
	}
}

@Composable
fun NoAccountConnected(
	onLoginClick: () -> Unit,
	modifier: Modifier = Modifier,
) {

	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.padding(16.dp)
			.fillMaxWidth()
	) {

		Row(verticalAlignment = Alignment.CenterVertically) {

			Icon(
				painter = painterResource(R.drawable.ic_info_circle),
				contentDescription = null,
				modifier = Modifier.padding(end = 8.dp)
			)

			Text(text = "No account connected.", style = MaterialTheme.typography.bodyLarge)
		}

		Button(onLoginClick) {
			Text(text = "Sign In", style = MaterialTheme.typography.bodyLarge)
		}
	}
}

@Composable
fun AccountInformation(
	name: String,
	email: String,
	subDays: Int,
	modifier: Modifier = Modifier,
) {
	Column(modifier) {

		Row(verticalAlignment = Alignment.CenterVertically) {
			Icon(
				painter = painterResource(R.drawable.ic_person),
				contentDescription = null,
				modifier = Modifier
					.padding(end = 8.dp)
					.size(32.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape,
					)
					.padding(4.dp)
			)

			Column {
				Text(text = name, style = MaterialTheme.typography.bodyMedium)

				Text(text = email, style = MaterialTheme.typography.bodySmall)
			}
		}

		if (subDays > 0) {
			AccountDataLine(
				iconPainter = painterResource(R.drawable.ic_clock),
				text = "Renewing in $subDays " + if (subDays == 1) "day." else "days."
			)
		}

		AccountDataLine(
			iconPainter = painterResource(R.drawable.ic_refresh),
			text = "Up to date."
		)
	}
}

@Composable
fun AccountDataLine(
	iconPainter: Painter,
	text: String,
	modifier: Modifier = Modifier,
) {

	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.padding(top = 12.dp)
	) {

		Icon(
			painter = iconPainter,
			contentDescription = null,
			modifier = Modifier
				.padding(start = 4.dp, end = 12.dp)
				.background(
					color = MaterialTheme.colorScheme.secondaryContainer,
					shape = CircleShape,
				)
				.padding(4.dp)
				.size(16.dp),
		)

		Text(text = text, style = MaterialTheme.typography.bodyMedium)
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
			.padding(horizontal = 16.dp)
			.clip(MaterialTheme.shapes.small)
			.clickable(onClick = onClick)
			.padding(start = 8.dp, top = 12.dp, bottom = 12.dp)
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
private fun SettingsSignedInPreview() {
	MedianMeepleTheme {
		SettingsScreen(uiState = SettingsSampleData.SignedIn, {}, {}, {}, {})
	}
}

@PreviewLightDark
@Composable
private fun SettingsSignedOutPreview() {
	MedianMeepleTheme {
		SettingsScreen(uiState = SettingsSampleData.SignedOut, {}, {}, {}, {})
	}
}
