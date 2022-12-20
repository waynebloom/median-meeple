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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.screens.*
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.google.android.gms.ads.MobileAds
import com.waynebloom.scorekeeper.data.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.data.color.GameColors
import com.waynebloom.scorekeeper.data.color.LightThemeGameColors
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.exceptions.NullGameCache
import com.waynebloom.scorekeeper.exceptions.NullNavArgument
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
            ScorekeeperApp(gamesViewModel = gamesViewModel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gamesViewModel.adService.destroyAd()
    }
}

@Composable
fun ScorekeeperApp(gamesViewModel: GamesViewModel) {
    ScoreKeeperTheme {
        val navController = rememberNavController()

        LaunchedEffect(true) {
            while (true) {
                gamesViewModel.adService.loadNewAd()
                delay(AdService.NEW_AD_REQUEST_DELAY_MS)
                gamesViewModel.adService.currentAd.value = null
                delay(AdService.BETWEEN_ADS_DELAY_MS)
            }
        }

        CompositionLocalProvider(
            LocalGameColors provides if (isSystemInDarkTheme()) DarkThemeGameColors() else LightThemeGameColors(),
        ) {
            Scaffold { innerPadding ->
                ScoresNavHost(
                    navController = navController,
                    gamesViewModel = gamesViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun ScoresNavHost(
    navController: NavHostController,
    gamesViewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAd = gamesViewModel.adService.currentAd.value

    NavHost(
        navController = navController,
        startDestination = ScorekeeperScreen.Overview.name,
        modifier = modifier
    ) {
        // OverviewScreen
        composable(ScorekeeperScreen.Overview.name) {
            var allGames: List<GameObject> by remember { mutableStateOf(listOf()) }
            var allMatches: List<MatchObject> by remember { mutableStateOf(listOf()) }
            var isLoading: Boolean by remember { mutableStateOf(true) }

            LaunchedEffect("$route%getData") {
                gamesViewModel.games
                    .combine(gamesViewModel.matches) { games, matches ->
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
                    onAddNewGameTap = { navigateToEditGame(navController, isNewGame = true) },
                    onSingleGameTap = { gameId ->
                        gamesViewModel.cachedGameObject = findGameById(gameId, allGames)
                        navController.navigate(ScorekeeperScreen.SingleGame.name)
                    },
                    onSingleMatchTap = { matchId ->
                        val selectedMatch = findMatchById(matchId, allMatches)
                        gamesViewModel.cachedMatchObject = selectedMatch
                        gamesViewModel.cachedGameObject = findGameById(
                            id = selectedMatch.entity.gameOwnerId,
                            games = allGames
                        )
                        navigateToSingleMatch(navController, isNewMatch = false)
                    }
                )
            }
        }

        // GamesScreen
        composable(ScorekeeperScreen.Games.name) {
            var isLoading by remember { mutableStateOf(true) }
            var allGames: List<GameObject> by remember { mutableStateOf(listOf()) }

            LaunchedEffect("$route%getGames") {
                gamesViewModel.games.collectLatest {
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
                    onAddNewGameTap = { navigateToEditGame(navController, isNewGame = true) },
                    onSingleGameTap = { gameId ->
                        gamesViewModel.cachedGameObject = findGameById(gameId, allGames)
                        navController.navigate(ScorekeeperScreen.SingleGame.name)
                    }
                )
            }
        }

        // GamesScreen w/ delete intent
        composable(
            route = "${ScorekeeperScreen.Games.name}/delete/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getLong("id", 0) ?: 0
            with(gamesViewModel) {
                executeDbOperation { deleteGameById(targetGameId) }
            }
            val allGames = gamesViewModel.games.collectAsState(listOf()).value

            GamesScreen(
                games = allGames.map { it.entity },
                currentAd = currentAd,
                onAddNewGameTap = { navigateToEditGame(navController, isNewGame = true) },
                onSingleGameTap = { gameId ->
                    gamesViewModel.cachedGameObject = findGameById(gameId, allGames)
                    navController.navigate(ScorekeeperScreen.SingleGame.name)
                }
            )
        }

        // SingleGameScreen
        composable(route = ScorekeeperScreen.SingleGame.name) {
            SingleGameScreen(
                game = gamesViewModel.cachedGameObject,
                currentAd = currentAd,
                onEditGameTap = { navigateToEditGame(navController, isNewGame = false) },
                onNewMatchTap = { navigateToSingleMatch(navController, isNewMatch = true) },
                onSingleMatchTap = { matchId ->
                    gamesViewModel.cachedMatchObject = findMatchById(
                        id = matchId,
                        matches = gamesViewModel.cachedGameObject.matches
                    )

                    navigateToSingleMatch(navController, isNewMatch = false)
                }
            )
        }

        // SingleGameScreen w/ delete intent
        composable(
            route = "${ScorekeeperScreen.SingleGame.name}/delete/{matchId}",
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val deleteTargetMatchId = entry.arguments?.getLong("matchId", 0) ?: 0
            with(gamesViewModel) {
                executeDbOperation { deleteMatchById(deleteTargetMatchId) }
            }

            SingleGameScreen(
                game = gamesViewModel.cachedGameObject,
                currentAd = currentAd,
                onEditGameTap = { navigateToEditGame(navController, isNewGame = false) },
                onNewMatchTap = { navigateToSingleMatch(navController, isNewMatch = true) },
                onSingleMatchTap = { matchId ->
                    gamesViewModel.cachedMatchObject = findMatchById(
                        id = matchId,
                        matches = gamesViewModel.cachedGameObject.matches
                    )

                    navigateToSingleMatch(navController, isNewMatch = false)
                }
            )
        }

        // EditGameScreen
        composable(
            route = "${ScorekeeperScreen.EditGame.name}/{isNewGame}",
            arguments = listOf(
                navArgument("isNewGame") {
                    type = NavType.BoolType
                }
            )
        ) { entry ->
            val isNewGame = entry.arguments?.getBoolean("isNewGame")
                ?: throw NullNavArgument(route, "isNewGame")

            if (isNewGame) {
                EditGameScreen(
                    game = GameEntity(),
                    onSaveTap = { newGameEntity ->
                        val popSuccess = navController.popBackStack(
                            route = ScorekeeperScreen.Games.name,
                            inclusive = false
                        )
                        if (!popSuccess) {
                            navController.popBackStack(
                                route = ScorekeeperScreen.Overview.name,
                                inclusive = false
                            )
                        }
                        with(gamesViewModel) {
                            executeDbOperation {
                                insertGame(newGameEntity) {
                                    navController.navigate(ScorekeeperScreen.SingleGame.name)
                                    Toast.makeText(
                                        context,
                                        R.string.toast_game_created,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    isNewGame = true
                )
            } else {
                EditGameScreen(
                    game = gamesViewModel.cachedGameObject.entity,
                    onDeleteTap = { gameId ->
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT).show()
                        navController.popBackStack(
                            route = ScorekeeperScreen.Overview.name,
                            inclusive = false
                        )
                        navigateToGamesWithDeletedGame(navController, gameId)
                    },
                    onSaveTap = { updatedGame ->
                        Toast.makeText(context, R.string.toast_game_updated, Toast.LENGTH_SHORT)
                            .show()
                        navController.popBackStack()
                        with(gamesViewModel) {
                            executeDbOperation { updateGame(updatedGame) }
                        }
                    }
                )
            }
        }

        // SingleMatchScreen
        composable(
            route = "${ScorekeeperScreen.SingleMatch.name}/{isNewMatch}",
            arguments = listOf(
                navArgument("isNewMatch") {
                    type = NavType.BoolType
                }
            )
        ) { entry ->
            val isNewMatch = entry.arguments?.getBoolean("isNewMatch")

            if (isNewMatch == true) {
                SingleMatchScreen(
                    game = gamesViewModel.cachedGameObject.entity,
                    match = MatchObject().apply {
                        entity = MatchEntity(gameOwnerId = gamesViewModel.cachedGameObject.entity.id)
                    },
                    openInEditMode = true,
                    isNewMatch = true,
                    onSaveTap = { newMatch, newScores ->
                        Toast.makeText(context, R.string.toast_match_created, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        with(gamesViewModel) {
                            executeDbOperation {
                                insertMatchWithScores(newMatch, newScores.map { it.entity })
                            }
                        }
                    }
                )
            } else {
                SingleMatchScreen(
                    game = gamesViewModel.cachedGameObject.entity,
                    match = gamesViewModel.cachedMatchObject,
                    onSaveTap = { updatedMatch, updatedScores ->
                        Toast.makeText(context, R.string.toast_match_updated, Toast.LENGTH_SHORT).show()
                        updateMatchAndScores(gamesViewModel, updatedMatch, updatedScores)
                    },
                    onDeleteMatchTap = { matchId ->
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT).show()
                        val popSuccess = navController.popBackStack(
                            route = ScorekeeperScreen.SingleGame.name,
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()

                        navigateToSingleGameWithDeletedMatch(navController, matchId)
                    }
                )
            }
        }
    }
}

// region Helpers

private fun findGameById(id: Long?, games: List<GameObject>) =
    games.find { it.entity.id == id } ?: EMPTY_GAME_OBJECT

private fun findMatchById(id: Long?, matches: List<MatchObject>) =
    matches.find { it.entity.id == id } ?: EMPTY_MATCH_OBJECT

private fun updateMatchAndScores(
    gamesViewModel: GamesViewModel,
    updatedMatch: MatchEntity,
    updatedScores: List<ScoreObject>
) {
    with(gamesViewModel) {
        executeDbOperation {
            updateMatch(updatedMatch)
            forwardScoreListUpdatesToDb(updatedScores)
        }
    }
}

// endregion

// region Navigation

private fun navigateToGamesWithDeletedGame(navController: NavController, gameId: Long) {
    navController.navigate("${ScorekeeperScreen.Games.name}/delete/$gameId")
}

private fun navigateToEditGame(navController: NavController, isNewGame: Boolean) {
    navController.navigate("${ScorekeeperScreen.EditGame.name}/$isNewGame")
}

private fun navigateToSingleGameWithDeletedMatch(navController: NavController, matchId: Long) {
    navController.navigate("${ScorekeeperScreen.SingleGame.name}/delete/$matchId")
}

private fun navigateToSingleMatch(navController: NavController, isNewMatch: Boolean) {
    navController.navigate("${ScorekeeperScreen.SingleMatch.name}/$isNewMatch")
}

// endregion