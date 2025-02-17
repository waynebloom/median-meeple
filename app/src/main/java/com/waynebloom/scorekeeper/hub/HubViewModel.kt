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
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatchesByDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
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
	private val getGame: GetGame,
	private val getMatchesByDate: GetMatchesByDate,
	// updateGame (need to update data model first)
	mutableStateFlowFactory: MutableStateFlowFactory,

	) : ViewModel() {
	private val viewModelState: MutableStateFlow<HubState>
	val uiState: StateFlow<HubUiState>

	init {
		viewModelState = mutableStateFlowFactory.newInstance(HubState())
		uiState = viewModelState
			.map(HubState::toUiState)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.Eagerly,
				initialValue = viewModelState.value.toUiState()
			)

		viewModelScope.launch {
			// TODO: conciseness
			// 	consider simply using period, since start is derived from it anyway
			val start = ZonedDateTime.now().minusDays(LENGTH_DAYS.toLong())
			val period = Period.ofDays(LENGTH_DAYS)

			getMatchesByDate(start, period).collect { matches ->

				if (matches.isEmpty()) {
					viewModelState.update {
						it.copy(
							loading = false,
							quickGames = listOf(),
							period = period
						)
					}
					return@collect
				}

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

				viewModelState.update {
					it.copy(
						loading = false,
						quickGames = listOf(),
						period = period,
						weekPlays = weekPlays,
						chartKey = chartKey,
					)
				}
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

/*
				if (sortedMatches.isEmpty()) {
					this[dayLabel] = listOf()
					continue
				}

				var splitIndex = sortedMatches.lastIndex
				for (i in 0..splitIndex) {
					if (sortedMatches[i].dateMillis > cutoffTime.getLong(ChronoField.INSTANT_SECONDS) * 1000) {
						splitIndex = i
						break
					}
				}

				if (splitIndex == 0 && sortedMatches.lastIndex != 0) {
					this[dayLabel] = listOf()
					continue
				}

				this[dayLabel] = sortedMatches
					.slice(0..<splitIndex)
					.map {
						games[it.gameId] ?: return@buildMap
					}

				sortedMatches = sortedMatches.slice(splitIndex..sortedMatches.lastIndex)
*/
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
}

private data class HubState(
	val loading: Boolean = true,
	val quickGames: List<GameDomainModel> = listOf(),
	val period: Period = Period.ZERO,
	val weekPlays: Map<String, List<String>> = mapOf(),
	val chartKey: Map<String, Pair<Color, Shape>> = mapOf(),
) {

	fun toUiState(): HubUiState {
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
			dateRange = dateRange,
			weekPlays = weekPlays,
			chartKey = chartKey
		)
	}
}

sealed interface HubUiState {
	data object Loading : HubUiState
	data class Content(
		val quickGames: List<GameDomainModel>,
		val dateRange: String,
		val weekPlays: Map<String, List<String>>,
		val chartKey: Map<String, Pair<Color, Shape>>,
	) : HubUiState
}