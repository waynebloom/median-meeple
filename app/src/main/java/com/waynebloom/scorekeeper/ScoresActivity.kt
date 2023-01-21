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
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.data.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.data.color.GameColors
import com.waynebloom.scorekeeper.data.color.LightThemeGameColors
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.viewmodel.GamesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

val LocalGameColors: ProvidableCompositionLocal<GameColors> = compositionLocalOf { DarkThemeGameColors() }

class ScoresActivity : ComponentActivity() {
    private lateinit var gamesViewModel: GamesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gamesViewModel = ViewModelProvider(this)[GamesViewModel::class.java]
        MobileAds.initialize(this)
        setContent {
            ScorekeeperApp(viewModel = gamesViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gamesViewModel.adService.destroyAd()
    }
}

@Composable
fun ScorekeeperApp(viewModel: GamesViewModel) {
    ScoreKeeperTheme {
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
                ScoresNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun ScoresNavHost(
    navController: NavHostController,
    viewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAd = viewModel.adService.currentAd.value

    NavHost(
        navController = navController,
        startDestination = ScorekeeperScreen.Overview.name,
        modifier = modifier
    ) {
        // region OverviewScreen

        composable(ScorekeeperScreen.Overview.name) {
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
                    onSeeAllGamesTap = { navController.navigate(ScorekeeperScreen.Games.name) },
                    onAddNewGameTap = { navController.navigate(ScorekeeperScreen.EditGame.name) },
                    onSingleGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(ScorekeeperScreen.SingleGame.name)
                    },
                    onSingleMatchTap = { matchId ->
                        with(viewModel) {
                            updateMatchCacheById(id = matchId, matches = allMatches)
                            updateGameCacheById(
                                id = matchCache.dataObject.entity.gameId,
                                games = allGames
                            )
                        }
                        navController.navigate(ScorekeeperScreen.SingleMatch.name)
                    }
                )
            }
        }

        // endregion

        // region GamesScreen

        composable(ScorekeeperScreen.Games.name) {
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
                            navController.navigate(ScorekeeperScreen.EditGame.name)
                        }
                    },
                    onSingleGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(ScorekeeperScreen.SingleGame.name)
                    }
                )
            }
        }

        // endregion

        // region SingleGameScreen

        composable(route = ScorekeeperScreen.SingleGame.name) {
            SingleGameScreen(
                game = viewModel.gameCache.dataObject,
                currentAd = currentAd,
                onEditGameTap = { navController.navigate(ScorekeeperScreen.EditGame.name) },
                onNewMatchTap = {
                    with(viewModel) {
                        executeDbOperation {
                            insertNewEmptyMatch()
                        }
                    }
                    navController.navigate(ScorekeeperScreen.SingleMatch.name)
                },
                onSingleMatchTap = { matchId ->
                    viewModel.updateMatchCacheById(
                        id = matchId,
                        matches = viewModel.gameCache.dataObject.matches
                    )
                    navController.navigate(ScorekeeperScreen.SingleMatch.name)
                }
            )
        }

        // endregion

        // region EditGameScreen

        composable(route = ScorekeeperScreen.EditGame.name) {

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
                            route = ScorekeeperScreen.Overview.name,
                            inclusive = false
                        )
                        with(viewModel) {
                            executeDbOperation {
                                deleteGameById(gameId)
                            }
                        }
                        navController.navigate(ScorekeeperScreen.Games.name)
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region SingleMatchScreen

        composable(route = ScorekeeperScreen.SingleMatch.name) {
            if (!viewModel.matchCache.needsUpdate) {
                SingleMatchScreen(
                    game = viewModel.gameCache.dataObject.entity,
                    match = viewModel.matchCache.dataObject,
                    onAddPlayerTap = {
                        with(viewModel) {
                            executeDbOperation {
                                insertNewEmptyPlayer()
                            }
                        }
                        navController.navigate(ScorekeeperScreen.EditPlayerScore.name)
                    },
                    onDeleteMatchTap = { matchId ->
                        val popSuccess = navController.popBackStack(
                            route = ScorekeeperScreen.SingleGame.name,
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()
                        with(viewModel) {
                            executeDbOperation {
                                deleteMatchById(matchId)
                            }
                        }

                        navController.navigate(ScorekeeperScreen.SingleGame.name)
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT).show()
                    },
                    onPlayerTap = { playerId ->
                        viewModel.updatePlayerCacheById(
                            id = playerId,
                            players = viewModel.matchCache.dataObject.players
                        )
                        navController.navigate(ScorekeeperScreen.EditPlayerScore.name)
                    },
                    onViewDetailedScoresTap = {
                        navController.navigate(ScorekeeperScreen.DetailedPlayerScores.name)
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
                    },
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region DetailedPlayerScoresScreen

        composable(route = ScorekeeperScreen.DetailedPlayerScores.name) {
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
                        navController.navigate(ScorekeeperScreen.EditPlayerScore.name)
                    },
                    onAddPlayerTap = {
                        with(viewModel) {
                            executeDbOperation {
                                insertNewEmptyPlayer()
                            }
                        }
                        navController.navigate(ScorekeeperScreen.EditPlayerScore.name)
                    }
                )
            } else {
                Loading()
            }
        }

        // endregion

        // region EditPlayerScoreScreen

        composable(route = ScorekeeperScreen.EditPlayerScore.name) {
            if (!viewModel.playerCache.needsUpdate) {
                EditPlayerScoreScreen(
                    initialPlayer = viewModel.playerCache.dataObject,
                    subscoreTitles = viewModel.gameCache.dataObject.subscoreTitles,
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