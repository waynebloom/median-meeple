package com.waynebloom.highscores.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.PreviewScoreData
import com.waynebloom.highscores.R
import com.waynebloom.highscores.data.EMPTY_MATCH
import com.waynebloom.highscores.data.MatchEntity
import com.waynebloom.highscores.ui.theme.HighScoresTheme
import com.waynebloom.highscores.ui.theme.orange100

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchCard(
    match: MatchEntity,
    gameInitial: String,
    gameColor: Color,
    onSingleMatchTap: (MatchEntity) -> Unit,
    showGameIdentifier: Boolean = true
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = gameColor.copy(alpha = 0.3f),
        onClick = { onSingleMatchTap(match) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (showGameIdentifier) {
                GameIdentifier(
                    initial = gameInitial,
                    color = gameColor
                )
            }
            VictorCard(
                name = match.scores.maxByOrNull { it.scoreValue ?: 0 }?.name ?: "",
                score = match.scores.maxByOrNull { it.scoreValue ?: 0 }?.scoreValue ?: 0,
                color = gameColor,
                modifier = Modifier.weight(1f, fill = false)
            )
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

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ScoreCardPreview() {
    HighScoresTheme() {
        MatchCard(
            match = EMPTY_MATCH.apply {
                scores = PreviewScoreData
            },
            gameInitial = "W",
            gameColor = orange100,
            onSingleMatchTap = {}
        )
    }
}