package com.waynebloom.scorekeeper.overview

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun OverviewScreen(
        uiState: OverviewUiState,
        onAddGameClick: () -> Unit,
        onGoToLibraryClick: () -> Unit,
        onGameClick: (Long) -> Unit,
        onMatchClick: (Long) -> Unit,
) {

    OverviewScreen(
        ad = uiState.ad,
        games = uiState.displayedGames,
        loading = uiState.loading,
        matches = uiState.displayedMatches,
        onAddGameTap = onAddGameClick,
        onGoToLibraryTap = onGoToLibraryClick,
        onGameTap = onGameClick,
        onMatchTap = onMatchClick
    )
}

@Composable
fun OverviewScreen(
    ad: NativeAd?,
    games: List<GameDataRelationModel>,
    loading: Boolean,
    matches: List<MatchDataRelationModel>,
    onGoToLibraryTap: () -> Unit,
    onAddGameTap: () -> Unit,
    onGameTap: (Long) -> Unit,
    onMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {

    Scaffold(
        topBar = {
            OverviewTopBar(title = stringResource(id = R.string.top_bar_header_overview))
        }
    ) { contentPadding ->

        if (loading) {
            Loading()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = Spacing.screenEdge,
                    vertical = Spacing.sectionContent),
                verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
                modifier = modifier.padding(contentPadding)
            ) {

                item {

                    GamesSection(
                        ad = ad,
                        games = games,
                        onAddNewGameTap = onAddGameTap,
                        onSeeAllGamesTap = onGoToLibraryTap,
                        onSingleGameTap = onGameTap,
                    )
                }

                item {

                    MatchesSection(
                        matches = matches,
                        games = games,
                        onSingleMatchTap = onMatchTap
                    )
                }
            }
        }
    }

}

@Composable
private fun GamesSection(
    ad: NativeAd?,
    games: List<GameDataRelationModel>,
    onAddNewGameTap: () -> Unit,
    onSeeAllGamesTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        GamesHeader(
            games = games,
            onAddNewGameTap = onAddNewGameTap,
            onSeeAllGamesTap = onSeeAllGamesTap
        )

        if (games.isEmpty()) {
            HelperBox(
                message = stringResource(R.string.text_empty_games),
                type = HelperBoxType.Missing
            )
        } else TopGames(
            games = games,
            onSingleGameTap = onSingleGameTap
        )

        AdCard(ad = ad)
    }
}

@Composable
private fun GamesHeader(
    games: List<GameDataRelationModel>,
    onAddNewGameTap: () -> Unit,
    onSeeAllGamesTap: () -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = stringResource(id = R.string.header_games),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        if (games.isNotEmpty()) {
            IconButton(
                painter = painterResource(id = R.drawable.ic_list),
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                endText = "Library",
                onClick = onSeeAllGamesTap
            )
        } else {
            IconButton(
                imageVector = Icons.Rounded.Add,
                foregroundColor = MaterialTheme.colors.primary,
                onClick = onAddNewGameTap,
            )
        }
    }
}

@Composable
private fun MatchesSection(
    matches: List<MatchDataRelationModel>,
    games: List<GameDataRelationModel>,
    onSingleMatchTap: (Long) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_recent_matches),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        if (matches.isNotEmpty()) {
            RecentMatches(
                matches = matches,
                games = games,
                onSingleMatchTap = onSingleMatchTap)
        } else {
            HelperBox(
                message = stringResource(R.string.text_empty_matches),
                type = HelperBoxType.Missing)
        }
    }
}

@Composable
fun OverviewTopBar(title: String) {
    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .defaultMinSize(minHeight = Size.topBarHeight)
                .fillMaxWidth()
        ) {

            Text(
                text = title,
                color = MaterialTheme.colors.primary,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        Divider()
    }
}

@Composable
fun TopGames(games: List<GameDataRelationModel>, onSingleGameTap: (Long) -> Unit) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        val gameRows: MutableList<List<GameDataRelationModel>> = mutableListOf()

        for (i in 1 until games.size step 2)
            gameRows.add(games.slice(i-1..i))

        if (games.size % 2 == 1)
            gameRows.add(listOf(games.last()))

        gameRows.forEach { gameRow ->
            GamesHeadRow(
                games = gameRow,
                onSingleGameTap = onSingleGameTap
            )
        }
    }
}

@Composable
fun GamesHeadRow(
    games: List<GameDataRelationModel>,
    onSingleGameTap: (Long) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { game ->
            GameListItem(
                name = game.entity.name,
                color = LocalCustomThemeColors.current.getColorByKey(game.entity.color),
                onClick = { onSingleGameTap(game.entity.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun RecentMatches(
    matches: List<MatchDataRelationModel>,
    games: List<GameDataRelationModel>,
    onSingleMatchTap: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        matches.forEach { match ->
            val parentGame = games.find { it.entity.id == match.entity.gameId }?.entity
                ?: throw NoSuchElementException(
                    stringResource(id = R.string.exc_no_game_with_id, match.entity.gameId)
                )

            MatchListItem(
                gameEntity = parentGame,
                match = match,
                onSingleMatchTap = onSingleMatchTap
            )
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OverviewScreenPreview() {
    MedianMeepleTheme {
        Scaffold {
            
            OverviewScreen(
                uiState = OverviewSampleData.UiState,
                onAddGameClick = {},
                onGoToLibraryClick = {},
                onGameClick = {},
                onMatchClick = {}
            )
        }
    }
}
