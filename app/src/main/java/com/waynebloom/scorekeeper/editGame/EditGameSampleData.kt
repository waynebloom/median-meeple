package com.waynebloom.scorekeeper.editGame

import com.waynebloom.scorekeeper.ui.PreviewData

object EditGameSampleData {
    private val game = PreviewData.Games[0]
    val Default = EditGameUiState.Content(
        categories = PreviewData.Categories,
        color = game.color,
        dragState = DragState(),
        indexOfCategoryReceivingInput = null,
        isCategoryDialogOpen = false,
        nameInput = game.name,
        scoringMode = game.scoringMode,
        showColorMenu = false
    )
    val NoCategories = EditGameUiState.Content(
        categories = listOf(),
        color = game.color,
        dragState = DragState(),
        indexOfCategoryReceivingInput = null,
        isCategoryDialogOpen = false,
        nameInput = game.name,
        scoringMode = game.scoringMode,
        showColorMenu = false
    )
    val CategoryDialog = Default.copy(
        categories = PreviewData.Categories,
        isCategoryDialogOpen = true,
        nameInput = game.name,
        color = game.color,
        scoringMode = game.scoringMode
    )
}
