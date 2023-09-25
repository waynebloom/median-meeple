package com.waynebloom.scorekeeper.ui.screens

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.ui.components.AdCard
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.components.MatchListItem
import com.waynebloom.scorekeeper.ui.components.MedianMeepleFab
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.ext.toAdSeparatedSubLists
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun MatchesForSingleGameScreen(
    currentAd: NativeAd?,
    gameEntity: GameDataModel,
    lazyListState: LazyListState,
    listDisplayState: ListDisplayState,
    matches: List<MatchDataRelationModel>,
    searchString: String,
    themeColor: Color,
    onNewMatchTap: () -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        floatingActionButton = { MedianMeepleFab(
            backgroundColor = themeColor,
            onClick = onNewMatchTap,
        ) },
        modifier = modifier,
    ) { contentPadding ->

        Column(modifier = Modifier
            .padding(contentPadding)
            .padding(horizontal = Spacing.screenEdge)
        ) {

            AnimatedContent(
                targetState = listDisplayState,
                transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay }
            ) {

                if (it != ListDisplayState.ShowAll) {

                    val type = if (it == ListDisplayState.ShowFiltered) {
                        HelperBoxType.Info
                    } else HelperBoxType.Missing

                    val text = when (it) {
                        ListDisplayState.Empty -> stringResource(R.string.text_empty_matches)
                        ListDisplayState.EmptyFiltered -> stringResource(
                            id = R.string.text_empty_match_search_results,
                            searchString
                        )
                        ListDisplayState.ShowFiltered -> stringResource(
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

                val adSeparatedSubLists = matches.toAdSeparatedSubLists()

                adSeparatedSubLists.forEachIndexed { index, subList ->

                    items(
                        items = subList,
                        key = { item -> item.entity.id }
                    ) { match ->

                        MatchListItem(
                            gameEntity = gameEntity,
                            match = match,
                            onSingleMatchTap = onSingleMatchTap,
                            showGameIdentifier = false,
                            modifier = Modifier.animateItemPlacement(
                                animationSpec = tween(
                                    durationMillis = DurationMs.medium,
                                    easing = Ease)),
                        )
                    }

                    item {
                        if (index == adSeparatedSubLists.lastIndex) {
                            AdCard(
                                ad = currentAd,
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
                gameEntity = GameDataModel(
                    name = "Wingspan",
                    color = "ORANGE"
                ),
                lazyListState = rememberLazyListState(),
                listDisplayState = ListDisplayState.ShowAll,
                matches = MatchObjectsDefaultPreview,
                searchString = "",
                themeColor = LocalCustomThemeColors.current.getColorByKey("ORANGE"),
                onNewMatchTap = {},
                onSingleMatchTap = {},
                modifier = Modifier
                    .padding(it)
                    .padding(vertical = 16.dp)
            )
        }
    }
}
