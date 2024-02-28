package com.waynebloom.scorekeeper.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.base.MedianMeepleActivityViewModel
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.editGame.EditGameRoute
import com.waynebloom.scorekeeper.library.LibraryRoute
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.overview.OverviewRoute
import com.waynebloom.scorekeeper.playerScore.PlayerScoreScreen
import com.waynebloom.scorekeeper.ui.screens.DetailedPlayerScoresScreen
import com.waynebloom.scorekeeper.singleMatch.SingleMatchScreen
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleApp(
    deprecatedViewModel: MedianMeepleActivityViewModel, // TODO: make a new view model for this or delete
) {
    val context = LocalContext.current
    val currentAd = deprecatedViewModel.adService.currentAd.value // TODO: remove old ad service and all usages
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Overview.route,
    ) {

        // Overview

        composable(Destination.Overview.route) {
            OverviewRoute(navController)
        }

        // Library

        composable(Destination.Library.route) {
            LibraryRoute(navController)
        }

        // MatchesForGame

        composable(
            route = "${Destination.MatchesForGame.route}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {
            val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
            MatchesForGameRoute(navController, viewModel)
        }

        // StatisticsForGame

        composable(
            route = "${Destination.StatisticsForGame.route}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {
            val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
            StatisticsForGameRoute(navController, viewModel)
        }

        // EditGame

        composable(
            route = "${Destination.EditGame.route}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {

            EditGameRoute()
        }

        // SingleMatchScreen

        composable(
            route = "${Destination.SingleMatch.route}/{matchId}",
            arguments = listOf(
                navArgument(name = "matchId") { type = NavType.LongType }
            )
        ) {
            if (!deprecatedViewModel.matchCache.needsUpdate) {
                SingleMatchScreen(
                    game = deprecatedViewModel.gameCache.dataObject,
                    match = deprecatedViewModel.matchCache.dataObject,
                    onAddPlayerClick = {
                        with(deprecatedViewModel) {
                            executeDbOperation {
                                insertNewEmptyPlayer()
                            }
                        }
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                    onDeleteMatchClick = { matchId ->
                        val popSuccess = navController.popBackStack(
                            route = TopLevelScreen.SingleGame.name,
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()
                        with(deprecatedViewModel) {
                            executeDbOperation {
                                deleteMatchById(matchId)
                            }
                        }

                        navController.navigate(TopLevelScreen.SingleGame.name)
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT)
                            .show()
                    },
                    onPlayerClick = { playerId ->
                        deprecatedViewModel.updatePlayerCacheById(
                            id = playerId,
                            players = deprecatedViewModel.matchCache.dataObject.players
                        )
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                    onViewDetailedScoresClick = {
                        navController.navigate(TopLevelScreen.DetailedPlayerScores.name)
                    },
                    saveMatch = { updatedMatch ->
                        if (updatedMatch.databaseAction == DatabaseAction.UPDATE) {
                            with(deprecatedViewModel) {
                                executeDbOperation {
                                    updateMatch(updatedMatch.entity)
                                }
                            }
                        }
                        navController.popBackStack()
                        Toast.makeText(context, R.string.toast_match_updated, Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            } else {
                Loading()
            }
        }

        // DetailedPlayerScoresScreen

        composable(route = TopLevelScreen.DetailedPlayerScores.name) {
            if (deprecatedViewModel.allCachesUpToDate()) {
                DetailedPlayerScoresScreen(
                    players = deprecatedViewModel.matchCache.dataObject.players,
                    subscoreTitles = deprecatedViewModel.gameCache.dataObject.categories,
                    themeColor = LocalCustomThemeColors.current.getColorByKey(deprecatedViewModel.gameCache.dataObject.entity.color),
                    onExistingPlayerClick = { playerId ->
                        deprecatedViewModel.updatePlayerCacheById(
                            id = playerId,
                            players = deprecatedViewModel.matchCache.dataObject.players
                        )
                        navController.navigate(TopLevelScreen.EditPlayerScore.name)
                    },
                )
            } else {
                Loading()
            }
        }

        // PlayerScoreScreen

        composable(route = TopLevelScreen.EditPlayerScore.name) {
            if (!deprecatedViewModel.playerCache.needsUpdate) {
                PlayerScoreScreen(
                    initialPlayer = deprecatedViewModel.playerCache.dataObject,
                    matchObject = deprecatedViewModel.matchCache.dataObject,
                    categories = deprecatedViewModel.gameCache.dataObject.categories,
                    isGameManualRanked = deprecatedViewModel.gameCache.dataObject.getScoringMode() == ScoringMode.Manual,
                    themeColor = LocalCustomThemeColors.current.getColorByKey(deprecatedViewModel.gameCache.dataObject.entity.color),
                    onSaveClick = { playerBundle, subscoreBundles ->
                        with(deprecatedViewModel) {
                            executeDbOperation {
                                commitPlayerBundle(playerBundle)
                                commitSubscoreBundles(subscoreBundles)
                            }
                        }
                        navController.popBackStack()
                    },
                    onDeleteClick = { playerId ->
                        with(deprecatedViewModel) {
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

        // TODO: garbage for reference
        /*composable(route = TopLevelScreen.Overview.name) {

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
                    allMatches = allMatches,
                    ad = currentAd,
                    onAddGameClick = {
                        viewModel.executeDbOperation {
                            viewModel.insertNewEmptyGame()
                        }
                        navController.navigate(TopLevelScreen.EditGame.name)
                    },
                    onGoToLibraryClick = { navController.navigate(TopLevelScreen.Games.name) },
                    onGameClick = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    },
                    onMatchClick = { matchId ->
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
        }*/
        /*composable(TopLevelScreen.Games.name) {
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
                LibraryScreen(
                    games = allGames.map { it.entity },
                    currentAd = currentAd,
                    onAddNewGameClick = {
                        viewModel.executeDbOperation {
                            viewModel.insertNewEmptyGame()
                        }
                        navController.navigate(TopLevelScreen.EditGame.name)
                    },
                    onSingleGameClick = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    }
                )
            }
        }*/
        /*composable(
            route = "${Destinations.SingleGame}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {
            val gameId = it.arguments?.getLong("gameId")!!

            SingleGameScreen(
                gameObject = viewModel.gameCache.dataObject,
                currentAd = currentAd,
                onEditGameClick = {
                    navController.navigate("${TopLevelScreen.EditGame.name}/$gameId")
                },
                onNewMatchClick = {
                    with(viewModel) {
                        executeDbOperation {
                            insertNewEmptyMatch()
                        }
                    }
                    navController.navigate(TopLevelScreen.SingleMatch.name)
                },
                onSingleMatchClick = { matchId ->
                    viewModel.updateMatchCacheById(
                        id = matchId,
                        matches = viewModel.gameCache.dataObject.matches
                    )
                    navController.navigate(TopLevelScreen.SingleMatch.name)
                }
            )
        }*/
        /*composable(route = TopLevelScreen.EditGame.name) {

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
                        Toast.makeText(context, R.string.toast_game_updated, Toast.LENGTH_SHORT)
                            .show()
                    },
                    onDeleteClick = { gameId ->
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
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            } else {
                Loading()
            }
        }*/
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
