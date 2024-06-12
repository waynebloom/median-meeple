package com.waynebloom.scorekeeper.editGame

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.PreviewData
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel

object EditGameSampleData {
    private val Games = listOf(
        GameDomainModel(
            name = TextFieldValue("Wingspan"),
            displayColorIndex = 0,
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = TextFieldValue("Splendor"),
            displayColorIndex = 5,
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = TextFieldValue("Catan"),
            displayColorIndex = 15,
            scoringMode = ScoringMode.Descending
        )
    )
    private val game = Games[0]
    val Default = EditGameUiState.Content(
        categories = PreviewData.Categories,
        colorIndex = game.displayColorIndex,
        dragState = DragState(),
        indexOfCategoryReceivingInput = null,
        isCategoryDialogOpen = false,
        name = game.name,
        scoringMode = game.scoringMode,
        showColorMenu = false
    )
    val NoCategories = EditGameUiState.Content(
        categories = listOf(),
        colorIndex = game.displayColorIndex,
        dragState = DragState(),
        indexOfCategoryReceivingInput = null,
        isCategoryDialogOpen = false,
        name = game.name,
        scoringMode = game.scoringMode,
        showColorMenu = false
    )
    val CategoryDialog = Default.copy(
        categories = PreviewData.Categories,
        colorIndex = game.displayColorIndex,
        indexOfCategoryReceivingInput = 1,
        isCategoryDialogOpen = true,
        name = game.name,
        scoringMode = game.scoringMode
    )
}
