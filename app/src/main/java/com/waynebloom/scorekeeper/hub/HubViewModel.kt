package com.waynebloom.scorekeeper.hub

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.domain.GameRepository
import com.waynebloom.scorekeeper.database.domain.MatchRepository
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import javax.inject.Inject

private const val LENGTH_DAYS = 6

@HiltViewModel
class HubViewModel @Inject constructor(
	private val gameRepository: GameRepository,
	private val matchRepository: MatchRepository,
	mutableStateFlowFactory: MutableStateFlowFactory,
) : ViewModel() {

	private val _uiState = mutableStateFlowFactory.newInstance(HubState())
	val uiState = _uiState
		.onStart { observeData() }
		.map(HubState::toUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = _uiState.value.toUiState()
		)

	@OptIn(ExperimentalCoroutinesApi::class)
	private fun observeData() {
		viewModelScope.launch(Dispatchers.IO) {
			val period = Period.ofDays(LENGTH_DAYS)
			val start = ZonedDateTime.now().minus(period)

			val favoriteGamesFlow = gameRepository.getFavorites()
				.transformLatest { games ->

					// ASAP: needs to be observable. Too tired to do it right now.
					val gamesWithCounts = games.map {
						val matchCountDef = async {
							matchRepository.getCountByGameID(it.id)
						}

						Pair(it, matchCountDef.await())
					}

					emit(gamesWithCounts)
				}
			matchRepository.getByDate(start, period)
				.transformLatest { matches ->

					val gameIDs = matches
						.map { it.gameID }
						.distinct()

					gameRepository.getMultiple(gameIDs).collectLatest { playedGames ->
						Log.d(this::class.simpleName, "transformLatest -> collectLatest: returned $playedGames")
						emit(Pair(matches, playedGames))
					}
				}
				.combine(favoriteGamesFlow) { matchesAndPlayedGames, favoriteGamesWithCounts ->
					Pair(
						matchesAndPlayedGames,
						favoriteGamesWithCounts
					)
				}
				.collectLatest { (matchesAndPlayedGames, favoriteGamesWithCounts) ->
					val (matches, playedGames) = matchesAndPlayedGames
					Log.d(this::class.simpleName, "final collectLatest: returned \nmatches: $matches\nplayedGames: $playedGames\nfavoriteGames: $favoriteGamesWithCounts")

					if (matches.isNotEmpty()) {
						val weekPlays = findPlaysInPeriod(
							start = start,
							recentMatches = matches,
							games = playedGames.associate { it.id to it.name.text }
						)
						val chartKey = generateChartKey(playedGames)

						_uiState.update {
							it.copy(
								loading = false,
								quickGames = favoriteGamesWithCounts,
								period = period,
								weekPlays = weekPlays,
								chartKey = chartKey,
							)
						}

						return@collectLatest
					}

					_uiState.update {
						it.copy(
							loading = false,
							quickGames = favoriteGamesWithCounts,
							period = period
						)
					}
				}
		}
	}

	private fun findPlaysInPeriod(
		start: ZonedDateTime,
		recentMatches: List<MatchDomainModel>,
		games: Map<Long, String>,
	): Map<String, List<String>> {

		var remainingMatches = recentMatches
		val realStart = start
			.withHour(23)
			.withMinute(59)
			.withSecond(59)

		return buildMap<String, List<String>> {

			for (days in 0L..LENGTH_DAYS) {
				val cutoffTime = realStart.plusDays(days)
				val dayLabel = cutoffTime.dayOfWeek.name
					.take(3)
					.lowercase()
					.replaceFirstChar { if (it.isLowerCase()) it.titlecaseChar() else it }

				val results = remainingMatches.fastFilter {
					it.dateMillis < cutoffTime.getLong(ChronoField.INSTANT_SECONDS) * 1000
				}

				this[dayLabel] = results.map {
					games[it.gameID] ?: return@buildMap
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

	fun fetchAllGames() {
		if (_uiState.value.allGames == null) {
			viewModelScope.launch(Dispatchers.IO) {
				gameRepository.getAll().collectLatest { games ->
					_uiState.update { state ->
						state.copy(allGames = games.associateBy { it.id })
					}
				}
			}
		}
	}

	fun addQuickGame(id: Long) {
		val allGames = _uiState.value.allGames ?: return
		val addedGame = allGames[id] ?: return
		viewModelScope.launch(Dispatchers.IO) {
			gameRepository.upsert(addedGame.copy(isFavorite = true))
		}
	}
}

private data class HubState(
	val loading: Boolean = true,
	val quickGames: List<Pair<GameDomainModel, Int>> = listOf(),
	val allGames: Map<Long, GameDomainModel>? = null,
	val period: Period = Period.ZERO,
	val weekPlays: Map<String, List<String>> = mapOf(),
	val chartKey: Map<String, Pair<Color, Shape>> = mapOf(),
) {

	fun toUiState(): HubUiState {
		if (loading) {
			return HubUiState.Loading
		}

		return HubUiState.Content(
			quickGames = quickGames,
			allGames = allGames?.values?.toList(),
			weekPlays = weekPlays,
			chartKey = chartKey,
		)
	}
}

sealed interface HubUiState {
	data object Loading : HubUiState
	data class Content(
		val quickGames: List<Pair<GameDomainModel, Int>>,
		val allGames: List<GameDomainModel>?,
		val weekPlays: Map<String, List<String>>,
		val chartKey: Map<String, Pair<Color, Shape>>,
	) : HubUiState
}