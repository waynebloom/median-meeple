package com.waynebloom.scorekeeper.navigation

import android.R.attr.label
import android.R.attr.onClick
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.waynebloom.scorekeeper.navigation.graph.Library
import com.waynebloom.scorekeeper.navigation.graph.Settings
import com.waynebloom.scorekeeper.navigation.graph.hubDestination
import com.waynebloom.scorekeeper.navigation.graph.librarySection
import com.waynebloom.scorekeeper.navigation.graph.navigateToEditGame
import com.waynebloom.scorekeeper.navigation.graph.navigateToHub
import com.waynebloom.scorekeeper.navigation.graph.navigateToLibrary
import com.waynebloom.scorekeeper.navigation.graph.navigateToMatchesForGame
import com.waynebloom.scorekeeper.navigation.graph.navigateToScoreCard
import com.waynebloom.scorekeeper.navigation.graph.navigateToSettings
import com.waynebloom.scorekeeper.navigation.graph.navigateToStatisticsForGame
import com.waynebloom.scorekeeper.navigation.graph.settingsDestination
import com.waynebloom.scorekeeper.navigation.graph.settingsSection
import kotlin.reflect.KClass

val LocalNavBarHeight = compositionLocalOf<Int?> { null }

@SuppressWarnings("CyclomaticComplexMethod")
@Composable
fun MedianMeepleNavHost(
	onSendFeedback: () -> Unit,
) {

	val navController = rememberNavController()
	val currentDestination = getCurrentDestination(navController)
	var navBarHeight by remember {
		mutableStateOf(0.dp)
	}
	val density = LocalDensity.current

	Scaffold(
		bottomBar = {
			NavigationBar(
				modifier = Modifier
					.onSizeChanged { size ->
						with(density) {
							navBarHeight = size.height.toDp()
						}
					}
			) {
				TopLevelDestination.entries.forEach { destination ->
					val selected = currentDestination.isRouteInHierarchy(destination.route)

					NavigationBarItem(
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
		},
		contentWindowInsets = WindowInsets(0.dp)
	) { innerPadding ->

		NavHost(
			navController = navController,
			startDestination = Hub,
			modifier = Modifier
				.consumeWindowInsets(PaddingValues(bottom = navBarHeight))
				.padding(innerPadding)
		) {

			hubDestination(
				onNavigateToScoreCard = navController::navigateToScoreCard,
			)

			settingsSection {
				settingsDestination(onSendFeedback = onSendFeedback)
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

	when (topLevelDestination) {
		TopLevelDestination.HUB -> {
			val notAtHub =
				navController.currentDestination?.hasRoute(Hub::class) == false
			if (notAtHub) {
				navController.navigateToHub(navOptions = opts)
			}
		}

		TopLevelDestination.LIBRARY -> {
			val notAtLibrary =
				navController.currentDestination?.hasRoute(Library::class) == false
			if (notAtLibrary) {
				navController.navigateToLibrary(navOptions = opts)
			}
		}

		TopLevelDestination.SETTINGS -> {
			val notAtSettings =
				navController.currentDestination?.hasRoute(Settings::class) == false
			if (notAtSettings) {
				navController.navigateToSettings(navOptions = opts)
			}
		}
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
	} == true
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
