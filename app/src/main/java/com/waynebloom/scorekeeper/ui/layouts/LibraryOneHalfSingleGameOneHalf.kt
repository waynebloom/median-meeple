package com.waynebloom.scorekeeper.ui.layouts

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.data.GameEntitiesDefaultPreview
import com.waynebloom.scorekeeper.data.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.ui.components.HelperBox
import com.waynebloom.scorekeeper.ui.components.HelperBoxType
import com.waynebloom.scorekeeper.ui.screens.LibraryScreen
import com.waynebloom.scorekeeper.ui.screens.SingleGameScreen
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme

@Composable
fun LibraryOneHalfSingleGameOneHalf(
    games: List<GameObject>,
    currentAd: NativeAd?,
    onAddGameTap: () -> Unit,
    onEditGameTap: () -> Unit,
    onAddMatchTap: () -> Unit,
    onMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedGame: GameObject? by rememberSaveable { mutableStateOf(null) }

    Row(modifier = modifier) {

        LibraryScreen(
            games = games.map { it.entity },
            currentAd = currentAd,
            onAddGameTap = onAddGameTap,
            onGameTap = { gameId ->
                selectedGame = games.find { it.entity.id == gameId }
            },
            modifier = Modifier.weight(1f),
        )

        Divider()

        if (selectedGame != null) {
            SingleGameScreen(
                gameObject = selectedGame!!,
                currentAd = currentAd,
                onEditGameTap = onEditGameTap,
                onNewMatchTap = onAddMatchTap,
                onSingleMatchTap = onMatchTap,
                modifier = Modifier.weight(1f),
            )
        } else {
            HelperBox(
                message = "Select a game",
                type = HelperBoxType.Info,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(device = Devices.TABLET)
@Composable
private fun Preview() {
    MedianMeepleTheme {

        LibraryOneHalfSingleGameOneHalf(
            games = GameObjectsDefaultPreview,
            currentAd = null,
            onAddGameTap = {},
            onEditGameTap = {},
            onAddMatchTap = {},
            onMatchTap = {},
        )
    }
}