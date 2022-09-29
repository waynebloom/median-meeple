package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.MatchCard
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme

@Composable
fun SingleGameScreen(
    game: GameObject,
    onEditGameTap: () -> Unit,
    onNewMatchTap: (Long) -> Unit,
    onSingleMatchTap: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColorResource = GameColor.valueOf(game.entity.color).color
    var searchString: String by rememberSaveable { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewMatchTap(game.entity.id) },
                shape = MaterialTheme.shapes.small,
                backgroundColor = gameColorResource,
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
                gameColorResource = gameColorResource
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(game.matches) { match ->
                    if (showMatch(match, searchString)) {
                        MatchCard(
                            match = match,
                            gameInitial = game.entity.name.first().uppercase(),
                            gameColor = gameColorResource,
                            onSingleMatchTap = onSingleMatchTap,
                            showGameIdentifier = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchAndFilterBar(
    searchString: String,
    onSearchStringChanged: (String) -> Unit,
    onEditGameTap: () -> Unit,
    gameColorResource: Color
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

        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = gameColorResource,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        CompositionLocalProvider(
            LocalTextSelectionColors.provides(
                TextSelectionColors(
                    handleColor = gameColorResource,
                    backgroundColor = gameColorResource.copy(0.3f)
        ))) {
            BasicTextField(
                value = searchString.ifEmpty {
                    if (!searchBarFocused) {
                        stringResource(R.string.search_placeholder_match)
                    } else ""
                },
                onValueChange = { onSearchStringChanged(it) },
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface,
                ),
                cursorBrush = SolidColor(gameColorResource),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { searchBarFocused = it.hasFocus }
            )
        }
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Transparent
            ),
            elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sort),
                tint = gameColorResource,
                contentDescription = null
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
                tint = gameColorResource,
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
            onEditGameTap = { /*TODO*/ },
            onNewMatchTap = {},
            onSingleMatchTap = {_,_->}
        )
    }
}