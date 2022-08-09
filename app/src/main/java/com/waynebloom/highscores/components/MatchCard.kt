package com.waynebloom.highscores.components

import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.widget.GridLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.PreviewScoreData
import com.waynebloom.highscores.R
import com.waynebloom.highscores.data.EMPTY_MATCH
import com.waynebloom.highscores.data.Match
import com.waynebloom.highscores.data.Score
import com.waynebloom.highscores.ui.theme.HighScoresTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchCard(
    match: Match,
    onSingleMatchTap: (Match) -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        onClick = { onSingleMatchTap(match) }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            VictorCard(
                name = match.scores.maxByOrNull { it.scoreValue ?: 0 }?.name ?: "",
                score = match.scores.maxByOrNull { it.scoreValue ?: 0 }?.scoreValue ?: 0
            )
            PlayerCountCard(
                count = match.scores.size,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun VictorCard(
    name: String,
    score: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        elevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                painterResource(id = R.mipmap.ic_crown),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(end = 4.dp)
                    .size(20.dp)
            )
            Text(
                text = name,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h6
            )
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painterResource(id = R.drawable.ic_star),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
            )
            Text(
                text = score.toString(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h6
            )
        }
    }
}

@Composable
fun PlayerCountCard(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        elevation = 1.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(all = 8.dp)
        ) {
            Image(
                painterResource(id = if (count == 1) R.drawable.ic_person else R.drawable.ic_group),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
            )
            Text(
                text = count.toString(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h6
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
            onSingleMatchTap = {}
        )
    }
}