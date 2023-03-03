package com.waynebloom.scorekeeper.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.*
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject

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
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        HeadedSection(title = R.string.header_games) {
            GamesHead(
                games = games.take(6),
                onSeeAllGamesTap = onSeeAllGamesTap,
                onAddNewGameTap = onAddNewGameTap,
                onSingleGameTap = onSingleGameTap
            )
        }
        HeadedSection(title = R.string.header_recent_matches) {
            MatchesHead(
                matches = matches,
                games = games,
                currentAd = currentAd,
                onSingleMatchTap = onSingleMatchTap
            )
        }
    }
}

@Composable
fun GamesHead(
    games: List<GameObject>,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val gameRows: MutableList<List<GameObject>> = mutableListOf()
        var buttonClick = onSeeAllGamesTap
        var buttonText = stringResource(id = R.string.button_games)

        for (i in 1 until games.size step 2) {
            gameRows.add(games.slice(i-1..i))
        }
        if (games.size % 2 == 1) {
            gameRows.add(listOf(games.last()))
        }
        gameRows.forEach { gameRow ->
            GamesHeadRow(
                games = gameRow,
                onSingleGameTap = onSingleGameTap
            )
        }
        if (games.isEmpty()) {
            buttonClick = onAddNewGameTap
            buttonText = stringResource(id = R.string.button_add_new_game)
            DullColoredTextCard(text = stringResource(R.string.text_empty_games))
        }
        Button(
            onClick = { buttonClick() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = buttonText
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { game ->
            GameCard(
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        matches.forEachIndexed { index, match ->
            val parentGame = games.find { it.entity.id == match.entity.gameId }?.entity
                ?: throw NoSuchElementException(
                    stringResource(id = R.string.exc_no_game_with_id, match.entity.gameId)
                )
            MatchCard(
                game = parentGame,
                match = match,
                onSingleMatchTap = onSingleMatchTap
            )
            if (index == 2) { AdCard(currentAd) }
        }
        if (matches.isEmpty()) {
            DullColoredTextCard(text = stringResource(R.string.text_empty_matches))
        }
        if (matches.size < 3) {
            AdCard(currentAd)
        }
    }
}

