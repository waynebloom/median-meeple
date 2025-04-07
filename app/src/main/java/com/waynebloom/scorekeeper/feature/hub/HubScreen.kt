@file:OptIn(ExperimentalLayoutApi::class)

package com.waynebloom.scorekeeper.feature.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.constants.Alpha
import com.waynebloom.scorekeeper.ui.constants.Dimensions
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameWithMatchCount
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun HubScreen(
	uiState: HubUiState,
	onQuickGameClick: (Long) -> Unit,
	onRemoveQuickGame: (GameDomainModel) -> Unit,
	onAddQuickGameClick: () -> Unit,
	onPickNewQuickGame: (Long) -> Unit,
) {

	when (uiState) {
		is HubUiState.Loading -> Loading()
		is HubUiState.Content -> {

			Scaffold() { innerPadding ->
				HubScreen(
					quickStartGames = uiState.favoriteGames,
					pickerOptions = uiState.nonFavoritesWithMatchCount ?: listOf(),
					isPickerLoading = uiState.nonFavoritesWithMatchCount == null,
					weekPlays = uiState.weekPlays,
					chartKey = uiState.chartKey,
					modifier = Modifier.padding(innerPadding),
					onQuickGameClick = onQuickGameClick,
					onRemoveQuickGame = onRemoveQuickGame,
					onAddQuickGameClick = onAddQuickGameClick,
					onPickNewQuickGame = onPickNewQuickGame,
				)
			}
		}
	}
}

@Composable
private fun HubScreen(
	quickStartGames: List<GameDomainModel>,
	pickerOptions: List<GameWithMatchCount>,
	isPickerLoading: Boolean,
	weekPlays: Map<String, List<String>>,
	chartKey: Map<String, Pair<Color, Shape>>,
	modifier: Modifier = Modifier,
	onQuickGameClick: (Long) -> Unit,
	onRemoveQuickGame: (GameDomainModel) -> Unit,
	onAddQuickGameClick: () -> Unit,
	onPickNewQuickGame: (Long) -> Unit,
) {

	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = Dimensions.Spacing.screenEdge),
	) {

		TopBar(Modifier.fillMaxWidth().padding(bottom = 12.dp))
		QuickStart(
			quickStartGames = quickStartGames,
			pickerOptions = pickerOptions,
			isPickerLoading = isPickerLoading,
			onQuickGameClick = onQuickGameClick,
			onRemoveClick = onRemoveQuickGame,
			onAddClick = onAddQuickGameClick,
			onPickNewQuickGame = onPickNewQuickGame,
			modifier = Modifier.fillMaxWidth().padding(bottom = 36.dp)
		)
		RecentActivityCard(weekPlays, chartKey)
	}
}

@Composable
private fun TopBar(modifier: Modifier = Modifier) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.heightIn(min = Dimensions.Size.topBarHeight)
	) {
		Text(
			text = "Hub",
			style = MaterialTheme.typography.titleMedium,
		)
	}
}

@Composable
fun DayActivity(
	modifier: Modifier = Modifier,
	dayPlays: List<String>,
	chartKey: Map<String, Pair<Color, Shape>>,
) {

	val totalPlays = dayPlays.size
	val cols = 2
	val rows = if (totalPlays % cols == 0) {
		totalPlays / cols
	} else {
		(totalPlays / cols) + 1
	}
	var i = 0

	val pipSize = 16.dp
	val gap = 4.dp

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(gap),
		modifier = modifier
			.width(48.dp)
			.graphicsLayer {
				rotationZ += 180
			}
	) {

		repeat(rows) {
			Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
				repeat(cols) {

					if (i == dayPlays.size) {
						return@repeat
					}

					val baseColor = chartKey[dayPlays[i]]?.first ?: Color.Black
					val composited = baseColor
						.copy(alpha = Alpha.HIGH_ALPHA)
						.compositeOver(MaterialTheme.colorScheme.onSurface)
					val shape = chartKey[dayPlays[i]]?.second ?: CircleShape
					i++

					Box(
						modifier = Modifier
							.background(composited, shape)
							.size(pipSize)
					) {}
				}
			}
		}
	}
}

@Composable
private fun RecentActivityCard(
	weekPlays: Map<String, List<String>>,
	chartKey: Map<String, Pair<Color, Shape>>,
	modifier: Modifier = Modifier,
) {
	Surface(
		shape = MaterialTheme.shapes.large,
		tonalElevation = 2.dp,
		modifier = modifier
	) {

		Column(modifier = Modifier.padding(Dimensions.Spacing.sectionContent)) {

			Row(
				verticalAlignment = Alignment.CenterVertically
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
						painter = painterResource(R.drawable.ic_activity),
						contentDescription = null,
						modifier = Modifier
							.padding(4.dp)
							.size(16.dp)
					)
				}

				Text(text = "Activity", style = MaterialTheme.typography.titleMedium)
			}

			if (weekPlays.isEmpty()) {
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier.fillMaxWidth().padding(16.dp),
				) {
					Text(
						text = "No activity recorded in the past week.",
						style = MaterialTheme.typography.bodyLarge,
					)
				}
				return@Column
			}

			Column(horizontalAlignment = Alignment.CenterHorizontally) {

				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.Bottom,
					modifier = Modifier
						.fillMaxWidth()
						.padding(top = 24.dp, bottom = 4.dp)
				) {

					// 1 each per day of week
					weekPlays.forEach { (_, dayPlays) ->
						DayActivity(
							dayPlays = dayPlays,
							chartKey = chartKey,
						)
					}
				}

				Row(
					horizontalArrangement = Arrangement.SpaceBetween,
					modifier = Modifier.fillMaxWidth()
				) {

					weekPlays.keys.forEach {
						Box(
							contentAlignment = Alignment.Center,
							modifier = Modifier.width(48.dp),
						) {

							Text(text = it, style = MaterialTheme.typography.labelLarge)
						}
					}
				}

				HorizontalDivider(
					modifier = Modifier.padding(vertical = 8.dp),
					color = MaterialTheme.colorScheme.onSurface,
				)

				FlowRow(
					horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
					verticalArrangement = Arrangement.spacedBy(8.dp),
				) {
					chartKey.forEach { (game, display) ->
						Row(
							horizontalArrangement = Arrangement.spacedBy(8.dp),
							verticalAlignment = Alignment.CenterVertically,
						) {

							val pipColor = display.first
								.copy(alpha = Alpha.HIGH_ALPHA)
								.compositeOver(MaterialTheme.colorScheme.onSurface)
							val textColor = display.first
								.copy(alpha = Alpha.MEDIUM_ALPHA)
								.compositeOver(MaterialTheme.colorScheme.onSurface)

							Box(
								modifier = Modifier
									.size(16.dp)
									.background(pipColor, display.second)
							) {}

							Text(
								text = game,
								style = MaterialTheme.typography.labelLarge,
								color = textColor,
								maxLines = 1,
								overflow = TextOverflow.Ellipsis,
							)
						}
					}
				}
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun HubDefaultPreview() {
	MedianMeepleTheme {
		HubScreen(uiState = HubSampleData.Default, {}, {}, {}, {})
	}
}

@PreviewLightDark
@Composable
private fun HubNoActivityPreview() {
	MedianMeepleTheme {
		HubScreen(uiState = HubSampleData.NoActivity, {}, {}, {}, {})
	}
}
