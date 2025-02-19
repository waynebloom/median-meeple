package com.waynebloom.scorekeeper.hub

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel

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
			"Wingspan" to (Color.Red to shape),
			"Carcassonne" to (Color.Green to shape),
			"Harmonies" to (Color.Blue to shape),
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