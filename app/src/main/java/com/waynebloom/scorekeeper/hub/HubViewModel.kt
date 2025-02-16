package com.waynebloom.scorekeeper.hub

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatchesByDateRange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Period
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val ONE_WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000L

@HiltViewModel
class HubViewModel @Inject constructor(
	// getGame
	private val getMatchesByDateRange: GetMatchesByDateRange,
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
			val nowMillis = viewModelState.value.today.toEpochSecond() * 1000
			val firstDayMillis = nowMillis - ONE_WEEK_MILLIS

			viewModelState.update {
				it.copy(
					loading = false,
					recentMatches = ,
					quickGames = listOf()
				)
			}
		}
	}

	private fun findPlaysInPeriod(start: ZonedDateTime, period: Period) {
		/**
		 * TODO: implement
		 * 	TIME-ZONE AWARENESS!!
		 * 	derive the DoW in order
		 * 	map each day to its milli range
		 * 	build the result map by checking each match against the day-millis mapping
		 */

viewModelScope.launch(Dispatchers.IO) {
	// TODO: so the LSP shuts up until I update the use-case
		// val recentMatches = getMatchesByDateRange(start, period)
		val recentMatches = listOf<MatchDomainModel>()
		val gameIDs = recentMatches
			.map { it.gameId }
			.distinct()
				.map {
					// grab the game from db
				}

			val weekDisplay = buildMap<String, Int> {
				for (days in 0..6L) {

					// TODO: need to figure out the rounding, so that we compare against 00:00 for each day
					val currentDay = today.minusDays(days)
					val count = recentMatches.count {
						it.dateMillis <= currentDay.toEpochSecond() * 1000
					}

					set(
						key = currentDay.dayOfWeek.name,
						value = 0
					)
				}
			}

			val test = mutableMapOf("Mo" to mutableMapOf("Wingspan" to 6))
			val blank = test["Mo"]!!["Wingspan"]
			test["Mo"]!!["Wingspan"] = 6
		}

	}
}

private data class HubState(
	val loading: Boolean = true,
	val quickGames: List<GameDomainModel> = listOf(),
	val today: ZonedDateTime = ZonedDateTime.now(),
	val recentMatches: List<MatchDataModel> = listOf(),
) {

	fun toUiState(): HubUiState {
		// TODO: implement
		return HubUiState.Content(
			quickGames = quickGames,
			dateRange = dateRange.toString(),
			weekActivity = mapOf(),
			chartKey = mapOf()
		)
	}

	private fun deriveWeeklyPlays(): Map<String, Map<String, Int>> {
	}
}

sealed interface HubUiState {
	data object Loading : HubUiState
	data class Content(
		val quickGames: List<GameDomainModel>,
		val dateRange: String,
		val weekActivity: Map<String, Map<String, Int>>,
		val chartKey: Map<String, Pair<Color, Shape>>,
	) : HubUiState
}