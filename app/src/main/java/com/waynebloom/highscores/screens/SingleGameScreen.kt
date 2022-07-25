package com.waynebloom.highscores.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.components.ScoreCard
import com.waynebloom.highscores.components.ScreenHeader
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.data.Score

@Composable
fun SingleGameScreen(
    game: Game,
    scores: List<Score>,
    onEditGameTap: () -> Unit,
    onNewScoreTap: (String) -> Unit,
    onSingleScoreTap: (Score) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewScoreTap(game.id) },
                shape = MaterialTheme.shapes.small,
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Column(
            modifier = modifier
        ) {
            ScreenHeader(
                title = game.name,
                image = game.imageId,
                titleBarButton = {
                    Button(
                        onClick = { onEditGameTap() },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .padding(end = 16.dp)
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                }
            )
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scores) { score ->
                    ScoreCard(
                        score = score,
                        onSingleScoreTap = onSingleScoreTap
                    )
                }
            }
        }
    }
}