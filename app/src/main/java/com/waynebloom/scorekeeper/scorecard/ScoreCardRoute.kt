package com.waynebloom.scorekeeper.scorecard

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun ScoreCardRoute(
    navController: NavHostController,
    viewModel: ScoreCardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val deletedToast = Toast.makeText(LocalContext.current, "Match deleted.", Toast.LENGTH_SHORT)
    val savedToast = Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

    MedianMeepleTheme {
        ScoreCardScreen(
            uiState = uiState,
            onPlayerClick = viewModel::onPlayerClick,
            onSaveClick = {
                viewModel.onSaveClick {
                    navController.popBackStack()
                    savedToast.show()
                }
            },
            onDeleteClick = {
                viewModel.onDeleteClick {
                    navController.popBackStack()
                    deletedToast.show()
                }
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
