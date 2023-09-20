package com.waynebloom.scorekeeper.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.library.LibraryRoute
import com.waynebloom.scorekeeper.ui.overview.OverviewRoute
import com.waynebloom.scorekeeper.ui.screens.DetailedPlayerScoresScreen
import com.waynebloom.scorekeeper.ui.screens.EditPlayerScoreScreen
import com.waynebloom.scorekeeper.ui.screens.SingleGameScreen
import com.waynebloom.scorekeeper.ui.screens.SingleMatchScreen
import com.waynebloom.scorekeeper.ui.base.MedianMeepleActivityViewModel
import com.waynebloom.scorekeeper.ui.editGame.EditGameRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun NavHost(
    viewModel: MedianMeepleActivityViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentAd = viewModel.adService.currentAd.value
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.Overview,
        modifier = modifier
    ) {

        // region OverviewScreen

        composable(route = Destinations.Overview) {

            OverviewRoute(navController = navController)
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
                    onAddGameTap = {
                        viewModel.executeDbOperation {
                            viewModel.insertNewEmptyGame()
                        }
                        navController.navigate(TopLevelScreen.EditGame.name)
                    },
                    onGoToLibraryTap = { navController.navigate(TopLevelScreen.Games.name) },
                    onGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    },
                    onMatchTap = { matchId ->
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

        // endregion

        // region GamesScreen

        composable(route = Destinations.Library) {

            LibraryRoute(navController = navController)
        }

        // TODO: garbage for reference
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
                    onAddNewGameTap = {
                        viewModel.executeDbOperation {
                            viewModel.insertNewEmptyGame()
                        }
                        navController.navigate(TopLevelScreen.EditGame.name)
                    },
                    onSingleGameTap = { gameId ->
                        viewModel.updateGameCacheById(id = gameId, games = allGames)
                        navController.navigate(TopLevelScreen.SingleGame.name)
                    }
                )
            }
        }*/

        // endregion

        // region SingleGameScreen

        composable(
            route = "${Destinations.SingleGame}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {
            val gameId = it.arguments?.getLong("gameId")!!

            SingleGameScreen(
                gameObject = viewModel.gameCache.dataObject,
                currentAd = currentAd,
                onEditGameTap = {
                    navController.navigate("${TopLevelScreen.EditGame.name}/$gameId")
                },
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
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT)
                            .show()
                    }
                )
            } else {
                Loading()
            }
        }*/

        composable(
            route = "${Destinations.EditGame}/{gameId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType }
            )
        ) {

            EditGameRoute()
        }

        // endregion

        // region SingleMatchScreen

        composable(
            route = "${Destinations.SingleMatch}/{matchId}",
            arguments = listOf(
                navArgument(name = "matchId") { type = NavType.LongType }
            )
        ) {
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
                        Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT)
                            .show()
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
                        Toast.makeText(context, R.string.toast_match_updated, Toast.LENGTH_SHORT)
                            .show()
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
                    subscoreTitles = viewModel.gameCache.dataObject.categories,
                    themeColor = LocalCustomThemeColors.current.getColorByKey(viewModel.gameCache.dataObject.entity.color),
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
                    categories = viewModel.gameCache.dataObject.categories,
                    isGameManualRanked = viewModel.gameCache.dataObject.getScoringMode() == ScoringMode.Manual,
                    themeColor = LocalCustomThemeColors.current.getColorByKey(viewModel.gameCache.dataObject.entity.color),
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