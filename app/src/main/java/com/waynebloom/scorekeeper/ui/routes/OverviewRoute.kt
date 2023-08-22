package com.waynebloom.scorekeeper.ui.routes

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.ui.layouts.LibraryOneHalfSingleGameOneHalf
import com.waynebloom.scorekeeper.ui.screens.OverviewScreen

@Composable
fun OverviewRoute(
    windowSizeClass: WindowSizeClass,
    games: List<GameObject>,
    allMatches: List<MatchObject>,
    currentAd: NativeAd?,
    onAddGameTap: () -> Unit,
    onAddMatchTap: () -> Unit,
    onEditGameTap: () -> Unit,
    onGameTap: (Long) -> Unit,
    onGoToLibraryTap: () -> Unit,
    onMatchTap: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    when(windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Row {

                OverviewScreen(
                    games = games,
                    allMatches = allMatches,
                    currentAd = currentAd,
                    onAddNewGameTap = onAddGameTap,
                    onGameTap = onGameTap,
                    onGoToLibraryTap = onGoToLibraryTap,
                    onMatchTap = onMatchTap,
                    modifier = modifier,
                )
            }
        }

        WindowWidthSizeClass.Medium -> {
            LibraryOneHalfSingleGameOneHalf(
                games = games,
                currentAd = currentAd,
                onAddGameTap = onAddGameTap,
                onAddMatchTap = onAddMatchTap,
                onEditGameTap = onEditGameTap,
                onMatchTap = onMatchTap,
            )
        }
        
        WindowWidthSizeClass.Expanded -> {}
    }
}