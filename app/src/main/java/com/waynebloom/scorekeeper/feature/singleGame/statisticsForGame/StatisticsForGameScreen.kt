package com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.ExpandCollapseButton
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Size
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.feature.singleGame.SingleGameScreen
import com.waynebloom.scorekeeper.feature.singleGame.StatisticsForGameUiState
import com.waynebloom.scorekeeper.feature.singleGame.matchesForGame.SingleGameTabBar
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model.ScoringPlayerDomainModel
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model.WinningPlayerDomainModel
import com.waynebloom.scorekeeper.util.ext.toStringForDisplay
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import kotlin.collections.first

@Composable
fun StatisticsForGameScreen(
	uiState: StatisticsForGameUiState,
	onEditGameClick: () -> Unit,
	onMatchesTabClick: () -> Unit,
	onBestWinnerButtonClick: () -> Unit,
	onHighScoreButtonClick: () -> Unit,
	onUniqueWinnersButtonClick: () -> Unit,
	onCategoryClick: (Int) -> Unit
) {

	Scaffold(
		topBar = {
			StatisticsForGameTopBar(
				title = uiState.screenTitle,
				selectedTab = SingleGameScreen.StatisticsForGame,
				onEditGameClick = onEditGameClick,
				onTabClick = {
					when (it) {
						SingleGameScreen.StatisticsForGame -> {}
						SingleGameScreen.MatchesForGame -> onMatchesTabClick()
					}
				},
				modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
			)
		},
		contentWindowInsets = WindowInsets(0.dp),
	) { innerPadding ->

		when (uiState) {
			is StatisticsForGameUiState.Loading -> Loading()
			is StatisticsForGameUiState.Empty -> {
				HelperBox(
					message = stringResource(R.string.helper_empty_data),
					type = HelperBoxType.Missing,
					modifier = Modifier
						.padding(innerPadding)
						.padding(horizontal = Spacing.screenEdge)
						.padding(top = Spacing.sectionContent)
				)
			}

			is StatisticsForGameUiState.Content -> {
				StatisticsForGameScreen(
					matchCount = uiState.matchCount.toString(),
					playCount = uiState.playCount.toString(),
					uniquePlayerCount = uiState.uniquePlayerCount.toString(),
					isBestWinnerExpanded = uiState.isBestWinnerExpanded,
					playersWithMostWins = uiState.playersWithMostWins,
					playersWithMostWinsOverflow = uiState.playersWithMostWinsOverflow,
					isHighScoreExpanded = uiState.isHighScoreExpanded,
					playersWithHighScore = uiState.playersWithHighScore,
					playersWithHighScoreOverflow = uiState.playersWithHighScoreOverflow,
					isUniqueWinnersExpanded = uiState.isUniqueWinnersExpanded,
					uniqueWinners = uiState.winners,
					uniqueWinnersOverflow = uiState.winnersOverflow,
					categoryNames = uiState.categoryNames,
					indexOfSelectedCategory = uiState.indexOfSelectedCategory,
					isCategoryDataEmpty = uiState.isCategoryDataEmpty,
					categoryTopScorers = uiState.categoryTopScorers,
					categoryLow = uiState.categoryLow,
					categoryMean = uiState.categoryMean,
					categoryRange = uiState.categoryRange,
					onBestWinnerButtonClick = onBestWinnerButtonClick,
					onHighScoreButtonClick = onHighScoreButtonClick,
					onUniqueWinnerButtonClick = onUniqueWinnersButtonClick,
					onCategoryClick = onCategoryClick,
					modifier = Modifier.padding(innerPadding),
				)
			}
		}
	}
}

