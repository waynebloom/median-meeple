package com.waynebloom.scorekeeper.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.base.MedianMeepleActivityViewModel
import com.waynebloom.scorekeeper.ui.components.Loading
import com.waynebloom.scorekeeper.ui.editGame.EditGameRoute
import com.waynebloom.scorekeeper.ui.library.LibraryRoute
import com.waynebloom.scorekeeper.ui.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.ui.overview.OverviewRoute
import com.waynebloom.scorekeeper.ui.screens.DetailedPlayerScoresScreen
import com.waynebloom.scorekeeper.ui.screens.EditPlayerScoreScreen
import com.waynebloom.scorekeeper.ui.screens.SingleMatchScreen
import com.waynebloom.scorekeeper.ui.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.StatisticsForGameRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleApp(
    deprecatedViewModel: MedianMeepleActivityViewModel, // TODO: make a new view model for this or delete
) {
    val context = LocalContext.current
    val currentAd = deprecatedViewModel.adService.currentAd.value // TODO: remove old ad service and all usages
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            MedianMeepleBottomNavigation(
                items = getBottomNavItems(currentBackStackEntry),
                navController = navController,
                currentBackStackEntry = currentBackStackEntry
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Destination.Overview.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Destination.Overview.route) {
                OverviewRoute(navController)
            }

            composable(Destination.Library.route) {
                LibraryRoute(navController)
            }

            navigation(
                startDestination = Destination.MatchesForGame.route,
                route = "${Destination.SingleGame.route}/{gameId}",
                arguments = listOf(
                    navArgument(name = "gameId") { type = NavType.LongType }
                )
            ) {

                composable(Destination.MatchesForGame.route) {
                    val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
                    MatchesForGameRoute(navController, viewModel)
                }
                composable(Destination.StatisticsForGame.route) {
                    val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
                    StatisticsForGameRoute(viewModel)
                }
            }

            composable(
                route = "${Destinations.EditGame}/{gameId}",
                arguments = listOf(
                    navArgument(name = "gameId") { type = NavType.LongType }
                )
            ) {

                EditGameRoute()
            }

            // region SingleMatchScreen

            composable(
                route = "${Destinations.SingleMatch}/{matchId}",
                arguments = listOf(
                    navArgument(name = "matchId") { type = NavType.LongType }
                )
            ) {
                if (!deprecatedViewModel.matchCache.needsUpdate) {
                    SingleMatchScreen(
                        game = deprecatedViewModel.gameCache.dataObject,
                        match = deprecatedViewModel.matchCache.dataObject,
                        onAddPlayerTap = {
                            with(deprecatedViewModel) {
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
                            with(deprecatedViewModel) {
                                executeDbOperation {
                                    deleteMatchById(matchId)
                                }
                            }

                            navController.navigate(TopLevelScreen.SingleGame.name)
                            Toast.makeText(context, R.string.toast_match_deleted, Toast.LENGTH_SHORT)
                                .show()
                        },
                        onPlayerTap = { playerId ->
                            deprecatedViewModel.updatePlayerCacheById(
                                id = playerId,
                                players = deprecatedViewModel.matchCache.dataObject.players
                            )
                            navController.navigate(TopLevelScreen.EditPlayerScore.name)
                        },
                        onViewDetailedScoresTap = {
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

            // endregion

            // region DetailedPlayerScoresScreen

            composable(route = TopLevelScreen.DetailedPlayerScores.name) {
                if (deprecatedViewModel.allCachesUpToDate()) {
                    DetailedPlayerScoresScreen(
                        players = deprecatedViewModel.matchCache.dataObject.players,
                        subscoreTitles = deprecatedViewModel.gameCache.dataObject.categories,
                        themeColor = LocalCustomThemeColors.current.getColorByKey(deprecatedViewModel.gameCache.dataObject.entity.color),
                        onExistingPlayerTap = { playerId ->
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

            // endregion

            // region EditPlayerScoreScreen

            composable(route = TopLevelScreen.EditPlayerScore.name) {
                if (!deprecatedViewModel.playerCache.needsUpdate) {
                    EditPlayerScoreScreen(
                        initialPlayer = deprecatedViewModel.playerCache.dataObject,
                        matchObject = deprecatedViewModel.matchCache.dataObject,
                        categories = deprecatedViewModel.gameCache.dataObject.categories,
                        isGameManualRanked = deprecatedViewModel.gameCache.dataObject.getScoringMode() == ScoringMode.Manual,
                        themeColor = LocalCustomThemeColors.current.getColorByKey(deprecatedViewModel.gameCache.dataObject.entity.color),
                        onSaveTap = { playerBundle, subscoreBundles ->
                            with(deprecatedViewModel) {
                                executeDbOperation {
                                    commitPlayerBundle(playerBundle)
                                    commitSubscoreBundles(subscoreBundles)
                                }
                            }
                            navController.popBackStack()
                        },
                        onDeleteTap = { playerId ->
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

            // endregion

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
        }
    }
}

@Composable
fun MedianMeepleBottomNavigation(
    items: List<Destination.BottomNavDestination>,
    navController: NavHostController,
    currentBackStackEntry: NavBackStackEntry?
) {

    BottomNavigation {
        items.forEach { screen ->

            val selected = currentBackStackEntry?.destination?.hierarchy?.any { it.route == screen.route } == true
            val icon = if (selected) screen.selectedIconResource else screen.unselectedIconResource
            BottomNavigationItem(
                icon = { Icon(painterResource(icon), null) },
                label = { Text(stringResource(screen.labelResource)) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        val requiredStartDestinationId = currentBackStackEntry
                            ?.destination
                            ?.parent
                            ?.findStartDestination()
                            ?.id
                            ?: return@navigate
                        popUpTo(requiredStartDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private fun getBottomNavItems(entry: NavBackStackEntry?): List<Destination.BottomNavDestination> {
    val requiredHierarchy = entry?.destination?.hierarchy ?: return emptyList()
    val singleGameDestinationExists = requiredHierarchy
        .any { it.route?.startsWith(Destination.SingleGame.route) == true }
    return if(singleGameDestinationExists) {
        listOf(
            Destination.MatchesForGame,
            Destination.StatisticsForGame
        )
    } else {
        listOf(
            Destination.Overview,
            Destination.Library
        )
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