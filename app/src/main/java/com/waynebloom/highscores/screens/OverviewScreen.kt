package com.waynebloom.highscores.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.GameCard
import com.waynebloom.highscores.components.ScoreCard
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Score
import java.util.*

@Composable
fun OverviewScreen(
    games: List<Game>,
    scores: List<Score>,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Game) -> Unit,
    onSingleScoreTap: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        MainScreenSection(title = R.string.header_games) {
            GamesHead(
                games = games,
                onSeeAllGamesTap = onSeeAllGamesTap,
                onAddNewGameTap = onAddNewGameTap,
                onSingleGameTap = onSingleGameTap
            )
        }
        MainScreenSection(title = R.string.header_scores) {
            ScoresHead(
                scores = scores,
                onSingleScoreTap = onSingleScoreTap
            )
        }
    }
}

@Composable
fun MainScreenSection(
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = stringResource(title).uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        content()
    }
}

@Composable
fun GamesHead(
    games: List<Game>,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (Game) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        val gameRows: MutableList<List<Game>> = mutableListOf()
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
    games: List<Game>,
    onSingleGameTap: (Game) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { game ->
            GameCard(
                name = game.name,
                image = game.imageId,
                onClick = { onSingleGameTap(game) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ScoresHead(
    scores: List<Score>,
    onSingleScoreTap: (Score) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        scores.forEach { score ->
            ScoreCard(
                score = score,
                onSingleScoreTap = onSingleScoreTap
            )
        }
        if (scores.isEmpty()) {
            val emptyContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
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
                    text = stringResource(id = R.string.text_empty_scores)
                )
            }
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

