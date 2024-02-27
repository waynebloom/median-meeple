package com.waynebloom.scorekeeper.singleGame.statisticsForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.ui.StatisticsForGameScreen
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme

@Composable
fun StatisticsForGameRoute(
    navController: NavHostController,
    viewModel: SingleGameViewModel,
) {

    val uiState by viewModel.statisticsForGameUiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.primaryColorId)

    UserSelectedPrimaryColorTheme(primaryColor) {
        StatisticsForGameScreen(
            uiState = uiState,
            onEditGameClick = {
                navController.navigate("${Destination.EditGame.route}/${viewModel.gameId}")
            },
            onMatchesTabClick = {
                navController.navigate("${Destination.MatchesForGame.route}/${viewModel.gameId}")
            },
            onBestWinnerButtonClick = viewModel::onBestWinnerButtonClick,
            onHighScoreButtonClick = viewModel::onHighScoreButtonClick,
            onUniqueWinnersButtonClick = viewModel::onUniqueWinnersButtonClick,
            onCategoryClick = viewModel::onCategoryClick
        )
    }
}
