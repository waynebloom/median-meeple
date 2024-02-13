package com.waynebloom.scorekeeper.ui.singleGame.matchesForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.navigation.Destination
import com.waynebloom.scorekeeper.ui.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.ui.theme.UserSelectedPrimaryColorTheme

@Composable
fun MatchesForGameRoute(
    navController: NavHostController,
    viewModel: SingleGameViewModel
) {

    val uiState by viewModel.matchesForGameUiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.primaryColorId)

    UserSelectedPrimaryColorTheme(primaryColor) {
        MatchesForGameScreen(
            uiState,
            onSearchInputChanged = viewModel::onSearchInputChanged,
            onSortButtonClick = viewModel::onSortButtonClick,
            onSortModeChanged = viewModel::onSortModeChanged,
            onSortDirectionChanged = viewModel::onSortDirectionChanged,
            onSortDialogDismiss = viewModel::onSortDialogDismiss,
            onMatchClick = { navController.navigate("${Destination.SingleMatch}/$it") },
            onAddMatchClick = { navController.navigate(Destination.StatisticsForGame.route) }
            // onAddMatchClick = { navController.navigate("${Destination.SingleMatch}/new")}
        )
    }
}