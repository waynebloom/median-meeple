package com.waynebloom.scorekeeper.hub

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetFavoriteGames
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetGames
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatchesByDate
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Period
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import javax.inject.Inject

private const val LENGTH_DAYS = 6

@HiltViewModel
class HubViewModel @Inject constructor(
	private val getFavoriteGames: GetFavoriteGames,
	private val getAllGames: GetGames,
	private val getGame: GetGame,
	private val getMatchesByDate: GetMatchesByDate,
	private val updateGame: UpdateGame,
	// updateGame (need to update data model first)
	mutableStateFlowFactory: MutableStateFlowFactory,
) : ViewModel() {

	private val _uiState = mutableStateFlowFactory.newInstance(HubState())
	val uiState = _uiState
		.onStart { loadData() }
		.map(HubState::toUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = _uiState.value.toUiState()
		)

	private fun loadData() {
		viewModelScope.launch(Dispatchers.IO) {
			// TODO: conciseness
			// 	consider simply using period, since start is derived from it anyway
			val start = ZonedDateTime.now().minusDays(LENGTH_DAYS.toLong())
			val period = Period.ofDays(LENGTH_DAYS)
			val matchesDeferred = async {
				getMatchesByDate(start, period)
			}
			val favoriteGamesDeferred = async {
				getFavoriteGames()
			}
			val matches = matchesDeferred.await()
			val favoriteGames = favoriteGamesDeferred.await()

			if (matches.isNotEmpty()) {
				val games = matches
					.map { it.gameId }
					.distinct()
					.map { getGame(it) }
				val weekPlays = findPlaysInPeriod(
					start = start,
					period = period,
					recentMatches = matches,
					games = games.associate { it.id to it.name.text }
				)
				val chartKey = generateChartKey(games)

				_uiState.update {
					it.copy(
						loading = false,
						quickGames = favoriteGames,
						period = period,
						weekPlays = weekPlays,
						chartKey = chartKey,
					)
				}

				return@launch
			}

			_uiState.update {
				it.copy(
					loading = false,
					quickGames = favoriteGames,
					period = period
				)
			}
		}
	}


	private fun findPlaysInPeriod(
		start: ZonedDateTime,
		period: Period,
		recentMatches: List<MatchDataModel>,
		games: Map<Long, String>,
	): Map<String, List<String>> {

		var remainingMatches = recentMatches
		val realStart = start
			.withHour(23)
			.withMinute(59)
			.withSecond(59)
		val numberOfDays = period.days.toLong()

		return buildMap<String, List<String>> {
			// start [numberOfDays] days ago
			// step thru each day, using 11:59:59 as the target
			// for each day, split the list around the index of the first match after the target time
			// add the result to the map with a key of the first two letters of current day
			// remove those elements from the full list

			for (days in 0..numberOfDays) {
				val cutoffTime = realStart.plusDays(days)
				val dayLabel = cutoffTime.dayOfWeek.name.take(2)

				val results = remainingMatches.fastFilter {
					it.dateMillis < cutoffTime.getLong(ChronoField.INSTANT_SECONDS) * 1000
				}

				this[dayLabel] = results.map {
					games[it.gameId] ?: return@buildMap
				}

				remainingMatches = remainingMatches.minus(results.toSet())
			}
		}
	}

	private fun generateChartKey(games: List<GameDomainModel>) =
		buildMap<String, Pair<Color, Shape>> {
			for (game in games) {
				val display = Pair(
					first = GameDomainModel.DisplayColors[game.displayColorIndex],
					second = RoundedCornerShape(4.dp),
				)

				this[game.name.text] = display
			}
		}

	fun onAddQuickGameClick() {
		if (_uiState.value.allGames == null) {
			viewModelScope.launch(Dispatchers.IO) {
				_uiState.update {
					it.copy(allGames = getAllGames().associateBy { game -> game.id })
				}
			}
		}
	}

	fun addQuickGame(id: Long) {
		_uiState.update {
			val allGames = it.allGames ?: return
			val addedGame = allGames[id] ?: return
			viewModelScope.launch(Dispatchers.IO) {
				updateGame(addedGame.copy(isFavorite = true))
			}
			it.copy(quickGames = it.quickGames.plus(addedGame))
		}
	}
}

private data class HubState(
	val loading: Boolean = true,
	val quickGames: List<GameDomainModel> = listOf(),
	val allGames: Map<Long, GameDomainModel>? = null,
	val period: Period = Period.ZERO,
	val weekPlays: Map<String, List<String>> = mapOf(),
	val chartKey: Map<String, Pair<Color, Shape>> = mapOf(),
) {

	fun toUiState(): HubUiState {
		if (loading) {
			return HubUiState.Loading
		}

		val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
		val dateRange = ZonedDateTime.now().let { now ->
			val firstDate = now
				.minusDays(period.days.toLong())
				.format(formatter)
				.replace('-', '/')
			val secondDate = now
				.format(formatter)
				.replace('-', '/')

			return@let "$firstDate - $secondDate"
		}

		return HubUiState.Content(
			quickGames = quickGames,
			allGames = allGames?.values?.toList(),
			dateRange = dateRange,
			weekPlays = weekPlays,
			chartKey = chartKey,
		)
	}
}

sealed interface HubUiState {
	data object Loading : HubUiState
	data class Content(
		val quickGames: List<GameDomainModel>,
		val allGames: List<GameDomainModel>?,

		// TODO: this is currently unused, maybe remove it?
		val dateRange: String,
		val weekPlays: Map<String, List<String>>,
		val chartKey: Map<String, Pair<Color, Shape>>,
	) : HubUiState
}