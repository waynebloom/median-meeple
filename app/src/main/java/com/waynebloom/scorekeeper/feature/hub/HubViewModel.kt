package com.waynebloom.scorekeeper.feature.hub

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.repository.GameRepository
import com.waynebloom.scorekeeper.database.repository.MatchRepository
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameWithMatchCount
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
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

			matchRepository.getByDate(start, period)
				.transformLatest { matches ->

					val gameIDs = matches
						.map { it.gameID }
						.distinct()

					gameRepository.getMultiple(gameIDs).collectLatest { playedGames ->
						emit(Pair(matches, playedGames))
					}
				}
				.combine(gameRepository.getFavorites()) { matchesAndPlayedGames, favoriteGamesWithCounts ->
					Pair(
						matchesAndPlayedGames,
						favoriteGamesWithCounts
					)
				}
				.collectLatest { (matchesAndPlayedGames, favoriteGamesWithCounts) ->
					val (matches, playedGames) = matchesAndPlayedGames

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
								favoriteGames = favoriteGamesWithCounts,
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
							favoriteGames = favoriteGamesWithCounts,
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

	fun fetchNonFavoriteGamesWithMatchCount() {
		viewModelScope.launch(Dispatchers.IO) {
			val state = _uiState.value
			if (state.nonFavoritesWithMatchCount != null) return@launch

			val favoriteIds = state.favoriteGames.map { it.id }

			gameRepository.getAllWithMatchCount(favoriteIds)
				.collectLatest { gamesWithMatchCount ->
					val nonFavoritesWithMatchCount = gamesWithMatchCount.associateBy { it.game.id }
					_uiState.update {
						it.copy(nonFavoritesWithMatchCount = nonFavoritesWithMatchCount)
					}
				}
		}
	}

	fun addQuickGame(id: Long) {
		val allGames = _uiState.value.nonFavoritesWithMatchCount ?: return
		val addedGame = allGames[id]?.game ?: return
		viewModelScope.launch(Dispatchers.IO) {
			gameRepository.upsert(addedGame.copy(isFavorite = true))
		}
	}

	fun removeQuickGame(game: GameDomainModel) {
		viewModelScope.launch(Dispatchers.IO) {
			gameRepository.upsert(game.copy(isFavorite = false))
		}
	}
}

private data class HubState(
	val loading: Boolean = true,
	val favoriteGames: List<GameDomainModel> = listOf(),
	val nonFavoritesWithMatchCount: Map<Long, GameWithMatchCount>? = null,
	val period: Period = Period.ZERO,
	val weekPlays: Map<String, List<String>> = mapOf(),
	val chartKey: Map<String, Pair<Color, Shape>> = mapOf(),
) {

	fun toUiState(): HubUiState {
		if (loading) {
			return HubUiState.Loading
		}

		return HubUiState.Content(
			favoriteGames = favoriteGames,
			nonFavoritesWithMatchCount = nonFavoritesWithMatchCount?.values?.toList(),
			weekPlays = weekPlays,
			chartKey = chartKey,
		)
	}
}

sealed interface HubUiState {
	data object Loading : HubUiState
	data class Content(
		val favoriteGames: List<GameDomainModel>,
		val nonFavoritesWithMatchCount: List<GameWithMatchCount>?,
		val weekPlays: Map<String, List<String>>,
		val chartKey: Map<String, Pair<Color, Shape>>,
	) : HubUiState
}