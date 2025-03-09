package com.waynebloom.scorekeeper.library

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel

object LibrarySampleData {
	private val gameCards = listOf(
		LibraryGameCard(
			id = 0,
			name = "Carcassonne",
			color = GameDomainModel.DisplayColors[17],
			noOfMatches = "17",
			highScore = "97"
		),
		LibraryGameCard(
			id = 1,
			name = "Wingspan",
			color = GameDomainModel.DisplayColors[2],
			noOfMatches = "57",
			highScore = "141"
		),
		LibraryGameCard(
			id = 2,
			name = "Century: Golem Edition",
			color = GameDomainModel.DisplayColors[5],
			noOfMatches = "4",
			highScore = "114"
		),
		LibraryGameCard(
			id = 3,
			name = "Azul: Queen's Garden",
			color = GameDomainModel.DisplayColors[13],
			noOfMatches = "29",
			highScore = "245"
		),
		LibraryGameCard(
			id = 4,
			name = "Catan",
			color = GameDomainModel.DisplayColors[20],
			noOfMatches = "30",
			highScore = "17"
		),
	)
	val Default = LibraryUiState.Content(
		gameCards = gameCards
	)
	val NoGames = LibraryUiState.Content(
		gameCards = emptyList()
	)
	val ActiveSearch = LibraryUiState.Content(
		gameCards = gameCards,
		searchInput = TextFieldValue("Catan"),
		isSearchBarFocused = true
	)
	val EmptySearch = LibraryUiState.Content(
		gameCards = emptyList(),
		searchInput = TextFieldValue("Catan"),
		isSearchBarFocused = true
	)
}