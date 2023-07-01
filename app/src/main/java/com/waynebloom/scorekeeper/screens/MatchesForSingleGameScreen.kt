package com.waynebloom.scorekeeper.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.*
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.data.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.ext.toAdSeparatedListlets
import com.waynebloom.scorekeeper.ui.theme.DurationMillis
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.ui.theme.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.sizeTransformWithDelay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MatchesForSingleGameScreen(
    currentAd: NativeAd?,
    gameEntity: GameEntity,
    lazyListState: LazyListState,
    listState: ListState,
    matches: List<MatchObject>,
    searchString: String,
    themeColor: Color,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = { MedianMeepleFab(
            backgroundColor = themeColor,
            onTap = onNewMatchTap,
        ) },
        modifier = modifier,
    ) { contentPadding ->

        Column(modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = Spacing.screenEdge)
        ) {

            AnimatedContent(
                targetState = listState,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay }
            ) {

                if (it != ListState.Default) {

                    val type = if (it == ListState.SearchResultsNotEmpty) {
                        HelperBoxType.Info
                    } else HelperBoxType.Missing

                    val text = when (it) {
                        ListState.ListEmpty -> stringResource(R.string.text_empty_matches)
                        ListState.SearchResultsEmpty -> stringResource(
                            id = R.string.text_empty_match_search_results,
                            searchString
                        )
                        ListState.SearchResultsNotEmpty -> stringResource(
                            R.string.text_showing_search_results,
                            searchString
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

                matches.toAdSeparatedListlets().forEachIndexed { index, listlet ->

                    items(
                        items = listlet,
                        key = { item -> item.entity.id }
                    ) { match ->

                        MatchListItem(
                            gameEntity = gameEntity,
                            match = match,
                            onSingleMatchTap = onSingleMatchTap,
                            showGameIdentifier = false,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = DurationMillis.medium,
                                    easing = Ease)),
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
                lazyListState = rememberLazyListState(),
                listState = ListState.Default,
                matches = MatchObjectsDefaultPreview,
                searchString = "",
                themeColor = LocalGameColors.current.getColorByKey("ORANGE"),
                onNewMatchTap = {},
                onSingleMatchTap = {},
                modifier = Modifier
                    .padding(it)
                    .padding(vertical = 16.dp)
            )
        }
    }
}