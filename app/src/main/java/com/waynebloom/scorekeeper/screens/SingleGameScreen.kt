package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.EmptyContentCard
import com.waynebloom.scorekeeper.components.MatchCard
import com.waynebloom.scorekeeper.components.showAdAtIndex
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme

@Composable
fun SingleGameScreen(
    game: GameObject,
    currentAd: NativeAd?,
    onEditGameTap: () -> Unit,
    onNewMatchTap: (Long) -> Unit,
    onSingleMatchTap: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColor = LocalGameColors.current.getColorByKey(game.entity.color)
    var searchString: String by rememberSaveable { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewMatchTap(game.entity.id) },
                shape = MaterialTheme.shapes.small,
                backgroundColor = gameColor,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Column(modifier = modifier) {
            SearchAndFilterBar(
                searchString = searchString,
                onSearchStringChanged = { searchString = it },
                onEditGameTap = onEditGameTap,
                gameColor = gameColor
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val displayedMatches = game.matches.filter { showMatch(it, searchString) }

                itemsIndexed(displayedMatches) { index, match ->
                    MatchCard(
                        match = match,
                        gameInitial = game.entity.name.first().uppercase(),
                        gameColor = gameColor,
                        onSingleMatchTap = onSingleMatchTap,
                        showGameIdentifier = false
                    )
                    if (showAdAtIndex(index, displayedMatches.size)) {
//                        Spacer(modifier = Modifier.height(8.dp))
//                        AdCard(
//                            currentAd = currentAd,
//                            themeColor = gameColor.toArgb()
//                        )
                    }
                }
                if (game.matches.isEmpty()) {
                    item {
                        EmptyContentCard(
                            text = stringResource(R.string.text_empty_matches),
                            color = gameColor
                        )
                    }
                    item {
                        AdCard(
                            currentAd = currentAd,
                            themeColor = gameColor.toArgb()
                        )
                    }
                } else if (displayedMatches.isEmpty()) {
                    item {
                        EmptyContentCard(
                            text = stringResource(id = R.string.text_empty_search_results, searchString),
                            color = gameColor
                        )
                    }
                    item {
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchAndFilterBar(
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onEditGameTap: () -> Unit,
    gameColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
            .height(48.dp)
            .fillMaxWidth()
    ) {
        var searchBarFocused: Boolean by rememberSaveable { mutableStateOf(false) }
        val textSelectionColors = TextSelectionColors(
            handleColor = gameColor,
            backgroundColor = gameColor.copy(0.3f)
        )
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = gameColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        CompositionLocalProvider(
            LocalTextSelectionColors.provides(textSelectionColors)
        ) {
            BasicTextField(
                value = searchString.ifEmpty {
                    if (!searchBarFocused) {
                        stringResource(R.string.search_placeholder_match)
                    } else ""
                },
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface,
                ),
                singleLine = true,
                cursorBrush = SolidColor(gameColor),
                onValueChange = { onSearchStringChanged(it) },
                keyboardActions = KeyboardActions (
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { searchBarFocused = it.hasFocus }
            )
        }
        Button(
            onClick = { onEditGameTap() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                tint = gameColor,
                contentDescription = null
            )
        }
    }
}

private fun showMatch(
    match: MatchObject,
    searchString: String
): Boolean {
    if (searchString.isEmpty()) return true
    return matchContainsPlayerWithString(match, searchString) ||
            matchContainsExactScoreMatch(match, searchString.toLongOrNull())
}

private fun matchContainsPlayerWithString(
    match: MatchObject,
    substring: String
): Boolean {
    return match.scores.any {
        it.name.contains(substring)
    }
}

private fun matchContainsExactScoreMatch(
    match: MatchObject,
    scoreValue: Long?
): Boolean {
    return if (scoreValue != null) {
        match.scores.any {
            it.scoreValue == scoreValue
        }
    } else false
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
                matches = listOf(
                    EMPTY_MATCH_OBJECT,
                    EMPTY_MATCH_OBJECT,
                    EMPTY_MATCH_OBJECT
                )
            ),
            currentAd = null,
            onEditGameTap = {},
            onNewMatchTap = {},
            onSingleMatchTap = {_,_->}
        )
    }
}