@Composable
fun StatisticsForGameScreen(
	matchCount: String,
	playCount: String,
	uniquePlayerCount: String,
	isBestWinnerExpanded: Boolean,
	playersWithMostWins: List<WinningPlayerDomainModel>,
	playersWithMostWinsOverflow: Int,
	isHighScoreExpanded: Boolean,
	playersWithHighScore: List<ScoringPlayerDomainModel>,
	playersWithHighScoreOverflow: Int,
	isUniqueWinnersExpanded: Boolean,
	uniqueWinners: List<WinningPlayerDomainModel>,
	uniqueWinnersOverflow: Int,
	categoryNames: List<String>,
	indexOfSelectedCategory: Int,
	isCategoryDataEmpty: Boolean,
	categoryTopScorers: List<ScoringPlayerDomainModel>,
	categoryLow: String,
	categoryMean: String,
	categoryRange: String,
	onBestWinnerButtonClick: () -> Unit,
	onHighScoreButtonClick: () -> Unit,
	onUniqueWinnerButtonClick: () -> Unit,
	onCategoryClick: (Int) -> Unit,
	modifier: Modifier = Modifier
) {
	LazyColumn(
		contentPadding = PaddingValues(bottom = Spacing.screenEdge),
		verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
		modifier = modifier,
	) {
		item {
			PlaysSection(
				matchCount = matchCount,
				playerCount = uniquePlayerCount,
				modifier = Modifier
					.padding(horizontal = Spacing.screenEdge)
					.padding(top = Spacing.betweenSections)
			)
			HorizontalDivider()
		}

		item {

			WinsSection(
				isBestWinnerExpanded = isBestWinnerExpanded,
				playersWithMostWins = playersWithMostWins,
				playersWithMostWinsOverflow = playersWithMostWinsOverflow,
				isHighScoreExpanded = isHighScoreExpanded,
				playersWithHighScore = playersWithHighScore,
				playersWithHighScoreOverflow = playersWithHighScoreOverflow,
				isUniqueWinnersExpanded = isUniqueWinnersExpanded,
				uniqueWinners = uniqueWinners,
				uniqueWinnersOverflow = uniqueWinnersOverflow,
				onBestWinnerButtonClick = onBestWinnerButtonClick,
				onHighScoreButtonClick = onHighScoreButtonClick,
				onUniqueWinnerButtonClick = onUniqueWinnerButtonClick,
				modifier = Modifier.padding(horizontal = Spacing.screenEdge)
			)

			HorizontalDivider()
		}

		item {

			ScoringSection(
				categories = categoryNames,
				indexOfSelectedCategory = indexOfSelectedCategory,
				isCategoryDataEmpty = isCategoryDataEmpty,
				topScorers = categoryTopScorers,
				low = categoryLow,
				mean = categoryMean,
				range = categoryRange,
				onCategoryClick = onCategoryClick
			)
		}

		item {
			Spacer(
				Modifier
					.windowInsetsBottomHeight(WindowInsets.navigationBars)
					.consumeWindowInsets(WindowInsets.navigationBars)
			)
		}
	}
}

@Composable
fun StatisticsForGameTopBar(
	title: String,
	selectedTab: SingleGameScreen,
	onEditGameClick: () -> Unit,
	onTabClick: (SingleGameScreen) -> Unit,
	modifier: Modifier = Modifier,
) {

	Surface {
		Column(modifier) {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(start = Spacing.screenEdge, end = 4.dp)
					.defaultMinSize(minHeight = Size.topBarHeight)
					.fillMaxWidth()
			) {
				Text(
					text = title,
					style = MaterialTheme.typography.titleLarge,
					overflow = TextOverflow.Ellipsis,
					maxLines = 1
				)
				Icon(
					imageVector = Icons.Rounded.Edit,
					contentDescription = null,
					modifier = Modifier
						.minimumInteractiveComponentSize()
						.clip(CircleShape)
						.clickable(onClick = onEditGameClick)
						.padding(4.dp)
				)
			}

			SingleGameTabBar(
				selectedTab = selectedTab,
				onTabSelected = onTabClick
			)
		}
	}
}

@Composable
private fun PlaysSection(
	matchCount: String,
	playerCount: String,
	modifier: Modifier = Modifier,
) {

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier.padding(bottom = Spacing.sectionContent),
	) {

		Text(
			text = stringResource(id = R.string.header_plays),
			style = MaterialTheme.typography.titleLarge,
		)

		TwoLineListItem(
			startHeadline = stringResource(R.string.headline_matches),
			startSupportingText = stringResource(R.string.description_matches),
			endText = matchCount,
		)

		HorizontalDivider()

		TwoLineListItem(
			startHeadline = stringResource(R.string.headline_players),
			startSupportingText = stringResource(R.string.description_players),
			endText = playerCount,
		)
	}
}

