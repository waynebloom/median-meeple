package com.waynebloom.scorekeeper.singleMatch

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
fun ScoreCardRoute(
    navController: NavHostController,
    viewModel: ScoreCardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val primaryColor = LocalCustomThemeColors.current.getColorByKey(uiState.game.color)

    val deletedToast = Toast.makeText(LocalContext.current, "Match deleted.", Toast.LENGTH_SHORT)
    val savedToast = Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

    UserSelectedPrimaryColorTheme(primaryColor) {
        ScoreCardScreen(
            uiState = uiState,
            onPlayerClick = viewModel::onPlayerClick,
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
            onAddPlayer = viewModel::onAddPlayer,
            onDeletePlayerClick = viewModel::onDeletePlayerClick,
            onCellChange = viewModel::onCellChange,
            onDialogTextFieldChange = viewModel::onDialogTextFieldChange,
            onDateChange = viewModel::onDateChange,
            onLocationChange = viewModel::onLocationChange,
            onNotesChange = viewModel::onNotesChange,
            onPlayerChange = viewModel::onPlayerChange,
        )
    }
}
