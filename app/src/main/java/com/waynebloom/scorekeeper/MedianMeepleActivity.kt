package com.waynebloom.scorekeeper

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.waynebloom.scorekeeper.screens.*
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.data.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.data.color.GameColors
import com.waynebloom.scorekeeper.data.color.LightThemeGameColors
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.viewmodel.MedianMeepleActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

val LocalGameColors: ProvidableCompositionLocal<GameColors> = compositionLocalOf { DarkThemeGameColors() }

class MedianMeepleActivity : ComponentActivity() {
    private lateinit var viewModel: MedianMeepleActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MedianMeepleActivityViewModel::class.java]
        MobileAds.initialize(this)
        setContent {
            App(viewModel = viewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.adService.destroyAd()
    }
}

@Composable
private fun App(viewModel: MedianMeepleActivityViewModel) {
    MedianMeepleTheme {
        val navController = rememberNavController()

        LaunchedEffect(true) {
            while (true) {
                viewModel.adService.loadNewAd()
                delay(AdService.NEW_AD_REQUEST_DELAY_MS)
                viewModel.adService.currentAd.value = null
                delay(AdService.BETWEEN_ADS_DELAY_MS)
            }
        }

        CompositionLocalProvider(
            LocalGameColors provides if (isSystemInDarkTheme()) DarkThemeGameColors() else LightThemeGameColors(),
        ) {
            Scaffold { innerPadding ->
                NavHost(
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun NavHost(
    navController: NavHostController,
    viewModel: MedianMeepleActivityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAd = viewModel.adService.currentAd.value

    NavHost(
        navController = navController,
        startDestination = TopLevelScreen.Overview.name,
        modifier = modifier
    ) {
        // region OverviewScreen

        composable(TopLevelScreen.Overview.name) {
            var allGames: List<GameObject> by remember { mutableStateOf(listOf()) }
            var allMatches: List<MatchObject> by remember { mutableStateOf(listOf()) }
            var isLoading: Boolean by remember { mutableStateOf(true) }

            LaunchedEffect("$route%getData") {
                viewModel.games
                    .combine(viewModel.matches) { games, matches ->
                        allGames = games
                        allMatches = matches
                    }
                    .onStart { isLoading = true }
                    .collectLatest { isLoading = false }
            }

            if (isLoading) {
                Loading()
            } else {
                OverviewScreen(
                    games = allGames,
                    matches = allMatches
                        .sortedByDescending { it.entity.timeModified }
                        .take(6),
                    currentAd = currentAd,
                    onSeeAllGamesTap = { navController.navigate(TopLevelScreen.Games.name) },
                    onAddNewGameTap = {
                        with(viewModel) {
                            executeDbOperation(
                                operation = {
                                    insertNewEmptyGame()
                                }
                            )
                            navController.navigate(TopLevelScreen.EditGame.name)
                        }
                    },
                    onSingleGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    },
                    onSingleMatchTap = { matchId ->
                        with(viewModel) {
                            updateMatchCacheById(id = matchId, matches = allMatches)
                            updateGameCacheById(
                                id = matchCache.dataObject.entity.gameId,
                                games = allGames
                            )
                        }
                        navController.navigate(TopLevelScreen.SingleMatch.name)
                    }
                )
            }
        }

        // endregion

        // region GamesScreen

        composable(TopLevelScreen.Games.name) {
            var isLoading by remember { mutableStateOf(true) }
            var allGames: List<GameObject> by remember { mutableStateOf(listOf()) }

            LaunchedEffect("$route%getGames") {
                viewModel.games.collectLatest {
                    allGames = it
                    isLoading = false
                }
            }

            if (isLoading) {
                Loading()
            } else {
                GamesScreen(
                    games = allGames.map { it.entity },
                    currentAd = currentAd,
                    onAddNewGameTap = {
                        with(viewModel) {
                            executeDbOperation(
                                operation = {
                                    insertNewEmptyGame()
                                }
                            )
                            navController.navigate(TopLevelScreen.EditGame.name)
                        }
                    },
                    onSingleGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    }
                )
            }
        }

        // endregion

        // region SingleGameScreen

        composable(route = TopLevelScreen.SingleGame.name) {
            SingleGameScreen(
                game = viewModel.gameCache.dataObject,
                currentAd = currentAd,
                onEditGameTap = { navController.navigate(TopLevelScreen.EditGame.name) },
                onNewMatchTap = {
                    with(viewModel) {
                        executeDbOperation {
                            insertNewEmptyMatch()
                        }
                    }
                    navController.navigate(TopLevelScreen.SingleMatch.name)
                },
                onSingleMatchTap = { matchId ->
                    viewModel.updateMatchCacheById(
                        id = matchId,
                        matches = viewModel.gameCache.dataObject.matches
                    )
                    navController.navigate(TopLevelScreen.SingleMatch.name)
                }
            )
        }

        // endregion

        // region EditGameScreen

        composable(route = TopLevelScreen.EditGame.name) {

            if (!viewModel.gameCache.needsUpdate) {
                EditGameScreen(
                    game = viewModel.gameCache.dataObject,
                    saveGame = { gameBundle, subscoreTitleBundles ->
                        with(viewModel) {
                            executeDbOperation {
                                commitGameBundle(gameBundle)
                                commitSubscoreTitleBundles(subscoreTitleBundles)
                            }
                        }
                        navController.popBackStack()
                        Toast.makeText(context, R.string.toast_game_updated, Toast.LENGTH_SHORT).show()
                    },
                    onDeleteTap = { gameId ->
                        navController.popBackStack(
                            route = TopLevelScreen.Overview.name,
                            inclusive = false
                        )
                        with(viewModel) {
                            executeDbOperation {
                                deleteGameById(gameId)
                            }
                        }
                        navController.navigate(TopLevelScreen.Games.name)
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region SingleMatchScreen

        composable(route = TopLevelScreen.SingleMatch.name) {
            if (!viewModel.matchCache.needsUpdate) {
                SingleMatchScreen(
                    game = viewModel.gameCache.dataObject,
                    match = viewModel.matchCache.dataObject,
                    onAddPlayerTap = {
                        with(viewModel) {
                            executeDbOperation {
                                insertNewEmptyPlayer()
                            }
                        }
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                    onDeleteMatchTap = { matchId ->
                        val popSuccess = navController.popBackStack(
                            route = TopLevelScreen.SingleGame.name,
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()
                        with(viewModel) {
                            executeDbOperation {
                                deleteMatchById(matchId)
                            }
                        }

                        navController.navigate(TopLevelScreen.SingleGame.name)
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT).show()
                    },
                    onPlayerTap = { playerId ->
                        viewModel.updatePlayerCacheById(
                            id = playerId,
                            players = viewModel.matchCache.dataObject.players
                        )
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                    onViewDetailedScoresTap = {
                        navController.navigate(TopLevelScreen.DetailedPlayerScores.name)
                    },
                    saveMatch = { updatedMatch ->
                        if (updatedMatch.databaseAction == DatabaseAction.UPDATE) {
                            with(viewModel) {
                                executeDbOperation {
                                    updateMatch(updatedMatch.entity)
                                }
                            }
                        }
                        navController.popBackStack()
                        Toast.makeText(context, R.string.toast_match_updated, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region DetailedPlayerScoresScreen

        composable(route = TopLevelScreen.DetailedPlayerScores.name) {
            if (viewModel.allCachesUpToDate()) {
                DetailedPlayerScoresScreen(
                    players = viewModel.matchCache.dataObject.players,
                    subscoreTitles = viewModel.gameCache.dataObject.subscoreTitles,
                    themeColor = LocalGameColors.current.getColorByKey(viewModel.gameCache.dataObject.entity.color),
                    onExistingPlayerTap = { playerId ->
                        viewModel.updatePlayerCacheById(
                            id = playerId,
                            players = viewModel.matchCache.dataObject.players
                        )
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region EditPlayerScoreScreen

        composable(route = TopLevelScreen.EditPlayerScore.name) {
            if (!viewModel.playerCache.needsUpdate) {
                EditPlayerScoreScreen(
                    initialPlayer = viewModel.playerCache.dataObject,
                    matchObject = viewModel.matchCache.dataObject,
                    subscoreTitles = viewModel.gameCache.dataObject.subscoreTitles,
                    isGameManualRanked = viewModel.gameCache.dataObject.getScoringMode() == ScoringMode.Manual,
                    themeColor = LocalGameColors.current.getColorByKey(viewModel.gameCache.dataObject.entity.color),
                    onSaveTap = { playerBundle, subscoreBundles ->
                        with(viewModel) {
                            executeDbOperation {
                                commitPlayerBundle(playerBundle)
                                commitSubscoreBundles(subscoreBundles)
                            }
                        }
                        navController.popBackStack()
                    },
                    onDeleteTap = { playerId ->
                        with(viewModel) {
                            executeDbOperation {
                                deletePlayerById(playerId)
                            }
                        }
                        navController.popBackStack()
                    }
                )
            } else {
                Loading()
            }
        }

        // endregion
    }
}