@Composable
private fun WinsSection(
	isBestWinnerExpanded: Boolean,
	playersWithMostWins: List<WinningPlayerDomainModel>,
	playersWithMostWinsOverflow: Int,
	isHighScoreExpanded: Boolean,
	playersWithHighScore: List<ScoringPlayerDomainModel>,
	playersWithHighScoreOverflow: Int,
	isUniqueWinnersExpanded: Boolean,
	uniqueWinners: List<WinningPlayerDomainModel>,
	uniqueWinnersOverflow: Int,
	onBestWinnerButtonClick: () -> Unit,
	onHighScoreButtonClick: () -> Unit,
	onUniqueWinnerButtonClick: () -> Unit,
	modifier: Modifier = Modifier,
) {

	val bestWinnerEndText = if (playersWithMostWins.size < 2) {
		playersWithMostWins.first().name
	} else {
		stringResource(R.string.text_tied)
	}
	val highScoreEndText = if (playersWithHighScore.size < 2) {
		playersWithHighScore.first().score.toStringForDisplay()
	} else {
		stringResource(R.string.text_tied)
	}
	val personIcon: @Composable (() -> Unit) = {
		Icon(
			painter = painterResource(id = R.drawable.ic_person),
			contentDescription = null,
			tint = MaterialTheme.colorScheme.primary,
			modifier = Modifier
				.padding(end = 4.dp)
				.size(16.dp)
		)
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier.padding(bottom = Spacing.sectionContent),
	) {

		Text(
			text = stringResource(id = R.string.header_wins),
			style = MaterialTheme.typography.titleLarge,
		)

		// Best Winner

		TwoLineExpandableListItem(
			startHeadline = stringResource(R.string.headline_best_winner),
			startSupportingText = stringResource(R.string.description_best_winner),
			buttonText = bestWinnerEndText,
			expanded = isBestWinnerExpanded,
			onItemClick = onBestWinnerButtonClick
		) {

			Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

				playersWithMostWins.forEach {
					SingleLineListItem(
						startText = it.name,
						startIcon = personIcon,
						endText = pluralStringResource(
							id = R.plurals.number_with_wins,
							count = it.numberOfWins,
							it.numberOfWins
						),
						showEndBackground = false
					)
				}

				if (playersWithMostWinsOverflow > 0) {
					Text(
						text = stringResource(
							id = R.string.number_list_overflow,
							playersWithMostWinsOverflow
						),
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier.align(Alignment.CenterHorizontally)
					)
				}
			}
		}

		HorizontalDivider()

		// High Score

		TwoLineExpandableListItem(
			startHeadline = stringResource(R.string.headline_high_score),
			startSupportingText = stringResource(R.string.description_high_score),
			buttonText = highScoreEndText,
			onItemClick = onHighScoreButtonClick,
			expanded = isHighScoreExpanded
		) {

			Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

				playersWithHighScore.forEach {
					SingleLineListItem(
						startText = it.name,
						startIcon = personIcon,
						endText = it.score.toStringForDisplay(),
						showEndBackground = false
					)
				}

				if (playersWithHighScoreOverflow > 0) {
					Text(
						text = stringResource(
							id = R.string.number_list_overflow,
							playersWithHighScoreOverflow
						),
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier.align(Alignment.CenterHorizontally)
					)
				}
			}
		}

		HorizontalDivider()

		// Unique Winners

		TwoLineExpandableListItem(
			startHeadline = stringResource(R.string.headline_unique_winners),
			startSupportingText = stringResource(R.string.description_unique_winners),
			buttonText = uniqueWinners.size.toString(),
			onItemClick = onUniqueWinnerButtonClick,
			expanded = isUniqueWinnersExpanded
		) {

			Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

				uniqueWinners.forEach {
					SingleLineListItem(
						startText = it.name,
						startIcon = personIcon,
						endText = pluralStringResource(
							id = R.plurals.number_with_wins,
							count = it.numberOfWins,
							it.numberOfWins
						),
						showEndBackground = false
					)
				}

				if (uniqueWinnersOverflow > 0) {
					Text(
						text = stringResource(
							id = R.string.number_list_overflow,
							uniqueWinnersOverflow
						),
						fontWeight = FontWeight.SemiBold,
						modifier = Modifier.align(Alignment.CenterHorizontally),
					)
				}
			}
		}
	}
}

// region Scoring Section

