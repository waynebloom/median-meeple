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
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.HeadedSection
import com.waynebloom.highscores.components.MatchCard
import com.waynebloom.highscores.components.ScreenHeader
import com.waynebloom.highscores.data.GameEntity
import com.waynebloom.highscores.data.GameColor
import com.waynebloom.highscores.data.GameObject
import com.waynebloom.highscores.data.MatchEntity

@Composable
fun SingleGameScreen(
    game: GameObject,
    onEditGameTap: () -> Unit,
    onNewMatchTap: (Long) -> Unit,
    onSingleMatchTap: (Long, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val gameColorResource = GameColor.valueOf(game.entity.color).color

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNewMatchTap(game.entity.id) },
                shape = MaterialTheme.shapes.small,
                backgroundColor = gameColorResource,
                contentColor = MaterialTheme.colors.onPrimary
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Column(modifier = modifier) {
            ScreenHeader(
                title = game.entity.name,
                color = GameColor.valueOf(game.entity.color).color,
                headerButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = gameColorResource,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        onClick = { onEditGameTap() },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                }
            )
            HeadedSection(
                title = R.string.header_matches,
                topPadding = 40,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(game.matches) { match ->
                        MatchCard(
                            match = match,
                            gameInitial = game.entity.name.first().uppercase(),
                            gameColor = gameColorResource,
                            onSingleMatchTap = onSingleMatchTap,
                            showGameIdentifier = false
                        )
                    }
                }
            }
        }
    }
}