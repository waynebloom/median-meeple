package com.waynebloom.scorekeeper.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.LocalGameColors
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.AdCard
import com.waynebloom.scorekeeper.components.GameCard
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.components.showAdAtIndex
import com.waynebloom.scorekeeper.data.GameEntity

@Composable
fun GamesScreen(
    games: List<GameEntity>,
    currentAd: NativeAd?,
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
                    contentPadding = PaddingValues(bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(games) { index, game ->
                        GameCard(
                            name = game.name,
                            color = LocalGameColors.current.getColorByKey(game.color),
                            onClick = { onSingleGameTap(game) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (showAdAtIndex(index, games.size)) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AdCard(currentAd)
                        }
                    }
                }
            }
        }
    }
}