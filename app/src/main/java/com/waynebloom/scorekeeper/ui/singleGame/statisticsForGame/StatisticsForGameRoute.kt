package com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.singleGame.SingleGameViewModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.StatisticsForGameScreen
import com.waynebloom.scorekeeper.ui.theme.UserSelectedPrimaryColorTheme

@Composable
fun StatisticsForGameRoute(viewModel: SingleGameViewModel) {

    val uiState by viewModel.statisticsForGameUiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.primaryColorId)

    UserSelectedPrimaryColorTheme(primaryColor) {
        StatisticsForGameScreen(
            uiState = uiState,
            onBestWinnerButtonClick = viewModel::onBestWinnerButtonClick,
            onHighScoreButtonClick = viewModel::onHighScoreButtonClick,
            onUniqueWinnersButtonClick = viewModel::onUniqueWinnersButtonClick,
            onCategoryClick = viewModel::onCategoryClick
        )
    }
}