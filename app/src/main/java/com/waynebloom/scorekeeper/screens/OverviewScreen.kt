package com.waynebloom.scorekeeper.screens

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
import com.waynebloom.scorekeeper.data.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.data.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun OverviewScreen(
    games: List<GameObject>,
    matches: List<MatchObject>,
    currentAd: NativeAd?,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
    onSingleMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        topBar = {
            OverviewTopBar(title = stringResource(id = R.string.top_bar_header_overview))
        }
    ) { contentPadding ->

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = Spacing.screenEdge,
                vertical = Spacing.sectionContent),
            verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
            modifier = modifier.padding(contentPadding)
        ) {

            item {

                GamesSection(
                    games = games,
                    onAddNewGameTap = onAddNewGameTap,
                    onSeeAllGamesTap = onSeeAllGamesTap,
                    onSingleGameTap = onSingleGameTap,
                )
            }

            item {

                MatchesSection(
                    matches = matches,
                    games = games,
                    currentAd = currentAd,
                    onSingleMatchTap = onSingleMatchTap
                )
            }
        }
    }

}

@Composable
private fun GamesSection(
    games: List<GameObject>,
    onAddNewGameTap: () -> Unit,
    onSeeAllGamesTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        GamesHeader(
            games = games,
            onAddNewGameTap = onAddNewGameTap,
            onSeeAllGamesTap = onSeeAllGamesTap)

        if (games.isEmpty()) {
            HelperBox(
                message = stringResource(R.string.text_empty_games),
                type = HelperBoxType.Missing)
        } else TopGames(games = games.take(6), onSingleGameTap = onSingleGameTap)
    }
}

@Composable
private fun GamesHeader(
    games: List<GameObject>,
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
            CustomIconButton(
                painter = painterResource(id = R.drawable.ic_list),
                backgroundColor = Color.Transparent,
                foregroundColor = MaterialTheme.colors.primary,
                endText = "Library",
                onTap = onSeeAllGamesTap
            )
        } else {
            CustomIconButton(
                imageVector = Icons.Rounded.Add,
                foregroundColor = MaterialTheme.colors.primary,
                onTap = onAddNewGameTap,
            )
        }
    }
}

@Composable
private fun MatchesSection(
    matches: List<MatchObject>,
    games: List<GameObject>,
    currentAd: NativeAd?,
    onSingleMatchTap: (Long) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_recent_matches),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        if (matches.isNotEmpty()) {
            MatchesHead(
                matches = matches,
                games = games,
                currentAd = currentAd,
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
fun TopGames(games: List<GameObject>, onSingleGameTap: (Long) -> Unit) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        val gameRows: MutableList<List<GameObject>> = mutableListOf()

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
    games: List<GameObject>,
    onSingleGameTap: (Long) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { game ->
            GameListItem(
                name = game.entity.name,
                color = LocalGameColors.current.getColorByKey(game.entity.color),
                onClick = { onSingleGameTap(game.entity.id) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MatchesHead(
    matches: List<MatchObject>,
    games: List<GameObject>,
    currentAd: NativeAd?,
    onSingleMatchTap: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        matches.forEachIndexed { index, match ->
            val parentGame = games.find { it.entity.id == match.entity.gameId }?.entity
                ?: throw NoSuchElementException(
                    stringResource(id = R.string.exc_no_game_with_id, match.entity.gameId)
                )

            MatchListItem(
                gameEntity = parentGame,
                match = match,
                onSingleMatchTap = onSingleMatchTap
            )

            if (index == 2) { AdCard(currentAd) }
        }

        if (matches.size < 3)
            AdCard(currentAd)
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun OverviewScreenPreview() {
    MedianMeepleTheme {
        Scaffold {
            OverviewScreen(
                games = GameObjectsDefaultPreview,
                matches = MatchObjectsDefaultPreview,
                currentAd = null,
                onSeeAllGamesTap = {},
                onAddNewGameTap = {},
                onSingleGameTap = {},
                onSingleMatchTap = {}
            )
        }
    }
}

