package com.waynebloom.highscores.screens

import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.PreviewGameData
import com.waynebloom.highscores.PreviewScoreData
import com.waynebloom.highscores.R
import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.Score
import com.waynebloom.highscores.ui.theme.HighScoresTheme
import java.util.*

@Composable
fun OverviewScreen(
    games: List<Game>,
    onSeeAllGamesTap: () -> Unit,
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
                onSingleGameTap = onSingleGameTap
            )
        }
        MainScreenSection(title = R.string.header_scores) {
            ScoresHead(
                scores = PreviewScoreData,
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

// region Games
@Composable
fun GamesHead(
    games: List<Game>,
    onSeeAllGamesTap: () -> Unit,
    onSingleGameTap: (Game) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        if (games.size >= 2) {
            GamesHeadRow(
                games = games.subList(0, 2),
                onSingleGameTap = onSingleGameTap
            )
        }
        if (games.size >= 4) {
            GamesHeadRow(
                games = games.subList(2, 4),
                onSingleGameTap = onSingleGameTap
            )
        }
        if (games.size >= 6) {
            GamesHeadRow(
                games = games.subList(4, 6),
                onSingleGameTap = onSingleGameTap
            )
        }
        if (games.size > 6) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.button_games)
                )
            }
        }
    }
}

@Composable
fun GamesHeadRow(
    games: List<Game>,
    onSingleGameTap: (Game) -> Unit,
) {
    if (games.size > 1) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GameCard(
                name = games[0].name,
                onClick = { onSingleGameTap(games[0]) },
                modifier = Modifier.weight(1f)
            )
            GameCard(
                name = games[1].name,
                onClick = { onSingleGameTap(games[1]) },
                modifier = Modifier.weight(1f)
            )
        }
    } else {
        // TODO
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GameCard(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes image: Int = R.drawable.default_img
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = onClick,
        modifier = modifier.height(64.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
// endregion Games

// region Scores
@Composable
fun ScoresHead(
    scores: List<Score>,
    onSingleScoreTap: (Score) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        scores.take(5).forEach { score ->
            ScoreCard(
                score = score,
                onSingleScoreTap = onSingleScoreTap
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScoreCard(
    score: Score,
    onSingleScoreTap: (Score) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = { onSingleScoreTap(score) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row() {
                Text(
                    text = score.name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "<game>",
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
            Row() {
                Text(
                    text = score.score.toString(),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "<rank>",
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
// endregion

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
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
}

