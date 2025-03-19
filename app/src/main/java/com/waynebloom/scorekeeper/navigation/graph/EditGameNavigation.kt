package com.waynebloom.scorekeeper.navigation.graph

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.waynebloom.scorekeeper.editGame.EditGameScreen
import com.waynebloom.scorekeeper.editGame.EditGameViewModel
import kotlinx.serialization.Serializable

@Serializable
internal data class EditGame(val gameID: Long)

fun NavGraphBuilder.editGameDestination(
	onPopBackStack: () -> Unit,
	onPopUpTo: (Any, Boolean) -> Unit,
) {
	composable<EditGame> {
		val viewModel: EditGameViewModel = hiltViewModel()
		val uiState by viewModel.uiState.collectAsStateWithLifecycle()
		val rowHeightForDrag = viewModel.getCategoryRowHeight()

		val deletedToast = Toast.makeText(LocalContext.current, "Game deleted.", Toast.LENGTH_SHORT)
		val savedToast =
			Toast.makeText(LocalContext.current, "Your changes have been saved.", Toast.LENGTH_SHORT)

		EditGameScreen(
			uiState = uiState,
			onSaveClick = {
				viewModel.onSaveClick {
					onPopBackStack()
					savedToast.show()
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
					onPopUpTo(Library, false)
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
}

fun NavController.navigateToEditGame(gameID: Long) {
	navigate(route = EditGame(gameID))
}
