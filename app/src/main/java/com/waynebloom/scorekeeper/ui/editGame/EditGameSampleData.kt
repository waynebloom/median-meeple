package com.waynebloom.scorekeeper.ui.editGame

import com.waynebloom.scorekeeper.GameObjectsDefaultPreview
import com.waynebloom.scorekeeper.ui.PreviewData

object EditGameSampleData {
    private val game = PreviewData.Games[0]
    val UiState = EditGameViewModel.EditGameUiState.Content(
        categories = PreviewData.Categories,
        nameInput = game.name,
        color = game.color,
        scoringMode = game.scoringMode
    )
}