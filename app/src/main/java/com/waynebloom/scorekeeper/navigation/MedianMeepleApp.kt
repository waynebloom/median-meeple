package com.waynebloom.scorekeeper.navigation

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
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.base.MedianMeepleActivityViewModel
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.editGame.EditGameRoute
import com.waynebloom.scorekeeper.editPlayer.EditPlayerRoute
import com.waynebloom.scorekeeper.library.LibraryRoute
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.playerScore.PlayerScoreScreen
import com.waynebloom.scorekeeper.ui.screens.DetailedPlayerScoresScreen
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameRoute
import com.waynebloom.scorekeeper.singleMatch.SingleMatchRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleApp(
    deprecatedViewModel: MedianMeepleActivityViewModel, // TODO: make a new view model for this or delete
) {
    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.Library.route,
    ) {

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

            EditGameRoute(navController)
        }

        // SingleMatchScreen

        composable(
            route = "${Destination.SingleMatch.route}/{gameId}/{matchId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType },
                navArgument(name = "matchId") { type = NavType.LongType },
            )
        ) {
            SingleMatchRoute(navController)
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

        composable(
            route = "${Destination.EditPlayer.route}/{gameId}/{matchId}/{playerId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType },
                navArgument(name = "matchId") { type = NavType.LongType },
                navArgument(name = "playerId") { type = NavType.LongType },
            )
        ) {

            EditPlayerRoute(navController = navController)
        }

        // TODO: garbage for reference
        /*composable(route = TopLevelScreen.EditPlayerScore.name) {
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
        }*/
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
