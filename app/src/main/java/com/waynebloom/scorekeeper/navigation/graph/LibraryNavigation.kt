package com.waynebloom.scorekeeper.navigation.graph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.waynebloom.scorekeeper.library.LibraryScreen
import com.waynebloom.scorekeeper.library.LibraryViewModel
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import kotlinx.serialization.Serializable

@Serializable data object Library

@Serializable data object LibrarySection

fun NavGraphBuilder.libraryDestination(
	onNavigateToMatchesForGame: (Long) -> Unit,
) {
	composable<Library> {
		val viewModel: LibraryViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsState()

		LibraryScreen(
			uiState = uiState,
			onSearchInputChanged = { viewModel.onSearchInputChanged(it) },
			onAddGameClick = {
				onNavigateToMatchesForGame(-1)
			},
			onGameClick = onNavigateToMatchesForGame,
		)
	}
}

fun NavGraphBuilder.librarySection(
	getSharedViewModel: @Composable (NavBackStackEntry) -> SingleGameViewModel,
	onNavigateToMatchesForGame: (Long) -> Unit,
	onNavigateToEditGame: (Long) -> Unit,
	onNavigateToStatisticsForGame: (Long) -> Unit,
	onNavigateToScoreCard: (Long, Long) -> Unit,
	onPopBackStack: () -> Unit,
	onPopUpTo: (Any, Boolean) -> Unit,
) {
	navigation<LibrarySection>(startDestination = Library) {

		libraryDestination(onNavigateToMatchesForGame)

		singleGameSection(
			getSharedViewModel = getSharedViewModel,
			onNavigateToEditGame = onNavigateToEditGame,
			onNavigateToMatchesForGame = onNavigateToMatchesForGame,
			onNavigateToStatisticsForGame = onNavigateToStatisticsForGame,
			onNavigateToScoreCard = onNavigateToScoreCard,
		)

		editGameDestination(
			onPopBackStack = onPopBackStack,
			onPopUpTo = onPopUpTo,
		)

		scoreCardDestination(onPopBackStack)
	}
}

fun NavController.navigateToLibrary(navOptions: NavOptions) {
	navigate(route = Library, navOptions = navOptions)
}
