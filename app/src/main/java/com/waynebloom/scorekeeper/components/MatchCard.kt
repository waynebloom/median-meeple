package com.waynebloom.scorekeeper.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.PreviewScoreData
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.getWinningScore
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchCard(
    game: GameEntity,
    match: MatchObject,
    onSingleMatchTap: (Long, Long) -> Unit,
    showGameIdentifier: Boolean = true
) {
    val gameInitial = game.name.first().uppercase()
    val gameColor = LocalGameColors.current.getColorByKey(game.color)

    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = { onSingleMatchTap(match.entity.gameOwnerId, match.entity.id) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            val winningScore = match.scores.getWinningScore(ScoringMode.getModeByOrdinal(game.scoringMode))

            if (showGameIdentifier) {
                GameIdentifier(
                    initial = gameInitial,
                    color = gameColor
                )
            }
            if (match.scores.isNotEmpty()) {
                VictorCard(
                    name = winningScore?.name ?: "",
                    score = winningScore?.scoreValue ?: 0,
                    color = gameColor,
                    modifier = Modifier.weight(1f, fill = false)
                )
            } else {
                EmptyPlayersCard()
            }
            PlayerCountCard(
                count = match.scores.size,
                color = gameColor
            )
        }
    }
}

@Composable
fun GameIdentifier(
    initial: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 16.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = initial,
            color = color,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyPlayersCard() {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, MaterialTheme.colors.error),
        modifier = Modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                imageVector = Icons.Rounded.Warning,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.error),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(end = 4.dp)
                    .size(20.dp)
            )
            Text(
                text = stringResource(id = R.string.text_empty_match_scores),
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}

@Composable
fun VictorCard(
    name: String,
    score: Long,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color),
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                painterResource(id = R.mipmap.ic_crown),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(end = 4.dp)
                    .size(20.dp)
            )
            Text(
                text = name,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
            )
            Text(
                text = score.toString(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun PlayerCountCard(
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, color),
        modifier = modifier
            .sizeIn(minHeight = 44.dp, minWidth = 44.dp)
            .fillMaxHeight()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                painterResource(id = if (count == 1) R.drawable.ic_person else R.drawable.ic_group),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
            )
            Text(
                text = count.toString(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun getWinningScore(
    scores: List<ScoreEntity>,
    scoringMode: ScoringMode
): ScoreEntity? {
    return if (scoringMode == ScoringMode.Descending) {
        scores.maxByOrNull { it.scoreValue ?: 0 }
    } else scores.minByOrNull { it.scoreValue ?: 0 }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoreCardPreview() {
    ScoreKeeperTheme() {
        MatchCard(
            game = GameEntity(name = "WWWW"),
            match = EMPTY_MATCH_OBJECT.apply {
                scores = PreviewScoreData
            },
            onSingleMatchTap = {_,_->}
        )
    }
}