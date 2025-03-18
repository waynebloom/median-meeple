package com.waynebloom.scorekeeper.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.waynebloom.scorekeeper.hub.HubRoute
import com.waynebloom.scorekeeper.library.LibraryRoute
import com.waynebloom.scorekeeper.scorecard.ScoreCardRoute
import com.waynebloom.scorekeeper.settings.SettingsRoute
import com.waynebloom.scorekeeper.settings.login.LoginRoute
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.matchesForGame.MatchesForGameRoute
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameRoute

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleNavHost() {
	val navController = rememberNavController()

	NavigationSuiteScaffold(
		navigationSuiteItems = {
			TopLevelDestination.entries.forEach {
				item(
					icon = {
						Icon(painterResource(it.icon), stringResource(it.label))
					},
					label = {
						Text(stringResource(it.label))
					},
					onClick = {},
					selected = false,
				)
			}
		}
	)


	NavHost(
		navController = navController,
		startDestination = Destination.Hub.route,
	) {

		// Hub

		composable(Destination.Hub.route) {
			HubRoute(navController)
		}

		// Settings

		composable(Destination.Settings.route) {
			SettingsRoute(navController)
		}

		// Login

		composable(Destination.Login.route) {
			LoginRoute(navController)
		}

		// Library

		composable(Destination.Library.route) {
			LibraryRoute(navController)
		}

		// MatchesForGame

		composable(
			route = "${Destination.MatchesForGame.route}/{gameID}",
			arguments = listOf(
				navArgument(name = "gameID") { type = NavType.LongType }
			)
		) {
			val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
			MatchesForGameRoute(navController, viewModel)
		}

		// StatisticsForGame

		composable(
			route = "${Destination.StatisticsForGame.route}/{gameID}",
			arguments = listOf(
				navArgument(name = "gameID") { type = NavType.LongType }
			)
		) {
			val viewModel = it.sharedViewModel<SingleGameViewModel>(navController)
			StatisticsForGameRoute(navController, viewModel)
		}

		// EditGame

		composable(
			route = "${Destination.EditGame.route}/{gameID}",
			arguments = listOf(
				navArgument(name = "gameID") { type = NavType.LongType }
			)
		) {

			EditGameRoute(navController)
		}

		// ScoreCard

		composable(
			route = "${Destination.ScoreCard.route}/{gameID}/{matchID}",
			arguments = listOf(
				navArgument(name = "gameID") { type = NavType.LongType },
				navArgument(name = "matchID") { type = NavType.LongType },
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
