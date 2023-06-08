package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.ext.toAdSeparatedListlets
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchesForSingleGameScreen(
    currentAd: NativeAd?,
    gameEntity: GameEntity,
    listState: ListState,
    matches: List<MatchObject>,
    searchString: String,
    themeColor: Color,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewMatchTap() },
                shape = MaterialTheme.shapes.small,
                backgroundColor = themeColor,
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

            AnimatedVisibility(
                visible = listState != ListState.Default,
                enter = scaleIn(),
                exit = scaleOut(),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                DullColoredTextCard(color = themeColor) { color, _, _ ->
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

                matches.toAdSeparatedListlets().forEachIndexed { index, listlet ->

                    items(
                        items = listlet,
                        key = { it.entity.id }
                    ) { match ->

                        MatchCard(
                            gameEntity = gameEntity,
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
                                themeColor = themeColor.toArgb()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SingleGameScreenPreview() {
    MedianMeepleTheme {
        Scaffold {
            MatchesForSingleGameScreen(
                currentAd = null,
                gameEntity = GameEntity(
                    name = "Wingspan",
                    color = "ORANGE"
                ),
                listState = ListState.Default,
                matches = MatchObjectsDefaultPreview,
                searchString = "",
                themeColor = LocalGameColors.current.getColorByKey("ORANGE"),
                onNewMatchTap = {},
                onSingleMatchTap = {},
                modifier = Modifier.padding(it).padding(vertical = 16.dp)
            )
        }
    }
}