package com.waynebloom.scorekeeper.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.GameListItem
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.MedianMeepleFab
import com.waynebloom.scorekeeper.components.SearchTopBar
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.enums.GamesTopBarState
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ext.toAdSeparatedListlets
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.fadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.viewmodel.GamesViewModel
import com.waynebloom.scorekeeper.viewmodel.GamesViewModelFactory

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun GamesScreen(
    games: List<GameEntity>,
    currentAd: NativeAd?,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = viewModel<GamesViewModel>(
        key = TopLevelScreen.EditGame.name,
        factory = GamesViewModelFactory())
    viewModel.updateListState(games)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GamesTopBar(
                state = viewModel.topBarState,
                title = stringResource(id = R.string.header_games),
                isSearchBarFocused = viewModel.isSearchBarFocused,
                searchString = viewModel.searchString,
                themeColor = MaterialTheme.colors.primary,
                onClearFiltersTap = { viewModel.onClearFiltersTap() },
                onSearchBarFocusedChanged = { viewModel.onSearchBarFocusedChanged(it) },
                onSearchStringChanged = { viewModel.onSearchStringChanged(it, coroutineScope) },
                onStateChanged = { viewModel.onTopBarStateChanged(it) }
            )
        },
        floatingActionButton = { MedianMeepleFab(onTap = onAddNewGameTap) },
        modifier = modifier,
    ) { contentPadding ->

        Column(modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = Spacing.screenEdge)
        ) {

            AnimatedContent(
                targetState = viewModel.listState,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay },
            ) {

                if (it != ListState.Default) {

                    val type = if (it == ListState.SearchResultsNotEmpty) {
                        HelperBoxType.Info
                    } else HelperBoxType.Missing

                    val text = when (it) {
                        ListState.ListEmpty -> stringResource(R.string.text_empty_games)
                        ListState.SearchResultsEmpty -> stringResource(
                            id = R.string.text_empty_game_search_results,
                            viewModel.searchString
                        )
                        ListState.SearchResultsNotEmpty -> stringResource(
                            R.string.text_showing_search_results,
                            viewModel.searchString
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
                state = viewModel.lazyListState,
                contentPadding = PaddingValues(
                    top = Spacing.sectionContent, bottom = Spacing.paddingForFab),
                verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
            ) {

                viewModel
                    .getGamesToDisplay(games)
                    .toAdSeparatedListlets()
                    .forEachIndexed { index, listlet ->

                        items(items = listlet, key = { it.id }) { game ->

                            GameListItem(
                                name = game.name,
                                color = LocalGameColors.current.getColorByKey(game.color),
                                onClick = { onSingleGameTap(game.id) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            )
                        }

                        item {
                            if (index == 0 || listlet.size >= 10) AdCard(currentAd)
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

        CustomIconButton(
            imageVector = Icons.Rounded.Search,
            backgroundColor = Color.Transparent,
            foregroundColor = themeColor,
            onTap = { onOpenSearchTap() }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Composable
fun GamesTopBar(
    state: GamesTopBarState,
    title: String,
    isSearchBarFocused: Boolean,
    searchString: String,
    themeColor: Color,
    onClearFiltersTap: () -> Unit,
    onSearchBarFocusedChanged: (Boolean) -> Unit,
    onSearchStringChanged: (String) -> Unit,
    onStateChanged: (GamesTopBarState) -> Unit,
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
            ) {

                when (it) {

                    GamesTopBarState.Default -> {
                        GamesDefaultActionBar(
                            title = title,
                            themeColor = themeColor,
                            onOpenSearchTap = { onStateChanged(GamesTopBarState.SearchBarOpen) },
                        )
                    }
                    GamesTopBarState.SearchBarOpen -> {
                        SearchTopBar(
                            isSearchBarFocused = isSearchBarFocused,
                            searchString = searchString,
                            themeColor = themeColor,
                            onClearFiltersTap = onClearFiltersTap,
                            onSearchBarFocusChanged = onSearchBarFocusedChanged,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseTap = {
                                onStateChanged(GamesTopBarState.Default)
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