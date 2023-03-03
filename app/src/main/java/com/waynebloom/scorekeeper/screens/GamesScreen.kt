package com.waynebloom.scorekeeper.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.enums.GamesTopBarState
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.ext.toAdSeparatedListlets

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun GamesScreen(
    games: List<GameEntity>,
    currentAd: NativeAd?,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var listState: ListState by rememberSaveable { mutableStateOf(ListState.Default) }
    var searchString: String by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            GamesTopBar(
                title = stringResource(id = R.string.header_games),
                themeColor = MaterialTheme.colors.primary,
                searchString = searchString,
                onSearchStringChanged = { searchString = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                shape = MaterialTheme.shapes.small,
                backgroundColor = MaterialTheme.colors.primary,
                onClick = { onAddNewGameTap() }
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) { contentPadding ->
        Column(
            modifier = modifier
                .padding(contentPadding)
                .padding(horizontal = 16.dp)
        ) {
            val gamesToDisplay = games
                .filter { it.name.lowercase().contains(searchString.lowercase()) }
            listState = when {
                games.isEmpty() -> ListState.ListEmpty
                gamesToDisplay.isEmpty() -> ListState.SearchResultsEmpty
                searchString.isNotBlank() -> ListState.SearchResultsNotEmpty
                else -> ListState.Default
            }

            AnimatedVisibility(
                visible = listState != ListState.Default,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                DullColoredTextCard { color, _ ->
                    AnimatedContent(
                        targetState = listState,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220, delayMillis = 90))
                                .with(fadeOut(animationSpec = tween(90)))
                        }
                    ) { state ->
                        when (state) {
                            ListState.Default -> Unit
                            ListState.ListEmpty -> {
                                Text(
                                    text = stringResource(R.string.text_empty_games),
                                    color = color,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            ListState.SearchResultsEmpty -> {
                                Text(
                                    text = stringResource(
                                        id = R.string.text_empty_game_search_results,
                                        searchString
                                    ),
                                    color = color,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            ListState.SearchResultsNotEmpty -> {
                                Text(
                                    text = stringResource(
                                        R.string.text_showing_search_results,
                                        searchString
                                    ),
                                    color = color,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                gamesToDisplay.toAdSeparatedListlets().forEachIndexed { index, listlet ->
                    items(
                        items = listlet,
                        key = { it.id }
                    ) { game ->
                        GameCard(
                            name = game.name,
                            color = LocalGameColors.current.getColorByKey(game.color),
                            onClick = { onSingleGameTap(game.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        )
                    }
                    item {
                        if (index == 0 || listlet.size >= 10) {
                            AdCard(currentAd)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GamesDefaultActionBar(
    themeColor: Color,
    onOpenSearchTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onOpenSearchTap() }
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            tint = themeColor,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(12.dp)
        )
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun GamesTopBar(
    title: String,
    themeColor: Color,
    searchString: String,
    onSearchStringChanged: (String) -> Unit
) {
    var topBarState: GamesTopBarState by rememberSaveable { mutableStateOf(GamesTopBarState.Default) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(16.dp)
            .defaultMinSize(minHeight = 48.dp)
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = topBarState == GamesTopBarState.Default,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = title,
                    color = themeColor,
                    style = MaterialTheme.typography.h5,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
        ) {
            AnimatedContent(targetState = topBarState) { state ->
                when (state) {
                    GamesTopBarState.Default -> {
                        GamesDefaultActionBar(
                            themeColor = themeColor,
                            onOpenSearchTap = { topBarState = GamesTopBarState.SearchBarOpen }
                        )
                    }
                    GamesTopBarState.SearchBarOpen -> {
                        SearchActionBar(
                            searchString = searchString,
                            themeColor = themeColor,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseTap = {
                                topBarState = GamesTopBarState.Default
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}