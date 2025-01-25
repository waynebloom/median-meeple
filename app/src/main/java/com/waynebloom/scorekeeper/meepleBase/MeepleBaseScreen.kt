package com.waynebloom.scorekeeper.meepleBase

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.NewGameCard
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun MeepleBaseScreen(
    uiState: MeepleBaseUiState,
    modifier: Modifier = Modifier,
) {

    when(uiState) {
        is MeepleBaseUiState.Loading -> {
            Loading()
        }
        is MeepleBaseUiState.Content -> {
            MeepleBaseScreen(
                gameCards = uiState.gameCards,
                modifier = modifier,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MeepleBaseScreen(
    gameCards: List<LibraryGameCard>,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            // TODO: top bar
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier,
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(160.dp),
                verticalItemSpacing = Dimensions.Spacing.sectionContent,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
                contentPadding = PaddingValues(Dimensions.Spacing.screenEdge),
            ) {
                gameCards.forEachIndexed { i, card ->
                    val showAd = (gameCards.size < 5 && i == gameCards.lastIndex)
                            || ((i - 3) % 13 == 0 && i != gameCards.lastIndex)

                    item(key = card.id) {
                        NewGameCard(
                            name = card.name,
                            color = card.color
                                .copy(alpha = 0.2f)
                                .compositeOver(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
                            highScore = card.highScore,
                            noOfMatches = card.noOfMatches,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    // TODO: onClick for game cards
                                }
                                .animateItemPlacement()
                        )
                    }
                }

                item {
                    Spacer(
                        Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .consumeWindowInsets(WindowInsets.navigationBars)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Normal() {
    MedianMeepleTheme {
        // TODO: previews
    }
}
