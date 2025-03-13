package com.waynebloom.scorekeeper.library

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.LargeImageAdCard
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.NewGameCard
import com.waynebloom.scorekeeper.components.SmallImageAdCard
import com.waynebloom.scorekeeper.components.TopBarWithSearch
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun LibraryScreen(
	uiState: LibraryUiState,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	onAddGameClick: () -> Unit,
	onGameClick: (Long) -> Unit,
	modifier: Modifier = Modifier,
) {

	when (uiState) {
		is LibraryUiState.Loading -> {
			Loading()
		}

		is LibraryUiState.Content -> {
			LibraryScreen(
				gameCards = uiState.gameCards,
				searchInput = uiState.searchInput,
				ads = uiState.ads,
				onGameClick = onGameClick,
				onAddNewGameClick = onAddGameClick,
				onSearchInputChanged = onSearchInputChanged,
				modifier = modifier
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
	gameCards: List<LibraryGameCard>,
	searchInput: TextFieldValue,
	ads: List<NativeAd>,
	onGameClick: (Long) -> Unit,
	onAddNewGameClick: () -> Unit,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
) {

	Scaffold(
		topBar = {
			LibraryTopBar(
				title = stringResource(id = R.string.header_games),
				searchInput = searchInput,
				onSearchInputChanged = onSearchInputChanged,
				modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = onAddNewGameClick,
				content = {
					Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
				},
				modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
			)
		},
		contentWindowInsets = WindowInsets(0.dp),
		modifier = modifier,
	) { innerPadding ->

		Column(Modifier.padding(innerPadding)) {

			AnimatedContent(
				targetState = gameCards.isNotEmpty() to searchInput.text.isNotBlank(),
				transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
				label = LibraryConstants.ListAnimationTag,
			) {

				when (it) {

					// There are games and there is search input
					true to true -> {
						Column(
							verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
							modifier = Modifier.padding(horizontal = 16.dp)
						) {
							HelperBox(
								message = stringResource(
									id = R.string.text_showing_search_results,
									searchInput.text
								),
								type = HelperBoxType.Info,
								maxLines = 2
							)
							HorizontalDivider()
						}
					}

					// There are games and there is no search input
					true to false -> {}

					// There are no games and there is search input
					false to true -> {
						Column(
							verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
							modifier = Modifier.padding(horizontal = 16.dp)
						) {
							HelperBox(
								message = stringResource(
									id = R.string.text_empty_game_search_results,
									searchInput.text
								),
								type = HelperBoxType.Missing,
								maxLines = 2
							)
							HorizontalDivider()
							LargeImageAdCard(ad = ads.firstOrNull())
						}
					}

					// There are no games and no search input
					false to false -> {
						Column(
							verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
							modifier = Modifier.padding(horizontal = 16.dp)
						) {
							HelperBox(
								message = stringResource(R.string.text_empty_games),
								type = HelperBoxType.Missing,
								maxLines = 2
							)
							HorizontalDivider()
							LargeImageAdCard(ad = ads.firstOrNull())
						}
					}
				}
			}

			LazyVerticalStaggeredGrid(
				columns = StaggeredGridCells.Adaptive(160.dp),
				verticalItemSpacing = Spacing.sectionContent,
				horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
				contentPadding = PaddingValues(
					start = Spacing.screenEdge,
					end = Spacing.screenEdge,
					top = if (searchInput.text.isBlank()) {
						0.dp
					} else {
						Spacing.sectionContent
					},
					bottom = Spacing.paddingForFab
				),
			) {
				gameCards.forEachIndexed { i, card ->
					val showAd = (gameCards.size < 5 && i == gameCards.lastIndex)
							|| ((i - 3) % 13 == 0 && i != gameCards.lastIndex)

					item(key = card.id) {
						NewGameCard(
							name = card.name,
							color = card.color
								.copy(alpha = 0.2f)
								.compositeOver(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
							highScore = card.highScore,
							noOfMatches = card.noOfMatches,
							modifier = Modifier
								.clip(MaterialTheme.shapes.medium)
								.clickable {
									Log.d("LibraryScreen", "Game with id ${card.id} was clicked.")
									onGameClick(card.id)
								}
								.animateItemPlacement()
						)
					}

					if (showAd) {
						item(key = "ad$i") {
							val ad = if (ads.isNotEmpty()) {
								val previousAdCount = (i - 3) / 13
								ads[previousAdCount % ads.size]
							} else {
								null
							}

							SmallImageAdCard(
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

@Composable
fun LibraryDefaultActionBar(
	title: String,
	onSearchClick: () -> Unit,
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
			maxLines = 1
		)

		Icon(
			imageVector = Icons.Rounded.Search,
			contentDescription = null,
			modifier = Modifier
				.minimumInteractiveComponentSize()
				.clip(CircleShape)
				.clickable(onClick = onSearchClick)
				.padding(4.dp)
		)
	}
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun LibraryTopBar(
	title: String,
	searchInput: TextFieldValue,
	onSearchInputChanged: (TextFieldValue) -> Unit,
	modifier: Modifier = Modifier,
) {
	var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }
	Surface {
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically,
			modifier = modifier
				.padding(start = Spacing.screenEdge, end = 4.dp)
				.defaultMinSize(minHeight = Size.topBarHeight)
				.fillMaxWidth()
		) {

			AnimatedContent(
				targetState = isSearchBarVisible,
				transitionSpec = { fadeInWithFadeOut },
				label = LibraryConstants.TopBarAnimationTag,
			) {

				if (it) {
					TopBarWithSearch(
						searchInput = searchInput,
						onSearchInputChanged = onSearchInputChanged,
						onCloseClick = {
							isSearchBarVisible = false
						},
						onClearClick = {
							onSearchInputChanged(TextFieldValue())
						}
					)
				} else {
					LibraryDefaultActionBar(
						title = title,
						onSearchClick = { isSearchBarVisible = true },
					)
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
		LibraryScreen(
			uiState = LibrarySampleData.Default,
			onSearchInputChanged = {},
			onAddGameClick = {},
			onGameClick = {},
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun NoGames() {
	MedianMeepleTheme {
		LibraryScreen(
			uiState = LibrarySampleData.NoGames,
			onSearchInputChanged = {},
			onAddGameClick = {},
			onGameClick = {},
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ActiveSearch() {
	MedianMeepleTheme {
		LibraryScreen(
			uiState = LibrarySampleData.ActiveSearch,
			onSearchInputChanged = {},
			onAddGameClick = {},
			onGameClick = {},
		)
	}
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun EmptySearch() {
	MedianMeepleTheme {
		LibraryScreen(
			uiState = LibrarySampleData.EmptySearch,
			onSearchInputChanged = {},
			onAddGameClick = {},
			onGameClick = {},
		)
	}
}
