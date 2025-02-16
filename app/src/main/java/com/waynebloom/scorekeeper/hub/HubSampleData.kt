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
		dateRange = "2/9 - 2/16",
		chartKey = mapOf(
			"Wingspan" to (Color.Red to shape),
			"Carcassonne" to (Color.Green to shape),
			"Harmonies" to (Color.Blue to shape),
		),
		weekActivity = mapOf(
			"Su" to mapOf(
				"Wingspan" to 2,
				"Carcassonne" to 1,
				"Harmonies" to 3,
			),
			"Mo" to mapOf("Harmonies" to 3),
			"Tu" to mapOf(),
			"We" to mapOf("Wingspan" to 1),
			"Th" to mapOf(),
			"Fr" to mapOf(),
			"Sa" to mapOf("Carcassonne" to 2),
		),
	)
}