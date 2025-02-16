@file:OptIn(ExperimentalLayoutApi::class)

package com.waynebloom.scorekeeper.hub

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun HubScreen(
	uiState: HubUiState,
	onGameClick: (Long) -> Unit,
	onLibraryClick: () -> Unit,
	onSettingsClick: () -> Unit,
) {

	when (uiState) {
		is HubUiState.Loading -> Loading()
		is HubUiState.Content -> {

			Scaffold() { innerPadding ->
				HubScreen(
					quickGames = uiState.quickGames,
					dateRange = uiState.dateRange,
					weekActivity = uiState.weekActivity,
					chartKey = uiState.chartKey,
					modifier = Modifier.padding(innerPadding),
					onGameClick,
					onLibraryClick,
					onSettingsClick,
				)
			}
		}
	}
}

@Composable
private fun HubScreen(
	quickGames: List<GameDomainModel>,
	dateRange: String,
	weekActivity: Map<String, Map<String, Int>>,
	chartKey: Map<String, Pair<Color, Shape>>,
	modifier: Modifier = Modifier,
	onGameClick: (Long) -> Unit,
	onLibraryClick: () -> Unit,
	onSettingsClick: () -> Unit,
) {

	Column(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = Dimensions.Spacing.screenEdge),
	) {

		TopBar(
			// TODO: this needs to be the actual user's username OR a default state if not logged in.
			"Welcome, Gigabyted.",
			onSettingsClick,
			Modifier.fillMaxWidth().padding(bottom = 12.dp),
		)
		QuickGames(
			quickGames,
			onGameClick,
			onLibraryClick,
			Modifier.fillMaxWidth().padding(bottom = 12.dp)
		)
		ActivityChart(dateRange, weekActivity, chartKey)
	}
}

@Composable
private fun TopBar(
	title: String,
	onSettingsClick: () -> Unit,
	modifier: Modifier = Modifier,
) {

	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.heightIn(min = Dimensions.Size.topBarHeight),
	) {
		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
		)

		IconButton(
			onClick = onSettingsClick,
			modifier = Modifier.background(
				color = MaterialTheme.colorScheme.primaryContainer,
				shape = CircleShape,
			)
		) {
			Icon(
				painter = painterResource(R.drawable.settings),
				contentDescription = "Settings",
				modifier = Modifier.size(24.dp)
			)
		}
	}
}

@Composable
private fun QuickGames(
	games: List<GameDomainModel>,
	onGameClick: (Long) -> Unit,
	onLibraryClick: () -> Unit,
	modifier: Modifier = Modifier,
) {
	Column(modifier = modifier) {

		Text(
			text = "Quick Start",
			style = MaterialTheme.typography.titleMedium,
			modifier = Modifier.padding(bottom = Dimensions.Spacing.sectionContent)
		)

		FlowRow(
			horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)
		) {

			games.forEach {
				val color = GameDomainModel.DisplayColors[it.displayColorIndex]
				val buttonColor = color
					.copy(alpha = 0.2f)
					.compositeOver(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))

				Button(
					onClick = { onGameClick(it.id) },
					colors = ButtonDefaults.buttonColors(
						containerColor = buttonColor,
						contentColor = MaterialTheme.colorScheme.onSurface,
					)
				) {
					Text(text = it.name.text)
				}
			}

			Button(
				onClick = onLibraryClick,
			) {

				Row {

					Icon(
						imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
						contentDescription = null,
						modifier = Modifier.size(16.dp)
					)

					Icon(
						painter = painterResource(R.drawable.ic_grid),
						contentDescription = null,
						modifier = Modifier.size(16.dp)
					)
				}
			}
		}
	}
}

@Composable
fun DayActivity(
	modifier: Modifier = Modifier,
	dayActivity: Map<String, Int>,
	chartKey: Map<String, Pair<Color, Shape>>,
) {

	val totalPlays = dayActivity.values.sum()
	val cols = 2
	val rows = if (totalPlays % cols == 0) {
		totalPlays / cols
	} else {
		(totalPlays / cols) + 1
	}
	val pips = dayActivity.flatMap { (game, amount) ->
		buildList {
			for(i in 0..<amount) {
				add(game)
			}
		}
	}
	var i = 0

	val pipSize = 12.dp
	val gap = 2.dp

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.spacedBy(gap),
		modifier = modifier.width(48.dp).graphicsLayer {
			rotationZ += 180
		}
	) {

		repeat(rows) {
			Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
				repeat (cols) {

					if (i == pips.size) {
						return@repeat
					}

					val color = chartKey[pips[i]]?.first ?: Color.Black
					val shape = chartKey[pips[i]]?.second ?: CircleShape
					i++

					Box(
						modifier = Modifier.background(color, shape).size(pipSize)
					) {}
				}
			}
		}
	}
}

@Composable
private fun ActivityChart(
	dateRange: String,

	// Mapping day of week TO a mapping of name of game TO number of matches
	weekActivity: Map<String, Map<String, Int>>,

	// Mapping name of game TO the color and shape representing it
	chartKey: Map<String, Pair<Color, Shape>>,
	modifier: Modifier = Modifier,
) {

	Column(modifier) {

		Text(text = "Activity")

		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
		) {

			Text(text = dateRange, style = MaterialTheme.typography.titleSmall)

			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Bottom,
				modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
			) {

				// 1 each per day of week
				weekActivity.forEach { (_, dayActivity) ->
					DayActivity(
						dayActivity = dayActivity,
						chartKey = chartKey,
					)
				}
			}

			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {

				weekActivity.keys.forEach {
					Box(
						contentAlignment = Alignment.Center,
						modifier = Modifier.width(48.dp),
					) {

						Text(text = it, style = MaterialTheme.typography.labelMedium)
					}
				}
			}

			HorizontalDivider(Modifier.padding(vertical = 8.dp))

			FlowRow(
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
			) {
				chartKey.forEach { (game, display) ->
					Row(
						horizontalArrangement = Arrangement.spacedBy(4.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {

						Box(
							modifier = Modifier.size(12.dp).background(display.first, display.second)
						) {}
						
						Text(text = game, style = MaterialTheme.typography.labelSmall)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun HubPreview() {
	MedianMeepleTheme {
		HubScreen(uiState = HubSampleData.Default, {}, {}, {})
	}
}
