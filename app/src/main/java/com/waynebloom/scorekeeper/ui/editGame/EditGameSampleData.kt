package com.waynebloom.scorekeeper.ui.editGame

import androidx.compose.runtime.Composable
import com.waynebloom.scorekeeper.ui.PreviewData

object EditGameSampleData {
    private val game = PreviewData.Games[0]
    val Default = EditGameViewModel.EditGameUiState.Content(
        categories = PreviewData.Categories,
        nameInput = game.name,
        color = game.color,
        scoringMode = game.scoringMode
    )
    val CategoryDialog = EditGameViewModel.EditGameUiState.Content(
        categories = PreviewData.Categories,
        isCategoryDialogOpen = true,
        nameInput = game.name,
        color = game.color,
        scoringMode = game.scoringMode
    )

    @Composable
    fun EditGameScreenSample(
        uiState: EditGameViewModel.EditGameUiState
    ) {

        EditGameScreen(
            uiState = uiState,
            onCategoryClick = {},
            onCategoryDialogDismiss = {},
            onCategoryInputChanged = {_,_ ->},
            onColorClick = {},
            onDeleteCategoryClick = {},
            onDeleteClick = {},
            onDrag = {},
            onDragEnd = {},
            onDragStart = {},
            onEditButtonClick = {},
            onHideCategoryInputField = {},
            onNameChanged = {},
            onNewCategoryClick = {},
            onScoringModeChanged = {}
        )
    }
}