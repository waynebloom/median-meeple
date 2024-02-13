package com.waynebloom.scorekeeper.ui.library

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.AdCard
import com.waynebloom.scorekeeper.ui.components.IconButton
import com.waynebloom.scorekeeper.ui.components.GameListItem
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.MedianMeepleFab
import com.waynebloom.scorekeeper.ui.components.TopBarWithSearch
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.enums.LibraryTopBarState
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.ext.toAdSeparatedSubLists
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onAddGameTap: () -> Unit,
    onClearFiltersTap: () -> Unit,
    onGameTap: (Long) -> Unit,
    onSearchBarFocusedChanged: (Boolean) -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onTopBarStateChanged: (LibraryTopBarState) -> Unit,
    modifier: Modifier = Modifier
) {

    LibraryScreen(
        ad = uiState.ad,
        games = uiState.displayedGames,
        isSearchBarFocused = uiState.isSearchBarFocused,
        lazyListState = uiState.lazyListState,
        listDisplayState = uiState.listDisplayState,
        loading = uiState.loading,
        searchInput = uiState.searchInput,
        topBarState = uiState.topBarState,
        onAddNewGameTap = onAddGameTap,
        onClearFiltersTap = onClearFiltersTap,
        onGameTap = onGameTap,
        onSearchBarFocusedChanged = onSearchBarFocusedChanged,
        onSearchInputChanged = onSearchInputChanged,
        onTopBarStateChanged = onTopBarStateChanged,
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
    ad: NativeAd?,
    games: List<GameDataRelationModel>,
    isSearchBarFocused: Boolean,
    lazyListState: LazyListState,
    listDisplayState: ListDisplayState,
    loading: Boolean,
    searchInput: String,
    topBarState: LibraryTopBarState,
    onAddNewGameTap: () -> Unit,
    onClearFiltersTap: () -> Unit,
    onSearchBarFocusedChanged: (Boolean) -> Unit,
    onSearchInputChanged: (String) -> Unit,
    onGameTap: (Long) -> Unit,
    onTopBarStateChanged: (LibraryTopBarState) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            GamesTopBar(
                state = topBarState,
                title = stringResource(id = R.string.header_games),
                isSearchBarFocused = isSearchBarFocused,
                searchString = searchInput,
                themeColor = MaterialTheme.colors.primary,
                onClearFiltersTap = onClearFiltersTap,
                onSearchBarFocusedChanged = { onSearchBarFocusedChanged(it) },
                onSearchStringChanged = { onSearchInputChanged(it) },
                onStateChanged = { onTopBarStateChanged(it) }
            )
        },
        floatingActionButton = { MedianMeepleFab(onClick = onAddNewGameTap) },
        modifier = modifier,
    ) { contentPadding ->

        if (loading) {
            Loading()
        } else {
            Column(modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = Spacing.screenEdge)
            ) {

                AnimatedContent(
                    targetState = listDisplayState,
                    transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                    label = LibraryConstants.ListAnimationTag,
                ) {

                    if (it != ListDisplayState.ShowAll) {

                        val type = if (it == ListDisplayState.ShowFiltered) {
                            HelperBoxType.Info
                        } else HelperBoxType.Missing

                        val text = when (it) {
                            ListDisplayState.Empty -> stringResource(R.string.text_empty_games)
                            ListDisplayState.EmptyFiltered -> stringResource(
                                id = R.string.text_empty_game_search_results,
                                searchInput
                            )
                            ListDisplayState.ShowFiltered -> stringResource(
                                R.string.text_showing_search_results,
                                searchInput
                            )
                            else -> ""
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                            modifier = Modifier.padding(top = Spacing.sectionContent)
                        ) {

                            HelperBox(message = text, type = type, maxLines = 2)

                            Divider()
                        }
                    }
                }

                LazyColumn(
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        top = Spacing.sectionContent, bottom = Spacing.paddingForFab),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                ) {

                    val subListsBetweenAds = games.map { it.entity }
                        .toAdSeparatedSubLists()

                    subListsBetweenAds.forEachIndexed { index, subList ->

                        items(
                            items = subList,
                            key = { it.id }
                        ) { game ->

                            GameListItem(
                                name = game.name,
                                color = LocalCustomThemeColors.current.getColorByKey(game.color),
                                onClick = { onGameTap(game.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            )
                        }

                        if (index != subListsBetweenAds.lastIndex) {
                            item { AdCard(ad = ad) }
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
    themeColor: Color,
    onOpenSearchTap: () -> Unit
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
            color = themeColor,
            style = MaterialTheme.typography.h5,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )

        IconButton(
            imageVector = Icons.Rounded.Search,
            backgroundColor = Color.Transparent,
            foregroundColor = themeColor,
            onClick = { onOpenSearchTap() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun GamesTopBar(
    state: LibraryTopBarState,
    title: String,
    isSearchBarFocused: Boolean,
    searchString: String,
    themeColor: Color,
    onClearFiltersTap: () -> Unit,
    onSearchBarFocusedChanged: (Boolean) -> Unit,
    onSearchStringChanged: (String) -> Unit,
    onStateChanged: (LibraryTopBarState) -> Unit,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(start = Spacing.screenEdge, end = Spacing.screenEdge / 2)
                .defaultMinSize(minHeight = Size.topBarHeight)
                .fillMaxWidth()
        ) {

            AnimatedContent(
                targetState = state,
                transitionSpec = { fadeInWithFadeOut },
                label = LibraryConstants.TopBarAnimationTag,
            ) {

                when (it) {

                    LibraryTopBarState.Default -> {
                        GamesDefaultActionBar(
                            title = title,
                            themeColor = themeColor,
                            onOpenSearchTap = { onStateChanged(LibraryTopBarState.SearchBarOpen) },
                        )
                    }
                    LibraryTopBarState.SearchBarOpen -> {
                        TopBarWithSearch(
                            isSearchBarFocused = isSearchBarFocused,
                            searchString = searchString,
                            themeColor = themeColor,
                            onClearFiltersTap = onClearFiltersTap,
                            onSearchBarFocusChanged = onSearchBarFocusedChanged,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseTap = {
                                onStateChanged(LibraryTopBarState.Default)
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
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

        /*LibraryScreen(
            games = GameEntitiesDefaultPreview,
            currentAd = null,
            onAddNewGameTap = {},
            onSingleGameTap = {}
        )*/
    }
}
