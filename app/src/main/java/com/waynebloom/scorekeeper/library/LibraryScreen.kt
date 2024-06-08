package com.waynebloom.scorekeeper.library

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.SampleGames
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.GameCard
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.MedianMeepleFab
import com.waynebloom.scorekeeper.components.TopBarWithSearch
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.ext.toAdSeparatedSubLists
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
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

    // TODO: this screen needs some new life. Display some interesting data on game cards.
    // TODO: Check up on search function after this ^^. Sometimes a card was showing underneath the ad...?

    when(uiState) {
        is LibraryUiState.Loading -> {
            Loading()
        }
        is LibraryUiState.Content -> {
            LibraryScreen(
                games = uiState.games,
                lazyListState = uiState.lazyListState,
                searchInput = uiState.searchInput,
                ad = uiState.ad,
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
    games: List<GameDomainModel>,
    lazyListState: LazyListState,
    searchInput: TextFieldValue,
    ad: NativeAd?,
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
                onSearchInputChanged = { onSearchInputChanged(it) },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        floatingActionButton = {
            MedianMeepleFab(onClick = onAddNewGameClick)
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier,
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {

            AnimatedContent(
                targetState = games.isNotEmpty() to searchInput.text.isNotBlank(),
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                label = LibraryConstants.ListAnimationTag,
            ) {

                when(it) {

                    true to true -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = Spacing.sectionContent)
                        ) {

                            HelperBox(
                                message = stringResource(
                                    id = R.string.text_showing_search_results,
                                    searchInput.text),
                                type = HelperBoxType.Info,
                                maxLines = 2
                            )

                            HorizontalDivider()
                        }
                    }

                    true to false -> {

                    }

                    false to true -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = Spacing.sectionContent)
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
                        }
                    }

                    false to false -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(top = Spacing.sectionContent)
                        ) {

                            HelperBox(
                                message = stringResource(R.string.text_empty_games),
                                type = HelperBoxType.Missing,
                                maxLines = 2
                            )

                            HorizontalDivider()
                        }
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = Spacing.paddingForFab),
            ) {

                val subListsBetweenAds = games.toAdSeparatedSubLists(
                    firstAdMaximumIndex = 5,
                    itemsBetweenAds = 10
                )

                subListsBetweenAds.forEachIndexed { index, subList ->

                    items(
                        items = subList,
                        key = { it.id }
                    ) { game ->

                        GameCard(
                            name = game.name.text,
                            color = LocalCustomThemeColors.current.getColorByKey(game.color),
                            onClick = { onGameClick(game.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        )
                    }

                    if (index != subListsBetweenAds.lastIndex || subListsBetweenAds.size == 1) {
                        item {
                            AdCard(
                                ad = ad,
                                modifier = Modifier.padding(
                                    horizontal = Spacing.screenEdge,
                                    vertical = Spacing.sectionContent
                                )
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
    onSearchClick: () -> Unit
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
                        onSearchClick = { isSearchBarVisible = true }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun GamesScreenPreview() {
    MedianMeepleTheme {

        LibraryScreen(
            games = SampleGames,
            lazyListState = LazyListState(),
            searchInput = TextFieldValue(),
            ad = null,
            onGameClick = {},
            onAddNewGameClick = {},
            onSearchInputChanged = {}
        )
    }
}
