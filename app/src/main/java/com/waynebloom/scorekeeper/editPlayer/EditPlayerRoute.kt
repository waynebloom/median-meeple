package com.waynebloom.scorekeeper.editPlayer

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.theme.UserSelectedPrimaryColorTheme


@Composable
fun EditPlayerRoute(
    navController: NavHostController,
    viewModel: EditPlayerViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.game.color)

    val deletedToast = Toast.makeText(LocalContext.current, "Player deleted.", Toast.LENGTH_SHORT)
    val savedToast = Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

    UserSelectedPrimaryColorTheme(primaryColor) {
        EditPlayerScreen(
            uiState = uiState,
            onNameChange = viewModel::onNameChange,
            onRankChange = viewModel::onRankChange,
            onUseCategorizedScoreToggle = viewModel::onUseCategorizedScoreToggle,
            onTotalScoreChange = viewModel::onTotalScoreChange,
            onCategoryScoreChange = viewModel::onCategoryScoreChange,
            onSaveClick = {
                viewModel.onSaveClick()
                savedToast.show()
                navController.popBackStack()
            },
            onDeleteClick = {
                viewModel.onDeleteClick()
                deletedToast.show()
                navController.popBackStack()
            },
        )
    }
}
