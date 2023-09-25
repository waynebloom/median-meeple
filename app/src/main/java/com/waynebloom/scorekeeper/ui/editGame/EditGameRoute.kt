package com.waynebloom.scorekeeper.ui.editGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditGameRoute(
    viewModel: EditGameViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val rowHeightForDrag = viewModel.getCategoryRowHeight()

    viewModel.composableCoroutineScope = rememberCoroutineScope()

    EditGameScreen(
        uiState = uiState,
        onCategoryClick = viewModel::onCategoryClick,
        onCategoryDialogDismiss = viewModel::onCategoryDialogDismiss,
        onCategoryInputChanged = viewModel::onCategoryInputChanged,
        onColorClick = viewModel::onColorClick,
        onDeleteCategoryClick = viewModel::onDeleteCategoryClick,
        onDeleteClick = viewModel::onDeleteClick,
        onDrag = { viewModel.onDrag(it, rowHeightForDrag) },
        onDragEnd = viewModel::onDragEnd,
        onDragStart = viewModel::onDragStart,
        onEditButtonClick = viewModel::onEditButtonClick,
        onHideCategoryInputField = viewModel::onHideCategoryInput,
        onNameChanged = viewModel::onNameChanged,
        onNewCategoryClick = viewModel::onNewCategoryClick,
        onScoringModeChanged = viewModel::onScoringModeChanged
    )
}