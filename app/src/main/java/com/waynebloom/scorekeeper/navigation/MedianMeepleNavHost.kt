package com.waynebloom.scorekeeper.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.waynebloom.scorekeeper.navigation.graph.Hub
import com.waynebloom.scorekeeper.navigation.graph.hubDestination
import com.waynebloom.scorekeeper.navigation.graph.librarySection
import com.waynebloom.scorekeeper.navigation.graph.loginDestination
import com.waynebloom.scorekeeper.navigation.graph.navigateToEditGame
import com.waynebloom.scorekeeper.navigation.graph.navigateToHub
import com.waynebloom.scorekeeper.navigation.graph.navigateToLibrary
import com.waynebloom.scorekeeper.navigation.graph.navigateToLogin
import com.waynebloom.scorekeeper.navigation.graph.navigateToMatchesForGame
import com.waynebloom.scorekeeper.navigation.graph.navigateToScoreCard
import com.waynebloom.scorekeeper.navigation.graph.navigateToSettings
import com.waynebloom.scorekeeper.navigation.graph.navigateToStatisticsForGame
import com.waynebloom.scorekeeper.navigation.graph.settingsDestination
import com.waynebloom.scorekeeper.navigation.graph.settingsSection
import kotlin.reflect.KClass

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleNavHost() {
	val navController = rememberNavController()
	val currentDestination = getCurrentDestination(navController)

	NavigationSuiteScaffold(
		navigationSuiteItems = {
			TopLevelDestination.entries.forEach { destination ->
				val selected = currentDestination.isRouteInHierarchy(destination.route)

				item(
					icon = {
						Icon(painterResource(destination.icon), stringResource(destination.label))
					},
					label = {
						Text(stringResource(destination.label))
					},
					onClick = {
						navigateToTopLevelDestination(navController, destination)
					},
					selected = selected,
				)
			}
		}
	) {

		NavHost(
			navController = navController,
			startDestination = Hub,
		) {

			hubDestination(
				onNavigateToScoreCard = navController::navigateToScoreCard,
			)

			settingsSection {
				settingsDestination(
					onNavigateToLogin = navController::navigateToLogin,
				)

				loginDestination(
					onPopBackStack = navController::popBackStack,
				)
			}

			librarySection(
				getSharedViewModel = { it.sharedViewModel(navController) },
				onNavigateToMatchesForGame = navController::navigateToMatchesForGame,
				onNavigateToStatisticsForGame = navController::navigateToStatisticsForGame,
				onNavigateToEditGame = navController::navigateToEditGame,
				onNavigateToScoreCard = navController::navigateToScoreCard,
				onPopBackStack = navController::popBackStack,
				onPopUpTo = navController::popBackStack,
			)
		}
	}
}

fun navigateToTopLevelDestination(
	navController: NavHostController,
	topLevelDestination: TopLevelDestination,
) {
	val opts = navOptions {
		launchSingleTop = true
		restoreState = true
	}

	when(topLevelDestination) {
		TopLevelDestination.HUB -> navController.navigateToHub(navOptions = opts)
		TopLevelDestination.LIBRARY -> navController.navigateToLibrary(navOptions = opts)
		TopLevelDestination.SETTINGS -> navController.navigateToSettings(navOptions = opts)
	}
}

@Composable
private fun getCurrentDestination(navController: NavHostController): NavDestination? {
	val currentEntry = navController.currentBackStackEntryFlow
		.collectAsState(initial = null)

	return currentEntry.value?.destination
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>): Boolean {
	return this?.hierarchy?.any {
		it.hasRoute(route)
	} ?: false
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
	navController: NavController
): T {
	val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
	val parentEntry = remember(this) {
		navController.getBackStackEntry(navGraphRoute)
	}
	return hiltViewModel(parentEntry)
}
