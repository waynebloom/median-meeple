package com.waynebloom.scorekeeper.hub

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel

internal object HubSampleData {
	val shape = RoundedCornerShape(4.dp)
	val Default = HubUiState.Content(
		quickGames = listOf(
			GameDomainModel(
				name = TextFieldValue("Carcassonne"),
				displayColorIndex = 6,
			),
			GameDomainModel(
				name = TextFieldValue("Harmonies"),
				displayColorIndex = 14,
			),
			GameDomainModel(
				name = TextFieldValue("Wingspan"),
			),
		),
		allGames = null,
		dateRange = "2/9 - 2/16",
		chartKey = mapOf(
			"Wingspan" to (GameDomainModel.DisplayColors[0] to shape),
			"Carcassonne" to (GameDomainModel.DisplayColors[14] to shape),
			"Harmonies" to (GameDomainModel.DisplayColors[7] to shape),
		),
		weekPlays = mapOf(
			"Su" to listOf(
				"Wingspan",
				"Wingspan",
				"Carcassonne",
				"Harmonies",
				"Harmonies",
			),
			"Mo" to listOf("Harmonies"),
			"Tu" to listOf(),
			"We" to listOf("Wingspan"),
			"Th" to listOf(),
			"Fr" to listOf("Harmonies", "Harmonies", "Carcassonne"),
			"Sa" to listOf(
				"Carcassonne",
				"Carcassonne",
				"Carcassonne",
			),
		),
	)
}