@Composable
private fun ScoringSection(
	categories: List<String>,
	indexOfSelectedCategory: Int,
	isCategoryDataEmpty: Boolean,
	topScorers: List<ScoringPlayerDomainModel>,
	low: String,
	mean: String,
	range: String,
	onCategoryClick: (Int) -> Unit,
	modifier: Modifier = Modifier,
) {

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier.fillMaxWidth()
	) {

		Text(
			text = stringResource(id = R.string.header_scoring),
			style = MaterialTheme.typography.titleLarge,
			modifier = Modifier.padding(horizontal = Spacing.screenEdge)
		)

		if (categories.isNotEmpty()) {
			LazyRow(
				contentPadding = PaddingValues(horizontal = 16.dp),
				horizontalArrangement = Arrangement.spacedBy(8.dp),
				modifier = modifier
			) {

				itemsIndexed(items = listOf("Total") + categories) { i, category ->
					val isSelected = i == indexOfSelectedCategory

					FilterChip(
						selected = isSelected,
						onClick = { onCategoryClick(i) },
						label = {
							Text(text = category)
						},
						leadingIcon = {
							if (isSelected) {
								Icon(
									painter = painterResource(id = R.drawable.ic_checkmark),
									contentDescription = null,
									modifier = Modifier
										.padding(start = 4.dp)
										.size(18.dp)
								)
							}
						},
					)
				}
			}
		}

		if (isCategoryDataEmpty) {
			HelperBox(
				message = stringResource(R.string.no_data_for_category),
				type = HelperBoxType.Missing,
				modifier = Modifier.padding(horizontal = Spacing.screenEdge)
			)
		} else {
			ScoringStatisticsColumn(
				high = topScorers[0].score.toStringForDisplay(),
				mean = mean,
				low = low,
				range = range,
				topScorers = topScorers,
				modifier = Modifier.padding(horizontal = Spacing.screenEdge)
			)
		}
	}
}

@Composable
private fun ScoringStatisticsColumn(
	high: String,
	low: String,
	mean: String,
	range: String,
	topScorers: List<ScoringPlayerDomainModel>,
	modifier: Modifier = Modifier,
) {
	var highSectionExpanded by rememberSaveable { mutableStateOf(false) }
	val highStartText = if (highSectionExpanded) {
		stringResource(R.string.headline_top_scores)
	} else {
		stringResource(R.string.headline_high)
	}

	Column(
		verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
		modifier = modifier.padding(bottom = Spacing.sectionContent),
	) {

		SingleLineExpandableListItem(
			startText = highStartText,
			buttonText = high,
			onItemClick = { highSectionExpanded = !highSectionExpanded },
			expanded = highSectionExpanded,
		) {

			Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

				topScorers.forEach { scorer ->

					SingleLineListItem(
						startText = scorer.name,
						startIcon = {
							Icon(
								painter = painterResource(id = R.drawable.ic_person),
								contentDescription = null,
								tint = MaterialTheme.colorScheme.primary,
								modifier = Modifier
									.padding(end = 4.dp)
									.size(16.dp)
							)
						},
						endText = scorer.score.toStringForDisplay(),
						showEndBackground = false,
					)
				}
			}
		}

		HorizontalDivider()

		SingleLineListItem(
			startText = stringResource(R.string.headline_low),
			endText = low,
		)

		HorizontalDivider()

		SingleLineListItem(
			startText = stringResource(R.string.headline_mean),
			endText = mean,
		)

		HorizontalDivider()

		SingleLineListItem(
			startText = stringResource(R.string.headline_range),
			endText = range,
		)
	}
}

// endregion

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TwoLineExpandableListItem(
	modifier: Modifier = Modifier,
	startHeadline: String,
	startSupportingText: String? = null,
	buttonText: String? = null,
	onItemClick: () -> Unit,
	expanded: Boolean,
	expandedContent: @Composable () -> Unit,
) {

	Column {

		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = modifier.fillMaxWidth()
		) {

			Column(
				modifier = Modifier
					.weight(1f)
					.padding(end = 16.dp)
			) {

				Text(text = startHeadline)

				if (startSupportingText != null) {
					Text(
						text = startSupportingText,
						style = MaterialTheme.typography.bodyMedium,
					)
				}
			}

			ExpandCollapseButton(
				text = buttonText,
				expanded = expanded,
				onClick = onItemClick
			)
		}

		AnimatedContent(
			targetState = expanded,
			transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
			label = "ExpandableListItemExpand"
		) {
			if (it) {

				Column {
					Spacer(modifier = Modifier.height(Spacing.sectionContent))
					expandedContent()
				}
			}
		}
	}
}

