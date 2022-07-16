package com.waynebloom.highscores.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.Score

@Composable
fun SingleGameScreen(
    game: Game,
    onNewScoreTap: (String) -> Unit,
    onSingleScoreTap: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        SingleGameHeader(
            name = game.name,
            image = game.image,
            onNewScoreTap = onNewScoreTap
        )
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(game.scores) { score ->
                ScoreCard(
                    score = score,
                    onSingleScoreTap = onSingleScoreTap
                )
            }
        }
    }
}

@Composable
fun SingleGameHeader(
    name: String,
    @DrawableRes image: Int,
    onNewScoreTap: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.background.copy(alpha = 1f),
                    shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0))
                )
        ) {
            Text(
                style = MaterialTheme.typography.h4,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                text = name,
                modifier = Modifier
                    .weight(0.75f)
                    .padding(all = 16.dp)
            )
            Button(
                onClick = { onNewScoreTap(name) },
                colors = ButtonDefaults.buttonColors(backgroundColor = colors.secondary),
                modifier = Modifier
                    .weight(0.25f)
                    .padding(vertical = 16.dp)
                    .padding(end = 16.dp)
            ) {
                Text(text = "NEW")
            }
        }
    }
}