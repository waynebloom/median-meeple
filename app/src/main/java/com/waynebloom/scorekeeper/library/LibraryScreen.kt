package com.waynebloom.scorekeeper.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.waynebloom.scorekeeper.components.GameListItemNew
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.IconButton
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
            GamesTopBar(
                title = stringResource(id = R.string.header_games),
                searchInput = searchInput,
                onSearchInputChanged = { onSearchInputChanged(it) },
            )
        },
        floatingActionButton = { MedianMeepleFab(onClick = onAddNewGameClick) },
        modifier = modifier,
    ) { contentPadding ->

        Column(Modifier.padding(contentPadding)) {

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

                            Divider()
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

                            Divider()
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

                            Divider()
                        }
                    }
                }
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = PaddingValues(bottom = Spacing.paddingForFab),
            ) {

                val subListsBetweenAds = games.toAdSeparatedSubLists()

                subListsBetweenAds.forEachIndexed { index, subList ->

                    items(
                        items = subList,
                        key = { it.id }
                    ) { game ->

                        GameListItemNew(
                            name = game.name.value.text,
                            gameColor = LocalCustomThemeColors.current.getColorByKey(game.color),
                            onClick = { onGameClick(game.id) },
                            modifier = Modifier
                                .padding(
                                    horizontal = Spacing.screenEdge,
                                    vertical = Spacing.subSectionContent)
                                .fillMaxWidth()
                                .animateItemPlacement()
                        )
                    }

                    if (index != subListsBetweenAds.lastIndex) {
                        item {
                            Box(Modifier
                                .padding(horizontal = Spacing.screenEdge, vertical = Spacing.sectionContent)
                            ) {
                                AdCard(ad = ad)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamesDefaultActionBar(
    title: String,
    onSearchClick: () -> Unit
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(Size.topBarHeight)
    ) {

        Text(
            text = title,
            color = MaterialTheme.colors.primary,
            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        IconButton(
            imageVector = Icons.Rounded.Search,
            backgroundColor = Color.Transparent,
            foregroundColor = MaterialTheme.colors.primary,
            onClick = { onSearchClick() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun GamesTopBar(
    title: String,
    searchInput: TextFieldValue,
    onSearchInputChanged: (TextFieldValue) -> Unit,
) {

    var isSearchBarVisible by rememberSaveable { mutableStateOf(false) }

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = Spacing.screenEdge, end = Spacing.screenEdge / 2)
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
                    GamesDefaultActionBar(
                        title = title,
                        onSearchClick = { isSearchBarVisible = true }
                    )
                }
            }
        }

        Divider()
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
