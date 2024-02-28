package com.waynebloom.scorekeeper.singleGame.matchesForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme

@Composable
fun MatchesForGameRoute(
    navController: NavHostController,
    viewModel: SingleGameViewModel,
) {

    val uiState by viewModel.matchesForGameUiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.primaryColorId)

    UserSelectedPrimaryColorTheme(primaryColor) {
        MatchesForGameScreen(
            uiState = uiState,
            onSearchInputChanged = viewModel::onSearchInputChanged,
            onSortModeChanged = viewModel::onSortModeChanged,
            onSortDirectionChanged = viewModel::onSortDirectionChanged,
            onEditGameClick = {
                navController.navigate("${Destination.EditGame.route}/${viewModel.gameId}")
            },
            onStatisticsTabClick = {
                val route = "${Destination.StatisticsForGame.route}/${viewModel.gameId}"
                if (!navController.popBackStack(route = route, inclusive = false)) {
                    navController.navigate(route)
                }
            },
            onSortButtonClick = viewModel::onSortButtonClick,
            onMatchClick = {
                navController.navigate("${Destination.SingleMatch.route}/$it")
            },
            onAddMatchClick = { // TODO: this doesn't work anymore. Fix it.
                navController.navigate("${Destination.SingleMatch.route}/new")
            },
            onSortDialogDismiss = viewModel::onSortDialogDismiss,
        )
    }
}
