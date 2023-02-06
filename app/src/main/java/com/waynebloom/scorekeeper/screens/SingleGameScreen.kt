package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.PreviewMatchObjects
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.enums.MatchSortingMode
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SingleGameTopBarState
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.toAdSeparatedListlets
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme

@OptIn(ExperimentalAnimationApi::class, ExperimentalFoundationApi::class)
@Composable
fun SingleGameScreen(
    game: GameObject,
    currentAd: NativeAd?,
    onEditGameTap: () -> Unit,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColor = LocalGameColors.current.getColorByKey(game.entity.color)
    var listState: ListState by rememberSaveable { mutableStateOf(ListState.Default) }
    var searchString: String by rememberSaveable { mutableStateOf("") }
    var sortDescending: Boolean by rememberSaveable { mutableStateOf(true) }
    var sortingMode: MatchSortingMode by rememberSaveable { mutableStateOf(MatchSortingMode.ByMatchAge) }

    Scaffold(
        topBar = {
            SingleGameTopBar(
                searchString = searchString,
                sortDescending = sortDescending,
                sortingMode = sortingMode,
                title = game.entity.name,
                themeColor = gameColor,
                onSearchStringChanged = { searchString = it },
                onSortDirectionChanged = { sortDescending = it },
                onSortingModeChanged = { sortingMode = it },
                onEditGameTap = onEditGameTap,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewMatchTap() },
                shape = MaterialTheme.shapes.small,
                backgroundColor = gameColor,
                contentColor = MaterialTheme.colors.onPrimary
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
            val matchesToDisplay = filterAndSortMatches(
                scoringMode = ScoringMode.getModeByOrdinal(game.entity.scoringMode),
                matches = game.matches,
                searchString = searchString,
                sortingMode = sortingMode,
                sortDescending = sortDescending
            )
            listState = when {
                game.matches.isEmpty() -> ListState.ListEmpty
                matchesToDisplay.isEmpty() -> ListState.SearchResultsEmpty
                searchString.isNotBlank() -> ListState.SearchResultsNotEmpty
                else -> ListState.Default
            }

            AnimatedVisibility(
                visible = listState != ListState.Default,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                DullColoredTextCard(color = gameColor) { color, _ ->
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
                                    text = stringResource(R.string.text_empty_matches),
                                    color = color,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            ListState.SearchResultsEmpty -> {
                                Text(
                                    text = stringResource(
                                        id = R.string.text_empty_match_search_results,
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
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                matchesToDisplay.toAdSeparatedListlets().forEachIndexed { index, listlet ->
                    items(
                        items = listlet,
                        key = { it.entity.id }
                    ) { match ->
                        MatchCard(
                            game = game.entity,
                            match = match,
                            onSingleMatchTap = onSingleMatchTap,
                            showGameIdentifier = false,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                    item {
                        if (index == 0 || listlet.size >= 10) {
                            AdCard(
                                currentAd = currentAd,
                                themeColor = gameColor.toArgb()
                            )
                        }
                    }
                }
            }
        }
    }
}

// region Top Bar

@Composable
fun SingleGameSortMenuActionBar(
    themeColor: Color,
    sortDescending: Boolean,
    sortingMode: MatchSortingMode,
    onSortDirectionChanged: (Boolean) -> Unit,
    onSortingModeChanged: (MatchSortingMode) -> Unit,
    onCloseTap: () -> Unit
) {
    Column(modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = stringResource(R.string.header_sort_menu),
                color = themeColor,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onSortDirectionChanged(!sortDescending) }
            ) {
                Icon(
                    imageVector = if (sortDescending) {
                        Icons.Rounded.KeyboardArrowDown
                    } else Icons.Rounded.KeyboardArrowUp,
                    tint = themeColor,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onCloseTap() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    tint = themeColor,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(12.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp)
        ) {
            MatchSortingMode.values().forEach { option ->
                SortingMenuOption(
                    menuOption = option,
                    themeColor = themeColor,
                    isSelected = sortingMode == option,
                    onSelected = { onSortingModeChanged(it) }
                )
            }
        }
    }
}

@Composable
fun SingleGameDefaultActionBar(
    themeColor: Color,
    onOpenSearchTap: () -> Unit,
    onSortTap: () -> Unit,
    onEditGameTap: () -> Unit,
) {
    Row {

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

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onSortTap() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                tint = themeColor,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onEditGameTap() }
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                tint = themeColor,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SingleGameTopBar(
    title: String,
    themeColor: Color,
    searchString: String,
    sortDescending: Boolean,
    sortingMode: MatchSortingMode,
    onSearchStringChanged: (String) -> Unit,
    onSortDirectionChanged: (Boolean) -> Unit,
    onSortingModeChanged: (MatchSortingMode) -> Unit,
    onEditGameTap: () -> Unit
) {
    var topBarState: SingleGameTopBarState by rememberSaveable { mutableStateOf(SingleGameTopBarState.Default) }

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
            visible = topBarState == SingleGameTopBarState.Default,
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
                    SingleGameTopBarState.Default -> {
                        SingleGameDefaultActionBar(
                            themeColor = themeColor,
                            onEditGameTap = onEditGameTap,
                            onSortTap = { topBarState = SingleGameTopBarState.SortMenuOpen },
                            onOpenSearchTap = { topBarState = SingleGameTopBarState.SearchBarOpen }
                        )
                    }
                    SingleGameTopBarState.SearchBarOpen -> {
                        SearchActionBar(
                            searchString = searchString,
                            themeColor = themeColor,
                            onSearchStringChanged = onSearchStringChanged,
                            onCloseTap = {
                                topBarState = SingleGameTopBarState.Default
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    }
                    SingleGameTopBarState.SortMenuOpen -> {
                        SingleGameSortMenuActionBar(
                            themeColor = themeColor,
                            sortDescending = sortDescending,
                            sortingMode = sortingMode,
                            onSortDirectionChanged = onSortDirectionChanged,
                            onSortingModeChanged = onSortingModeChanged,
                            onCloseTap = { topBarState = SingleGameTopBarState.Default }
                        )
                    }
                }
            }
        }
    }
}

// endregion

/**
 * This will filter and sort the passed matches based on
 * the current string being searched and the current sort
 * mode.
 *
 * Order of operations:
 *      1. Iterate through the list, adding items that match the search string
 *         to a sublist.
 *          a. If a match has no scores, add it to a separate sublist.
 *      2. Sort the filtered items sublist as designated by the sorting mode.
 *      3. Reverse the order of the list if the user selects descending sort.
 *      4. Add the empty matches sublist to the end of the sorted list.
 *      5. Return
 */
private fun filterAndSortMatches(
    scoringMode: ScoringMode,
    matches: List<MatchObject>,
    searchString: String,
    sortingMode: MatchSortingMode,
    sortDescending: Boolean
): List<MatchObject> {
    val matchesToSort: MutableList<MatchObject> = mutableListOf()
    val emptyMatches: MutableList<MatchObject> = mutableListOf()
    matches.forEach {
        if (showMatch(it, searchString)) {
            if (it.players.isEmpty()) {
                emptyMatches.add(it)
            } else matchesToSort.add(it)
        }
    }
    var matchesInOrder: List<MatchObject> = when (sortingMode) {
        MatchSortingMode.ByMatchAge -> matchesToSort.reversed()
        MatchSortingMode.ByWinningPlayer -> matchesToSort.sortedBy { match ->
            match.players.getWinningPlayer(scoringMode)?.entity?.name
        }
        MatchSortingMode.ByWinningScore -> matchesToSort.sortedBy { match ->
            match.players.getWinningPlayer(scoringMode)?.entity?.score
        }
        MatchSortingMode.ByPlayerCount -> matchesToSort.sortedBy { it.players.size }
    }
    if (sortDescending) matchesInOrder = matchesInOrder.reversed()
    return matchesInOrder.plus(emptyMatches)
}

private fun matchContainsPlayerWithString(
    match: MatchObject,
    substring: String
): Boolean {
    return match.players.any {
        it.entity.name.lowercase().contains(substring.lowercase())
    }
}

private fun matchContainsExactScoreMatch(
    match: MatchObject,
    scoreValue: Long?
): Boolean {
    return if (scoreValue != null) {
        match.players.any {
            it.entity.score == scoreValue
        }
    } else false
}

private fun showMatch(
    match: MatchObject,
    searchString: String
): Boolean {
    if (searchString.isEmpty()) return true
    return matchContainsPlayerWithString(match, searchString) ||
            matchContainsExactScoreMatch(match, searchString.toLongOrNull())
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SingleGameScreenPreview() {
    ScoreKeeperTheme {
        SingleGameScreen(
            game = GameObject(
                entity = GameEntity(
                    name = "Wingspan",
                    color = "ORANGE"
                ),
                matches = PreviewMatchObjects
            ),
            currentAd = null,
            onEditGameTap = {},
            onNewMatchTap = {},
            onSingleMatchTap = {}
        )
    }
}