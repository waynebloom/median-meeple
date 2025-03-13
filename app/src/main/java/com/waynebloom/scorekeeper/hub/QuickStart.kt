package com.waynebloom.scorekeeper.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.util.PreviewContainer

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun QuickStart(
	quickGames: List<GameDomainModel>,
	allGames: List<GameDomainModel>,
	isGamePickerLoading: Boolean,
	onGameClick: (Long) -> Unit,
	onAddQuickGameClick: () -> Unit,
	onGameSelect: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {

		QuickStartHeader(Modifier.padding(bottom = 8.dp))

		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.subSectionContent),
			verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.subSectionContent),
			modifier = Modifier.fillMaxWidth()
		) {

			if (quickGames.isEmpty()) {
				ElevatedAssistChip(
					onClick = {},
					enabled = false,
					label = { Text("Example") },
					leadingIcon = {
						Icon(
							painter = painterResource(R.drawable.ic_zap),
							contentDescription = null,
							modifier = Modifier.size(16.dp)
						)
					},
					elevation = AssistChipDefaults.elevatedAssistChipElevation(elevation = 2.dp),
					modifier = Modifier.height(32.dp)
				)
			}

			quickGames.forEach {
				val color = GameDomainModel.DisplayColors[it.displayColorIndex]
				val iconColor = color
					.copy(alpha = Alpha.HIGH_ALPHA)
					.compositeOver(MaterialTheme.colorScheme.onSurface)
				val containerColor = color
					.copy(alpha = Alpha.LOW_ALPHA)
					.compositeOver(MaterialTheme.colorScheme.surface)

				FilledTonalButton (
					onClick = { onGameClick(it.id) },
					colors = ButtonDefaults.filledTonalButtonColors(
						containerColor = containerColor
					),
					elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
					contentPadding = PaddingValues(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
				) {

					Row(verticalAlignment = Alignment.CenterVertically) {

						Icon(
							painter = painterResource(R.drawable.ic_zap),
							contentDescription = null,
							tint = iconColor,
							modifier = Modifier.size(24.dp).padding(end = 8.dp)
						)

						Text(text = it.name.text, style = MaterialTheme.typography.labelLarge)
					}
				}
			}

			Box(contentAlignment = Alignment.TopEnd) {
				var isExpanded by remember { mutableStateOf(false) }

				FilledTonalIconButton(
					onClick = {
						onAddQuickGameClick()
						isExpanded = true
					},
				) {
					Icon(
						painter = painterResource(R.drawable.ic_plus),
						contentDescription = null,
					)
				}

				QuickStartPicker(
					modifier = Modifier.width(196.dp),
					expanded = isExpanded,
					loading = isGamePickerLoading,
					games = allGames.minus(quickGames.toSet()),
					dismissMenu = { isExpanded = false },
					onGameSelect = onGameSelect
				)
			}
		}
	}
}

@Composable
private fun QuickStartHeader(modifier: Modifier = Modifier) {

	Column(modifier) {
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier.padding(bottom = 8.dp)
		) {
			Box(
				modifier = Modifier
					.padding(end = 8.dp)
					.background(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape,
					)
			) {

				Icon(
					painter = painterResource(R.drawable.ic_zap),
					contentDescription = null,
					modifier = Modifier
						.padding(4.dp)
						.size(16.dp)
				)
			}

			Text(text = "Quick Start")
		}

		HorizontalDivider()
	}
}

@Composable
fun QuickStartPicker(
	modifier: Modifier = Modifier,
	expanded: Boolean,
	loading: Boolean,
	games: List<GameDomainModel>,
	dismissMenu: () -> Unit,
	onGameSelect: (Long) -> Unit,
) {

	DropdownMenu(
		expanded = expanded,
		onDismissRequest = dismissMenu,
		modifier = modifier
	) {

		when {
			loading -> CircularProgressIndicator()
			games.isEmpty() -> {
				DropdownMenuItem(
					onClick = {},
					text = { Text(text = "No games found.") },
					leadingIcon = {
						Icon(
							painter = painterResource(R.drawable.ic_help_circle),
							contentDescription = null,
						)
					},
					enabled = false
				)
			}

			else -> games.forEach {
				DropdownMenuItem(
					text = { Text(it.name.text) },
					onClick = {
						onGameSelect(it.id)
						dismissMenu()
					}
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun QuickStartPreview() {
	MedianMeepleTheme {
		PreviewContainer {
			QuickStart(
				quickGames = HubSampleData.Default.quickGames,
				allGames = HubSampleData.Default.allGames ?: listOf(),
				isGamePickerLoading = false,
				{}, {}, {}
			)
		}
	}
}
