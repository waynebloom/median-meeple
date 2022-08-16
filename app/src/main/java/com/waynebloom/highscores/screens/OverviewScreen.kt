package com.waynebloom.highscores.screens

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
import com.waynebloom.highscores.components.HeadedSection
import com.waynebloom.highscores.components.MatchCard
import com.waynebloom.highscores.data.*

@Composable
fun OverviewScreen(
    games: List<GameObject>,
    matches: List<MatchEntity>,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (GameEntity) -> Unit,
    onSingleMatchTap: (MatchEntity) -> Unit,
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
                games = games,
                onSeeAllGamesTap = onSeeAllGamesTap,
                onAddNewGameTap = onAddNewGameTap,
                onSingleGameTap = onSingleGameTap
            )
        }
        HeadedSection(title = R.string.header_recent_matches) {
            MatchesHead(
                matches = matches,
                games = games,
                onSingleMatchTap = onSingleMatchTap
            )
        }
    }
}

@Composable
fun GamesHead(
    games: List<GameEntity>,
    onSeeAllGamesTap: () -> Unit,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (GameEntity) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val gameRows: MutableList<List<GameEntity>> = mutableListOf()
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
    games: List<GameEntity>,
    onSingleGameTap: (GameEntity) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        games.forEach { game ->
            GameCard(
                name = game.name,
                color = GameColor.valueOf(game.color).color,
                onClick = { onSingleGameTap(game) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MatchesHead(
    matches: List<MatchEntity>,
    games: List<GameEntity>,
    onSingleMatchTap: (MatchEntity) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        matches.forEach { match ->
            val parentGame = games.find { it.id == match.gameOwnerId }
                ?: EMPTY_GAME
//            val parentGame = games.find { it.id == match.gameOwnerId }
//                ?: throw NoSuchElementException(stringResource(id = R.string.exc_no_game_with_id, match.gameOwnerId))
            MatchCard(
                match = match,
                gameInitial = parentGame.name.first().uppercase(),
                gameColor = GameColor.valueOf(parentGame.color).color,
                onSingleMatchTap = onSingleMatchTap
            )
        }
        if (matches.isEmpty()) {
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
                    text = stringResource(id = R.string.text_empty_matches)
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

