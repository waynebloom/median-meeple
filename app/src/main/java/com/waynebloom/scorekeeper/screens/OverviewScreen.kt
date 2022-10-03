package com.waynebloom.scorekeeper.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.GameCard
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.components.MatchCard
import com.waynebloom.scorekeeper.data.*

@Composable
fun OverviewScreen(
    games: List<GameObject>,
    matches: List<MatchObject>,
    currentAd: NativeAd?,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Long) -> Unit,
    onSingleMatchTap: (Long, Long) -> Unit,
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
            val emptyContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
            buttonClick = onAddNewGameTap
            buttonText = stringResource(id = R.string.button_add_new_game)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = emptyContentColor,
                        shape = MaterialTheme.shapes.small
                    )
                    .height(64.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    color = emptyContentColor,
                    text = stringResource(id = R.string.text_empty_games)
                )
            }
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
    onSingleMatchTap: (Long, Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        matches.forEachIndexed { index, match ->
            val parentGame = games.find { it.entity.id == match.entity.gameOwnerId }?.entity  TODO
                ?: throw NoSuchElementException(
                    stringResource(id = R.string.exc_no_game_with_id, match.entity.gameOwnerId)
                )
            MatchCard(
                match = match,
                gameInitial = parentGame.name.first().uppercase(),
                gameColor = LocalGameColors.current.getColorByKey(parentGame.color),
                onSingleMatchTap = onSingleMatchTap
            )
            if (index == 2) { AdCard(currentAd) }
        }
        if (matches.isEmpty()) {
            val emptyContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
            Column {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = emptyContentColor,
                            shape = MaterialTheme.shapes.small
                        )
                        .height(64.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        color = emptyContentColor,
                        text = stringResource(id = R.string.text_empty_matches)
                    )
                }
            }
        }
        if (matches.size < 3) {
            AdCard(currentAd)
        }
    }
}

/*@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameCardPreview() {
    HighScoresTheme {
        GameCard(
            name = "Carcassonne",
            onClick = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoreCardPreview() {
    HighScoresTheme {
        ScoreCard(
            score = PreviewScoreData[0],
            onSingleScoreTap = {}
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GameListPreview() {
    HighScoresTheme {
        GamesHead(PreviewGameData, {}, {})
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoresHeadPreview() {
    HighScoresTheme {
        ScoresHead(PreviewScoreData) {}
    }
}*/

