package com.waynebloom.scorekeeper

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.waynebloom.scorekeeper.data.*
import com.waynebloom.scorekeeper.screens.*
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.waynebloom.scorekeeper.data.color.DarkThemeGameColors
import com.waynebloom.scorekeeper.data.color.LightThemeGameColors
import com.waynebloom.scorekeeper.ui.theme.LocalGameColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var LocalNativeAd: ProvidableCompositionLocal<NativeAd?> = compositionLocalOf { null }

class ScoresActivity : ComponentActivity() {
    private lateinit var gamesViewModel: GamesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gamesViewModel = ViewModelProvider(this)[GamesViewModel::class.java]
        setContent {
            ScorekeeperApp(gamesViewModel = gamesViewModel)
        }

        MobileAds.initialize(this)
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

        CompositionLocalProvider(
            LocalGameColors provides if (isSystemInDarkTheme()) DarkThemeGameColors() else LightThemeGameColors(),
        ) {
            Scaffold() { innerPadding ->
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
    var currentAd: NativeAd? by remember { mutableStateOf(null) }

    if (gamesViewModel.adService.adLoader != null) {
        gamesViewModel.adService.adLoader = AdLoader.Builder(
            context,
            if (BuildConfig.DEBUG) AdmobID.DEBUG.id else AdmobID.RELEASE.id
        )
            .forNativeAd {
                currentAd = it
                gamesViewModel.adService.currentAd = it
            }
            .build()
    }

    LaunchedEffect(true) {
        while (true) {
            gamesViewModel.adService.loadNewAd()
            delay(60000)
        }
    }

    NavHost(
        navController = navController,
        startDestination = ScorekeeperScreen.Overview.name,
        modifier = modifier
    ) {
        // OverviewScreen
        composable(ScorekeeperScreen.Overview.name) {
            val allGames by gamesViewModel.games.collectAsState(listOf())
            val allMatches by gamesViewModel.matches.collectAsState(listOf())
            OverviewScreen(
                games = allGames.take(6),
                matches = allMatches
                    .sortedByDescending { it.entity.timeModified }
                    .take(6),
                currentAd = currentAd,
                onSeeAllGamesTap = { navController.navigate(ScorekeeperScreen.Games.name) },
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { gameId -> navigateToSingleGame(navController, gameId) },
                onSingleMatchTap = { gameOwnerId, matchId -> navigateToSingleMatch(navController, gameOwnerId, matchId) }
            )
        }

        // GamesScreen
        composable(ScorekeeperScreen.Games.name) {
            GamesScreen(
                games = gamesViewModel.games.collectAsState(listOf()).value.map { it.entity },
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
            )
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
            LaunchedEffect(targetGameId) { gamesViewModel.deleteGameById(targetGameId) }
            GamesScreen(
                games = gamesViewModel.games.collectAsState(listOf()).value.map { it.entity },
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
            )
        }

        // SingleGameScreen
        composable(
            route = "${ScorekeeperScreen.SingleGame.name}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getLong("id", 0) ?: 0
            val targetGame = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME_OBJECT)
                .value ?: EMPTY_GAME_OBJECT

            SingleGameScreen(
                game = targetGame,
                currentAd = currentAd,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewMatchTap = { gameId -> navigateToNewMatch(navController, gameId) },
                onSingleMatchTap = { gameOwnerId, matchId -> navigateToSingleMatch(navController, gameOwnerId, matchId) }
            )
        }

        // SingleGameScreen w/ delete intent
        composable(
            route = "${ScorekeeperScreen.SingleGame.name}/delete/{gameId}/{matchId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.LongType
                },
                navArgument("matchId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val targetMatchId = entry.arguments?.getLong("matchId", 0) ?: 0
            LaunchedEffect(key1 = targetMatchId) { gamesViewModel.deleteMatchById(targetMatchId) }

            val targetGameId = entry.arguments?.getLong("gameId", 0) ?: 0
            val targetGame = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME_OBJECT)
                .value ?: EMPTY_GAME_OBJECT

            SingleGameScreen(
                game = targetGame,
                currentAd = currentAd,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewMatchTap = { gameId -> navigateToNewMatch(navController, gameId) },
                onSingleMatchTap = { gameOwnerId, matchId -> navigateToSingleMatch(navController, gameOwnerId, matchId) }
            )
        }

        // EditGameScreen
        composable(
            route = "${ScorekeeperScreen.EditGame.name}/{gameId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getLong("gameId", 0) ?: 0

            if (targetGameId == -1L) {
                EditGameScreen(
                    game = GameEntity(),
                    onSaveTap = { newGame ->
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
                        gamesViewModel.viewModelScope.launch {
                            gamesViewModel.insert(newGame) { newGameId ->
                                navigateToSingleGame(navController, newGameId)
                                Toast.makeText(context, R.string.toast_game_created, Toast.LENGTH_SHORT).show()
                            }
                        }

                    },
                    isNewGame = true
                )
            } else {
                val targetGame = gamesViewModel
                    .getGameById(targetGameId)
                    .collectAsState(EMPTY_GAME_OBJECT)
                    .value ?: EMPTY_GAME_OBJECT
                EditGameScreen(
                    game = targetGame.entity,
                    onDeleteTap = { gameId ->
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT).show()
                        navController.popBackStack(
                            route = ScorekeeperScreen.Overview.name,
                            inclusive = false
                        )
                        navigateToGamesWithDeletedGame(navController, gameId)
                    },
                    onSaveTap = { updatedGame ->
                        Toast.makeText(context, R.string.toast_game_updated, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        gamesViewModel.viewModelScope.launch {
                            gamesViewModel.updateGame(updatedGame)
                        }
                    }
                )
            }
        }

        // SingleMatchScreen
        composable(
            route = "${ScorekeeperScreen.SingleMatch.name}/{gameId}/{matchId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.LongType
                },
                navArgument("matchId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val targetMatchId = entry.arguments?.getLong("matchId", 0) ?: 0
            val targetGameId = entry.arguments?.getLong("gameId", 0) ?: 0
            val game = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME_OBJECT)
                .value ?: EMPTY_GAME_OBJECT
            if (targetMatchId == -1L) {
                SingleMatchScreen(
                    game = game.entity,
                    match = MatchObject().apply {
                        entity = MatchEntity(gameOwnerId = game.entity.id)
                    },
                    openInEditMode = true,
                    isNewMatch = true,
                    onSaveTap = { newMatch, newScores ->
                        Toast.makeText(context, R.string.toast_match_created, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        gamesViewModel.viewModelScope.launch {
                            gamesViewModel.insertMatchWithScores(newMatch, newScores.map { it.entity })
                        }
                    }
                )
            } else {
                val targetMatch = gamesViewModel
                    .getMatchById(targetMatchId)
                    .collectAsState(EMPTY_MATCH_OBJECT)
                    .value ?: EMPTY_MATCH_OBJECT
                SingleMatchScreen(
                    game = game.entity,
                    match = targetMatch,
                    onSaveTap = { match, newScores ->
                        Toast.makeText(context, R.string.toast_match_updated, Toast.LENGTH_SHORT).show()
                        gamesViewModel.viewModelScope.launch {
                            gamesViewModel.updateMatch(match)
                            newScores.forEach { score ->
                                when(score.action) {
                                    DatabaseAction.UPDATE -> gamesViewModel.updateScore(score.entity)
                                    DatabaseAction.INSERT -> gamesViewModel.insert(score.entity) {}
                                    DatabaseAction.DELETE -> gamesViewModel.deleteScoreById(score.entity.id)
                                    else -> {}
                                }
                            }
                        }
                    },
                    onDeleteMatchTap = { gameId, matchId ->
                        val popSuccess = navController.popBackStack(
                            route = "${ScorekeeperScreen.SingleGame.name}/{id}",
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()

                        navigateToSingleGameWithDeletedMatch(navController, gameId, matchId)
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

private fun navigateToGamesWithDeletedGame(navController: NavController, gameId: Long) {
    navController.navigate("${ScorekeeperScreen.Games.name}/delete/$gameId")
}

private fun navigateToSingleGame(navController: NavController, gameId: Long) {
    navController.navigate("${ScorekeeperScreen.SingleGame.name}/$gameId")
}

private fun navigateToEditGame(navController: NavController, gameId: Long) {
    navController.navigate("${ScorekeeperScreen.EditGame.name}/$gameId")
}

private fun navigateToNewGame(navController: NavController) {
    navController.navigate("${ScorekeeperScreen.EditGame.name}/-1")
}

private fun navigateToSingleGameWithDeletedMatch(navController: NavController, gameId: Long, matchId: Long) {
    navController.navigate("${ScorekeeperScreen.SingleGame.name}/delete/$gameId/$matchId")
}

private fun navigateToSingleMatch(navController: NavController, gameId: Long, matchId: Long) {
    navController.navigate("${ScorekeeperScreen.SingleMatch.name}/$gameId/$matchId")
}

private fun navigateToNewMatch(navController: NavController, gameId: Long) {
    navController.navigate("${ScorekeeperScreen.SingleMatch.name}/$gameId/-1")
}