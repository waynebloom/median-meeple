package com.waynebloom.scorekeeper.settings

import android.R.attr.end
import android.R.attr.top
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.RadioButtonOption
import com.waynebloom.scorekeeper.settings.model.AppearanceMode
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
internal fun AppearanceDialog(
	modifier: Modifier = Modifier,
	selectedMode: AppearanceMode,
	onSelectMode: (AppearanceMode) -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismiss) {
		Card(modifier) {
			Column(Modifier.padding(vertical = 16.dp)) {
				Text(
					text = "Appearance",
					style = MaterialTheme.typography.headlineSmall,
					modifier = Modifier.padding(horizontal = 24.dp),
				)
				HorizontalDivider(Modifier.padding(vertical = 12.dp))
				AppearanceMode.entries.forEach {
					RadioButtonOption(
						modifier = Modifier.padding(horizontal = 8.dp),
						menuOption = it,
						isSelected = selectedMode == it,
						onSelected = onSelectMode,
					)
				}
				Box(
					contentAlignment = Alignment.BottomEnd,
					modifier = Modifier
						.fillMaxWidth()
						.padding(end = 24.dp)
				) {
					TextButton(onDismiss) {
						Text(text = "Confirm")
					}
				}
			}
		}
	}
}

@Composable
internal fun FeedbackDialog(
	modifier: Modifier = Modifier,
	onSendFeedback: () -> Unit,
	onDismiss: () -> Unit,
) {
	Dialog(onDismiss) {
		Card(modifier) {
			Column(Modifier.padding(top = 24.dp, bottom = 16.dp)) {
				Row(
					modifier = Modifier
						.padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
						.height(IntrinsicSize.Min),
				) {
					VerticalDivider(
						thickness = 3.dp,
						modifier = Modifier.clip(CircleShape)
					)

					Text(
						text = stringResource(R.string.text_feedback_request),
						fontStyle = FontStyle.Italic,
						modifier = Modifier.padding(start = 8.dp)
					)
				}

				Row(
					horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
					modifier = Modifier
						.fillMaxWidth()
						.padding(end = 24.dp)
				) {
					TextButton(onDismiss) {
						Text(text = stringResource(R.string.text_cancel))
					}
					Button(onSendFeedback) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Text(text = stringResource(R.string.text_open))
							Icon(
								painter = painterResource(R.drawable.ic_external_link),
								contentDescription = null,
								modifier = Modifier.padding(start = 8.dp).size(16.dp)
							)
						}
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun AppearanceDialogPreview() {
	MedianMeepleTheme {
		AppearanceDialog(
			modifier = Modifier,
			selectedMode = AppearanceMode.DARK,
			{}, {}
		)
	}
}

@Preview
@Composable
private fun FeedbackDialogPreview() {
	MedianMeepleTheme {
		FeedbackDialog(
			modifier = Modifier,
			{}, {}
		)
	}
}