@Composable
fun TwoLineListItem(
	startHeadline: String? = null,
	startSupportingText: String? = null,
	endText: String? = null,
) {
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth()
	) {
		Column(modifier = Modifier.weight(1f)) {
			if (startHeadline != null) {
				Text(text = startHeadline)
			}
			if (startSupportingText != null) {
				Text(
					text = startSupportingText,
					style = MaterialTheme.typography.bodyMedium,
				)
			}
		}

		if (endText != null) {
			Surface(
				color = MaterialTheme.colorScheme.secondaryContainer,
				shape = CircleShape
			) {
				Text(
					text = endText,
					modifier = Modifier
						.padding(horizontal = Spacing.sectionContent, vertical = 4.dp)
				)
			}
		}
	}
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SingleLineExpandableListItem(
	modifier: Modifier = Modifier,
	startText: String,
	buttonText: String? = null,
	expanded: Boolean,
	onItemClick: () -> Unit,
	expandedContent: @Composable () -> Unit,
) {

	Column {

		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = modifier.fillMaxWidth()
		) {

			Text(
				text = startText, modifier = Modifier
					.weight(1f)
					.padding(end = 16.dp)
			)

			ExpandCollapseButton(
				text = buttonText,
				expanded = expanded,
				onClick = onItemClick
			)
		}

		AnimatedContent(
			targetState = expanded,
			transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
			label = "ExpandableListItemExpand"
		) {
			if (it) {

				Column {
					Spacer(modifier = Modifier.height(Spacing.sectionContent))
					expandedContent()
				}
			}
		}
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatisticsForGameScreenPreview() {
	MedianMeepleTheme {
		StatisticsForGameScreen(
			uiState = StatisticsForGameSampleData.DefaultState,
			onEditGameClick = {},
			onMatchesTabClick = {},
			onBestWinnerButtonClick = {},
			onHighScoreButtonClick = {},
			onUniqueWinnersButtonClick = {},
			onCategoryClick = {}
		)
	}
}

@Composable
fun SingleLineListItem(
	startText: String? = null,
	startIcon: @Composable (() -> Unit)? = null,
	endText: String? = null,
	showEndBackground: Boolean = true,
) {

	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth()
	) {

		if (startIcon != null)
			startIcon()

		if (startText != null)
			Text(text = startText, modifier = Modifier.weight(1f))

		if (endText != null) {
			if (showEndBackground) {
				Box(
					contentAlignment = Alignment.Center,
					modifier = Modifier
						.defaultMinSize(
							minWidth = Size.minTappableSize,
							minHeight = Size.minTappableSize
						)
						.clip(MaterialTheme.shapes.medium)
						.background(MaterialTheme.colorScheme.surface)
						.padding(horizontal = Spacing.sectionContent, vertical = 4.dp),
					content = { Text(text = endText) }
				)
			} else {
				Text(text = endText)
			}
		}
	}
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun StatisticsForGameTopBarPreview() {
	MedianMeepleTheme {
		Box(Modifier.background(MaterialTheme.colorScheme.background)) {
			StatisticsForGameTopBar(
				title = "Wingspan",
				selectedTab = SingleGameScreen.StatisticsForGame,
				onEditGameClick = {},
				onTabClick = {},
			)
		}
	}
}

@Suppress("MagicNumber")
@Preview(
	name = "SingleLineExpandableListItem, interactive",
	uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun SingleLineExpandableListItemPreview() {
	MedianMeepleTheme {
		Surface(color = MaterialTheme.colorScheme.background) {
			var expanded by remember { mutableStateOf(false) }

			SingleLineExpandableListItem(
				startText = "Item title",
				buttonText = "Value",
				expanded = expanded,
				onItemClick = { expanded = !expanded },
				expandedContent = {
					mapOf(
						Pair("Wayne", 6),
						Pair("Alyssa", 6)
					).forEach {
						SingleLineListItem(
							startText = it.key,
							endText = pluralStringResource(
								id = R.plurals.number_with_wins,
								it.value
							),
						)
					}
				},
				modifier = Modifier.padding(16.dp)
			)
		}
	}
}

@Suppress("MagicNumber")
@Preview(
	name = "TwoLineExpandableListItem, interactive",
	uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun TwoLineExpandableListItemPreview() {
	MedianMeepleTheme {
		Surface(color = MaterialTheme.colorScheme.background) {
			var expanded by remember { mutableStateOf(false) }

			TwoLineExpandableListItem(
				startHeadline = "Item title",
				startSupportingText = "Supporting text",
				buttonText = "Value",
				expanded = expanded,
				onItemClick = { expanded = !expanded },
				expandedContent = {
					mapOf(
						Pair("Wayne", 6),
						Pair("Alyssa", 6)
					).forEach {
						SingleLineListItem(
							startText = it.key,
							endText = pluralStringResource(
								id = R.plurals.number_with_wins,
								it.value
							),
						)
					}
				},
				modifier = Modifier.padding(16.dp)
			)
		}
	}
}
