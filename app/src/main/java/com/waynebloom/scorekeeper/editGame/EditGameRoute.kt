package com.waynebloom.scorekeeper.editGame

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.waynebloom.scorekeeper.navigation.Destination

@Composable
fun EditGameRoute(
	navController: NavHostController,
	viewModel: EditGameViewModel = hiltViewModel()
) {

	val uiState by viewModel.uiState.collectAsState()
	val rowHeightForDrag = viewModel.getCategoryRowHeight()

	val deletedToast = Toast.makeText(LocalContext.current, "Game deleted.", Toast.LENGTH_SHORT)
	val savedToast =
		Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

	viewModel.composableCoroutineScope = rememberCoroutineScope()

	EditGameScreen(
		uiState = uiState,
		onSaveClick = {
			viewModel.onSaveClick {
				savedToast.show()
				navController.popBackStack()
			}
		},
		onCategoryClick = viewModel::onCategoryClick,
		onCategoryDialogDismiss = viewModel::onCategoryDialogDismiss,
		onCategoryInputChanged = viewModel::onCategoryInputChanged,
		onColorClick = viewModel::onColorClick,
		onDeleteCategoryClick = viewModel::onDeleteCategoryClick,
		onDeleteClick = {
			viewModel.onDeleteClick {
				deletedToast.show()
				navController.popBackStack(route = Destination.Library.route, inclusive = false)
			}
		},
		onDrag = { viewModel.onDrag(it, rowHeightForDrag) },
		onDragEnd = viewModel::onDragEnd,
		onDragStart = viewModel::onDragStart,
		onEditButtonClick = viewModel::onEditButtonClick,
		onHideCategoryInputField = viewModel::onCategoryDoneClick,
		onNameChanged = viewModel::onNameChanged,
		onNewCategoryClick = viewModel::onNewCategoryClick,
		onScoringModeChanged = viewModel::onScoringModeChanged
	)
}
