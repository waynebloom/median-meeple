package com.waynebloom.scorekeeper.network.domain.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.network.data.datasource.MeepleBaseApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGamesFromBase @Inject constructor(
	private val meepleBaseApi: MeepleBaseApi,
) {

	operator fun invoke() = flow {
		emit(meepleBaseApi.getGames())
	}.map { response ->
		val requiredBody = response.body() ?: return@map listOf()
		requiredBody.map { game ->
			GameDomainModel(
				id = game.id.toLong(),
				displayColorIndex = game.color,
				name = TextFieldValue(game.name),
				scoringMode = game.scoringMode.toScoringMode()
			)
		}
	}
}
