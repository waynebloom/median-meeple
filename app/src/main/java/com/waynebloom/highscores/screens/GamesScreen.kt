package com.waynebloom.highscores.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.waynebloom.highscores.R
import com.waynebloom.highscores.components.GameCard
import com.waynebloom.highscores.components.HeadedSection
import com.waynebloom.highscores.data.GameEntity
import com.waynebloom.highscores.data.GameColor

@Composable
fun GamesScreen(
    games: List<GameEntity>,
    onAddNewGameTap: () -> Unit,
    onSingleGameTap: (GameEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = MaterialTheme.shapes.small,
                backgroundColor = MaterialTheme.colors.primary,
                onClick = { onAddNewGameTap() }
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
            }
        }
    ) {
        Column(
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            HeadedSection(title = R.string.header_games) {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 64.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(games) { game ->
                        GameCard(
                            name = game.name,
                            color = GameColor.valueOf(game.color).color,
                            onClick = { onSingleGameTap(game) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}