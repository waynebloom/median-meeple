package com.waynebloom.scorekeeper.feature.singleGame.matchesForGame

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.LargeImageAdCard
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.components.MatchCard
import com.waynebloom.scorekeeper.ui.components.RadioButtonOption
import com.waynebloom.scorekeeper.ui.components.TopBarWithSearch
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Size
import com.waynebloom.scorekeeper.ui.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.ui.constants.DurationMs
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.feature.singleGame.SingleGameScreen
import com.waynebloom.scorekeeper.util.ext.toShortFormatString
import com.waynebloom.scorekeeper.singleGame.MatchesForGameUiState
import com.waynebloom.scorekeeper.singleGame.SingleGameSampleData
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun MatchesForGameScreen(
	uiState: MatchesForGameUiState,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	onSortModeChanged: (MatchSortMode) -> Unit,
	onSortDirectionChanged: (SortDirection) -> Unit,
	onEditGameClick: () -> Unit,
	onStatisticsTabClick: () -> Unit,
	onSortButtonClick: () -> Unit,
	onMatchClick: (Long) -> Unit,
	onAddMatchClick: () -> Unit,
	onSortDialogDismiss: () -> Unit,
) {

	when (uiState) {

		is MatchesForGameUiState.Content -> {
			MatchesForGameScreen(
				screenTitle = uiState.screenTitle,
				searchInput = uiState.searchInput,
				isSortDialogShowing = uiState.isSortDialogShowing,
				sortDirection = uiState.sortDirection,
				sortMode = uiState.sortMode,
				matches = uiState.matches,
				filteredIndices = uiState.filteredIndices,
				listState = rememberLazyListState(),
				ads = uiState.ads,
				onEditGameClick = onEditGameClick,
				onSortButtonClick = onSortButtonClick,
				onStatisticsTabClick = onStatisticsTabClick,
				onMatchClick = onMatchClick,
				onAddMatchClick = onAddMatchClick,
				onSearchInputChanged = onSearchInputChanged,
				onSortModeChanged = onSortModeChanged,
				onSortDirectionChanged = onSortDirectionChanged,
				onSortDialogDismiss = onSortDialogDismiss,
			)
		}

		is MatchesForGameUiState.Loading -> Loading()
	}
}

// region Top Bar

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun MatchesForSingleGameTopBar(
	searchInput: TextFieldValue,
	selectedTab: SingleGameScreen,
	title: String,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	onSortClick: () -> Unit,
	onEditGameClick: () -> Unit,
	onTabClick: (SingleGameScreen) -> Unit,
	modifier: Modifier = Modifier,
) {
	var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }

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

				AnimatedContent(
					targetState = isSearchBarVisible,
					transitionSpec = { fadeInWithFadeOut },
					label = "MatchesForGameTopBarTransition"
				) { visible ->

					if (visible) {
						TopBarWithSearch(
							searchInput = searchInput,
							onSearchInputChanged = onSearchInputChanged,
							onCloseClick = {
								isSearchBarVisible = false
							},
							onClearClick = {
								onSearchInputChanged(TextFieldValue())
							},
						)
					} else {
						MatchesForSingleGameDefaultActionBar(
							title = title,
							onSearchClick = { isSearchBarVisible = true },
							onSortClick = onSortClick,
							onEditGameClick = onEditGameClick,
						)
					}
				}
			}

			SingleGameTabBar(selectedTab, onTabClick)
		}
	}
}

