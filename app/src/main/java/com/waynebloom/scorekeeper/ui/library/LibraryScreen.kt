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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
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
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onAddGameClick: () -> Unit,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    LibraryScreen(
        ad = uiState.ad,
        games = uiState.displayedGames,
        lazyListState = uiState.lazyListState,
        loading = uiState.loading,
        searchInput = uiState.searchInput,
        onSearchInputChanged = onSearchInputChanged,
        onAddNewGameClick = onAddGameClick,
        onGameClick = onGameClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun LibraryScreen(
    ad: NativeAd?,
    games: List<GameDataRelationModel>,
    lazyListState: LazyListState,
    loading: Boolean,
    searchInput: TextFieldValue,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onAddNewGameClick: () -> Unit,
    onGameClick: (Long) -> Unit,
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

        if (loading) {
            Loading()
        } else {
            Column(modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = Spacing.screenEdge)
            ) {

                AnimatedContent(
                    targetState = games.isNotEmpty() to searchInput.text.isNotBlank(),
                    transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
                    label = LibraryConstants.ListAnimationTag,
                ) {

                    when(it) {

                        true to true -> {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
                                modifier = Modifier.padding(top = Spacing.sectionContent)
                            ) {

                                HelperBox(
                                    message = stringResource(
                                        id = R.string.text_showing_search_results,
                                        searchInput),
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
                                modifier = Modifier.padding(top = Spacing.sectionContent)
                            ) {

                                HelperBox(
                                    message = stringResource(
                                        id = R.string.text_empty_game_search_results,
                                        searchInput
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
                                modifier = Modifier.padding(top = Spacing.sectionContent)
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
                                onClick = { onGameClick(game.id) },
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
                            focusManager.clearFocus()
                            keyboardController?.hide()
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

        /*LibraryScreen(
            games = GameEntitiesDefaultPreview,
            currentAd = null,
            onAddNewGameClick = {},
            onSingleGameClick = {}
        )*/
    }
}
