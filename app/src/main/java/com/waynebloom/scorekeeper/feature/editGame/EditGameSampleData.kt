package com.waynebloom.scorekeeper.feature.editGame

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.common.ScoringMode

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
	val Categories = listOf(
		CategoryDomainModel(
			name = TextFieldValue("Eggs"),
			position = 0
		),
		CategoryDomainModel(
			name = TextFieldValue("Cached Food"),
			position = 1
		),
		CategoryDomainModel(
			name = TextFieldValue("Tucked Cards"),
			position = 2
		)
	)
	private val game = Games[0]
	val Default = EditGameUiState.Content(
		categories = Categories,
		colorIndex = game.displayColorIndex,
		dragState = DragState(),
		indexOfSelectedCategory = -1,
		isCategoryDialogOpen = false,
		name = game.name,
		scoringMode = game.scoringMode,
		showColorMenu = false
	)
	val NoCategories = EditGameUiState.Content(
		categories = listOf(),
		colorIndex = game.displayColorIndex,
		dragState = DragState(),
		indexOfSelectedCategory = -1,
		isCategoryDialogOpen = false,
		name = game.name,
		scoringMode = game.scoringMode,
		showColorMenu = false
	)
	val CategoryDialog = Default.copy(
		colorIndex = game.displayColorIndex,
		indexOfSelectedCategory = 1,
		isCategoryDialogOpen = true,
		name = game.name,
		scoringMode = game.scoringMode
	)
}