@Composable
fun MatchesForSingleGameDefaultActionBar(
	title: String,
	onSearchClick: () -> Unit,
	onSortClick: () -> Unit,
	onEditGameClick: () -> Unit,
) {

	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier.fillMaxWidth()
	) {

		Text(
			text = title,
			style = MaterialTheme.typography.titleLarge,
			overflow = TextOverflow.Ellipsis,
			maxLines = 1,
			modifier = Modifier.weight(1f, fill = false)
		)

		Row {
			Icon(
				imageVector = Icons.Rounded.Search,
				contentDescription = null,
				modifier = Modifier
					.minimumInteractiveComponentSize()
					.clip(CircleShape)
					.clickable(onClick = onSearchClick)
					.padding(4.dp)
			)
			Icon(
				painter = painterResource(id = R.drawable.ic_sort),
				contentDescription = null,
				modifier = Modifier
					.minimumInteractiveComponentSize()
					.clip(CircleShape)
					.clickable(onClick = onSortClick)
					.padding(4.dp)
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
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleGameTabBar(
	selectedTab: SingleGameScreen,
	onTabSelected: (SingleGameScreen) -> Unit
) {

	PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
		SingleGameScreen.entries.forEachIndexed { index, screen ->
			Tab(
				selected = index == selectedTab.ordinal,
				onClick = { onTabSelected(screen) },
				text = {
					Text(
						text = stringResource(id = screen.titleResource),
						style = MaterialTheme.typography.titleSmall,
					)
				},
				icon = {
					Icon(
						painter = painterResource(id = screen.iconResource),
						contentDescription = null,
					)
				},
				unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
			)
		}
	}
}

// endregion

@Composable
fun MatchesForGameSortOptionsDialog(
	sortMode: MatchSortMode,
	sortDirection: SortDirection,
	onSortModeChanged: (MatchSortMode) -> Unit,
	onSortDirectionChanged: (SortDirection) -> Unit,
	onDismiss: () -> Unit
) {
	Dialog(onDismiss) {
		Surface(
			shape = MaterialTheme.shapes.large,
			tonalElevation = 2.dp
		) {
			Column(Modifier.padding(Spacing.screenEdge)) {
				Text(
					text = stringResource(id = R.string.sort_by),
					style = MaterialTheme.typography.titleLarge,
					color = MaterialTheme.colorScheme.onBackground,
				)
				Spacer(modifier = Modifier.height(Spacing.subSectionContent))
				MatchSortMode.entries.forEach { option ->
					RadioButtonOption(
						menuOption = option,
						isSelected = sortMode == option,
						onSelected = onSortModeChanged,
						unselectedColor = MaterialTheme.colorScheme.onBackground,
					)
				}
				Spacer(Modifier.height(Spacing.betweenSections))
				Text(
					text = stringResource(id = R.string.sort_direction),
					style = MaterialTheme.typography.titleLarge,
					color = MaterialTheme.colorScheme.onBackground,
				)
				Spacer(modifier = Modifier.height(Spacing.subSectionContent))
				SortDirection.entries.forEach { option ->
					RadioButtonOption(
						menuOption = option,
						isSelected = sortDirection == option,
						onSelected = onSortDirectionChanged,
						unselectedColor = MaterialTheme.colorScheme.onBackground,
					)
				}
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchesForGameScreen(
	screenTitle: String,
	searchInput: TextFieldValue,
	isSortDialogShowing: Boolean,
	sortDirection: SortDirection,
	sortMode: MatchSortMode,
	matches: List<MatchDomainModel>,
	filteredIndices: List<Int>,
	listState: LazyListState,
	ads: List<NativeAd>,
	onEditGameClick: () -> Unit,
	onSortButtonClick: () -> Unit,
	onStatisticsTabClick: () -> Unit,
	onMatchClick: (Long) -> Unit,
	onAddMatchClick: () -> Unit,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	onSortModeChanged: (MatchSortMode) -> Unit,
	onSortDirectionChanged: (SortDirection) -> Unit,
	onSortDialogDismiss: () -> Unit,
) {

	Box {

		if (isSortDialogShowing) {
			MatchesForGameSortOptionsDialog(
				sortMode,
				sortDirection,
				onSortModeChanged,
				onSortDirectionChanged,
				onSortDialogDismiss
			)
		}

		Scaffold(
			topBar = {
				MatchesForSingleGameTopBar(
					searchInput = searchInput,
					selectedTab = SingleGameScreen.MatchesForGame,
					title = screenTitle,
					onSearchInputChanged = onSearchInputChanged,
					onTabClick = {
						when (it) {
							SingleGameScreen.MatchesForGame -> {}
							SingleGameScreen.StatisticsForGame -> onStatisticsTabClick()
						}
					},
					onSortClick = onSortButtonClick,
					onEditGameClick = onEditGameClick,
					modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
				)
			},
			floatingActionButton = {
				ExtendedFloatingActionButton(
					text = {
						Text(text = stringResource(id = R.string.text_new_match))
					},
					icon = {
						Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
					},
					onClick = onAddMatchClick,
					modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
				)
			},
			contentWindowInsets = WindowInsets(0.dp)
		) { innerPadding ->

			Column(
				modifier = Modifier
					.padding(innerPadding)
					.padding(horizontal = Spacing.screenEdge)
					.imePadding()
			) {

				AnimatedContent(
					targetState = filteredIndices.isNotEmpty() to searchInput.text.isNotBlank(),
					transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
					label = MatchesForGameConstants.AnimationLabel.HelperBox
				) {

					when (it) {

						// There are matches and there is search input
						true to true -> {

							Column(
								verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
								modifier = Modifier.padding(top = Spacing.sectionContent)
							) {
								HelperBox(
									message = stringResource(
										id = R.string.text_showing_search_results,
										searchInput.text
									),
									type = HelperBoxType.Info,
									maxLines = 2,
								)
								HorizontalDivider()
							}
						}

						// There are matches and there is no search input
						true to false -> {}

						// There are no matches and there is search input
						false to true -> {
							Column(
								verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
								modifier = Modifier.padding(top = Spacing.sectionContent)
							) {
								HelperBox(
									message = stringResource(
										id = R.string.text_empty_match_search_results,
										searchInput.text
									),
									type = HelperBoxType.Missing,
									maxLines = 2,
								)
								HorizontalDivider()
								LargeImageAdCard(ad = ads.firstOrNull())
							}
						}

						// There are no matches and no search input
						false to false -> {
							Column(
								verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
								modifier = Modifier.padding(top = Spacing.sectionContent)
							) {
								HelperBox(
									message = stringResource(R.string.text_empty_matches),
									type = HelperBoxType.Missing,
									maxLines = 2,
								)
								HorizontalDivider()
								LargeImageAdCard(ad = ads.firstOrNull())
							}
						}
					}
				}

				LazyColumn(
					state = listState,
					contentPadding = PaddingValues(
						top = Spacing.sectionContent, bottom = Spacing.paddingForFab
					),
					verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
				) {
					val formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT).apply {
						timeZone = TimeZone.getTimeZone("UTC")
					}
					val items = if (searchInput.text.isBlank()) {
						matches
					} else {
						matches.filterIndexed { index, _ -> filteredIndices.contains(index) }
					}

					items.forEachIndexed { i, match ->
						val showAd = (matches.size < 3 && i == matches.lastIndex)
								|| ((i - 1) % 6 == 0 && i != matches.lastIndex)

						item(key = match.id) {
							MatchCard(
								number = "${matches.indexOf(match) + 1}",
								date = formatter.format(match.dateMillis),
								location = match.location,
								players = match.players,
								totals = match.players.map {
									it.categoryScores
										.sumOf { score -> score.scoreAsBigDecimal ?: BigDecimal.ZERO }
										.toShortFormatString()
								},
								modifier = Modifier
									.clip(MaterialTheme.shapes.medium)
									.clickable {
										onMatchClick(match.id)
									}
									.animateItemPlacement(
										animationSpec = tween(
											durationMillis = DurationMs.MEDIUM,
											easing = Ease
										)
									)
							)
						}

						if (showAd) {
							item(key = "ad$i") {
								val ad = if (ads.isNotEmpty()) {
									val previousAdCount = (i - 1) / 6
									ads[previousAdCount % ads.size]
								} else {
									null
								}

								LargeImageAdCard(
									ad = ad,
									modifier = Modifier.animateItemPlacement()
								)
							}
						}
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
		}
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Normal() {
	MedianMeepleTheme {
		MatchesForGameScreen(
			uiState = SingleGameSampleData.Normal.toMatchesForGameUiState(),
			onSearchInputChanged = {},
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onEditGameClick = {},
			onStatisticsTabClick = {},
			onSortButtonClick = {},
			onMatchClick = {},
			onAddMatchClick = {},
			onSortDialogDismiss = {}
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LongGameName() {
	MedianMeepleTheme {
		MatchesForGameScreen(
			uiState = SingleGameSampleData.LongGameName.toMatchesForGameUiState(),
			onSearchInputChanged = {},
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onEditGameClick = {},
			onStatisticsTabClick = {},
			onSortButtonClick = {},
			onMatchClick = {},
			onAddMatchClick = {},
			onSortDialogDismiss = {}
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NoMatches() {
	MedianMeepleTheme {
		MatchesForGameScreen(
			uiState = SingleGameSampleData.NoMatches.toMatchesForGameUiState(),
			onSearchInputChanged = {},
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onEditGameClick = {},
			onStatisticsTabClick = {},
			onSortButtonClick = {},
			onMatchClick = {},
			onAddMatchClick = {},
			onSortDialogDismiss = {}
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EmptySearch() {
	MedianMeepleTheme {
		MatchesForGameScreen(
			uiState = SingleGameSampleData.EmptySearch.toMatchesForGameUiState(),
			onSearchInputChanged = {},
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onEditGameClick = {},
			onStatisticsTabClick = {},
			onSortButtonClick = {},
			onMatchClick = {},
			onAddMatchClick = {},
			onSortDialogDismiss = {}
		)
	}
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun MatchesForGameSortDialogPreviewDarkMode() {
	MedianMeepleTheme {
		MatchesForGameSortOptionsDialog(
			sortMode = MatchSortMode.ByMatchAge,
			sortDirection = SortDirection.Descending,
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onDismiss = {}
		)
	}
}

@Preview
@Composable
fun MatchesForGameSortDialogPreview() {
	MedianMeepleTheme {
		MatchesForGameSortOptionsDialog(
			sortMode = MatchSortMode.ByMatchAge,
			sortDirection = SortDirection.Descending,
			onSortModeChanged = {},
			onSortDirectionChanged = {},
			onDismiss = {}
		)
	}
}
