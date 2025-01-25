package com.waynebloom.scorekeeper.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.scorekeeper.editGame.EditGameRoute
import com.waynebloom.scorekeeper.library.LibraryRoute
import com.waynebloom.scorekeeper.meepleBase.MeepleBaseRoute
import com.waynebloom.scorekeeper.scorecard.ScoreCardRoute
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.MeepleBase.route,
    ) {

        // Meeple Base

        composable(Destination.MeepleBase.route) {
            MeepleBaseRoute(navController)
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

            EditGameRoute(navController)
        }

        // ScoreCard

        composable(
            route = "${Destination.ScoreCard.route}/{gameId}/{matchId}",
            arguments = listOf(
                navArgument(name = "gameId") { type = NavType.LongType },
                navArgument(name = "matchId") { type = NavType.LongType },
            )
        ) {
            ScoreCardRoute(navController)
        }
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
