package com.waynebloom.scorekeeper.feature.hub

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameWithMatchCount

internal object HubSampleData {
	val shape = RoundedCornerShape(4.dp)
	val Default = HubUiState.Content(
		favoriteGames = listOf(
			GameDomainModel(
				name = TextFieldValue("Game 1"),
				displayColorIndex = 0,
			),
			GameDomainModel(
				name = TextFieldValue("Game 2"),
				displayColorIndex = 1,
			),
			GameDomainModel(
				name = TextFieldValue("Game 3"),
				displayColorIndex = 2,
			),
			GameDomainModel(
				name = TextFieldValue("Game 4"),
				displayColorIndex = 3,
			),
			GameDomainModel(
				name = TextFieldValue("Game 5"),
				displayColorIndex = 4,
			),
			GameDomainModel(
				name = TextFieldValue("Game 6"),
				displayColorIndex = 5,
			),
			GameDomainModel(
				name = TextFieldValue("Game 7"),
				displayColorIndex = 6,
			),
			GameDomainModel(
				name = TextFieldValue("Game 8"),
				displayColorIndex = 7,
			),
			GameDomainModel(
				name = TextFieldValue("Game 9"),
				displayColorIndex = 8,
			),
			GameDomainModel(
				name = TextFieldValue("Game 10"),
				displayColorIndex = 9,
			),
			GameDomainModel(
				name = TextFieldValue("Game 11"),
				displayColorIndex = 10,
			),
			GameDomainModel(
				name = TextFieldValue("Game 12"),
				displayColorIndex = 11,
			),
			GameDomainModel(
				name = TextFieldValue("Game 13"),
				displayColorIndex = 12,
			),
			GameDomainModel(
				name = TextFieldValue("Game 14"),
				displayColorIndex = 13,
			),
			GameDomainModel(
				name = TextFieldValue("Game 15"),
				displayColorIndex = 14,
			),
			GameDomainModel(
				name = TextFieldValue("Game 16"),
				displayColorIndex = 15,
			),
		),
		nonFavoritesWithMatchCount = listOf(
			GameWithMatchCount(
				game = GameDomainModel(
					name = TextFieldValue("Other Game"),
					displayColorIndex = 17,
				),
				matchCount = 5,
			),
			GameWithMatchCount(
				game = GameDomainModel(
					name = TextFieldValue("Another Game"),
					displayColorIndex = 4,
				),
				matchCount = 9,
			),
			GameWithMatchCount(
				game = GameDomainModel(
					name = TextFieldValue("Test Game"),
					displayColorIndex = 13,
				),
				matchCount = 17,
			),
		),
		chartKey = mapOf(
			"Game 1" to (GameDomainModel.DisplayColors[0] to shape),
			"Game 2" to (GameDomainModel.DisplayColors[1] to shape),
			"Game 3" to (GameDomainModel.DisplayColors[2] to shape),
			"Game 4" to (GameDomainModel.DisplayColors[3] to shape),
			"Game 5" to (GameDomainModel.DisplayColors[4] to shape),
			"Game 6" to (GameDomainModel.DisplayColors[5] to shape),
			"Game 7" to (GameDomainModel.DisplayColors[6] to shape),
			"Game 8" to (GameDomainModel.DisplayColors[7] to shape),
			"Game 9" to (GameDomainModel.DisplayColors[8] to shape),
			"Game 10" to (GameDomainModel.DisplayColors[9] to shape),
			"Game 11" to (GameDomainModel.DisplayColors[10] to shape),
			"Game 12" to (GameDomainModel.DisplayColors[11] to shape),
			"Game 13" to (GameDomainModel.DisplayColors[12] to shape),
			"Game 14" to (GameDomainModel.DisplayColors[13] to shape),
			"Game 15" to (GameDomainModel.DisplayColors[14] to shape),
			"Game 16" to (GameDomainModel.DisplayColors[15] to shape),
		),
		weekPlays = mapOf(
			"Su" to listOf(
				"Game 1",
				"Game 1",
				"Game 4",
				"Game 14",
				"Game 8",
			),
			"Mo" to listOf("Game 3"),
			"Tu" to listOf(),
			"We" to listOf("Game 5"),
			"Th" to listOf(),
			"Fr" to listOf("Game 16", "Game 16", "Game 9"),
			"Sa" to listOf(
				"Game 14",
				"Game 13",
				"Game 12",
			),
		),
	)
	val NoActivity = Default.copy(
		chartKey = mapOf(),
		weekPlays = mapOf(),
	)
}