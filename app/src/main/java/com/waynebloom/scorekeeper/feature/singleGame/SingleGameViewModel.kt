package com.waynebloom.scorekeeper.feature.singleGame

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.feature.admob.domain.usecase.GetMultipleAdsAsFlow
import com.waynebloom.scorekeeper.ui.constants.DurationMs
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.repository.GameRepository
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.feature.singleGame.matchesForGame.MatchSortMode
import com.waynebloom.scorekeeper.common.ScoringMode
import com.waynebloom.scorekeeper.feature.singleGame.matchesForGame.SortDirection
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.StatisticsForGameConstants
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model.ScoringPlayerDomainModel
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model.StatisticsForCategory
import com.waynebloom.scorekeeper.feature.singleGame.statisticsForGame.domain.model.WinningPlayerDomainModel
import com.waynebloom.scorekeeper.util.ext.getWinningPlayer
import com.waynebloom.scorekeeper.util.ext.toStringForDisplay
import com.waynebloom.scorekeeper.navigation.graph.SingleGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.collections.first
import kotlin.collections.map
import kotlin.collections.takeWhile
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SingleGameViewModel @Inject constructor(
	private val gameRepository: GameRepository,
	private val getMultipleAdsAsFlow: GetMultipleAdsAsFlow,
	mutableStateFlowFactory: MutableStateFlowFactory,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	val gameID = savedStateHandle.toRoute<SingleGame>().gameID
	private val _uiState = mutableStateFlowFactory.newInstance(SingleGameViewModelState())
	val matchesForGameUiState = _uiState
		.map(SingleGameViewModelState::toMatchesForGameUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(),
			initialValue = _uiState.value.toMatchesForGameUiState()
		)
	val statisticsForGameUiState = _uiState
		.map(SingleGameViewModelState::toStatisticsForGameUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(),
			initialValue = _uiState.value.toStatisticsForGameUiState()
		)

	init {
		observeData()
	}

	private fun observeData() {
		viewModelScope.launch(Dispatchers.IO) {
			gameRepository.getOneWithRelations(gameID).collectLatest { latest ->
				if (latest == null) {
					this.cancel(CancellationException("The observed game no longer exists."))
					return@collectLatest
				}

				val categories = latest.categories
					.filterNot { it.name.text == "defaultMiscCategory" }
					.sortedBy { it.position }

				_uiState.update {
					it.copy(
						loading = false,
						nameOfGame = latest.name.text,
						matches = latest.matches,
						categories = categories,
						scoringMode = latest.scoringMode,
					)
				}
			}

			getMultipleAdsAsFlow().collectLatest { ads ->
				_uiState.update {
					it.copy(ads = ads)
				}
			}
		}
	}

	private fun scrollToTop() = viewModelScope.launch {
		delay(DurationMs.LONG.toLong())
		_uiState.value
			.matchesLazyListState
			.animateScrollToItem(0)
	}

	// region MatchesForGame

	fun onSearchInputChanged(value: TextFieldValue) = _uiState.update {
		scrollToTop()
		it.copy(searchValue = value)
	}

	fun onSortButtonClick() = _uiState.update {
		it.copy(isSortDialogShowing = true)
	}

	fun onSortModeChanged(value: MatchSortMode) = _uiState.update {
		scrollToTop()
		it.copy(sortMode = value)
	}

	fun onSortDirectionChanged(value: SortDirection) = _uiState.update {
		scrollToTop()
		it.copy(sortDirection = value)
	}

	fun onSortDialogDismiss() = _uiState.update {
		it.copy(isSortDialogShowing = false)
	}

	// endregion

	// region StatisticsForGame

	fun onBestWinnerButtonClick() = _uiState.update {
		it.copy(isBestWinnerExpanded = !it.isBestWinnerExpanded)
	}

	fun onHighScoreButtonClick() = _uiState.update {
		it.copy(isHighScoreExpanded = !it.isHighScoreExpanded)
	}

	fun onUniqueWinnersButtonClick() = _uiState.update {
		it.copy(isUniqueWinnersExpanded = !it.isUniqueWinnersExpanded)
	}

	fun onCategoryClick(index: Int) = _uiState.update {
		it.copy(indexOfSelectedCategory = index)
	}

	// endregion
}

data class SingleGameViewModelState(
	// region Shared
	val loading: Boolean = true,
	val nameOfGame: String = "",
	val ads: List<NativeAd> = emptyList(),
	// endregion

	// region Matches
	val searchValue: TextFieldValue = TextFieldValue(),
	val isSortDialogShowing: Boolean = false,
	val sortDirection: SortDirection = SortDirection.Descending,
	val sortMode: MatchSortMode = MatchSortMode.ByMatchAge,
	val matchesLazyListState: LazyListState = LazyListState(),
	val matches: List<MatchDomainModel> = listOf(),
	val scoringMode: ScoringMode = ScoringMode.Descending,
	// endregion

	// region Statistics
	val isBestWinnerExpanded: Boolean = false,
	val isHighScoreExpanded: Boolean = false,
	val isUniqueWinnersExpanded: Boolean = false,
	val categories: List<CategoryDomainModel> = listOf(),
	val indexOfSelectedCategory: Int = 0,
	// endregion
) {

	private fun MutableList<MatchDomainModel>.applySearchTerms(): MutableList<MatchDomainModel> {
		val result = mutableListOf<MatchDomainModel>()
		filterTo(result) { match ->
			if (searchValue.text.isNotEmpty()) {
				match.players.any {
					it.name.lowercase().contains(searchValue.text.lowercase())
				}
			} else {
				true
			}
		}
		return result
	}

	private fun MutableList<MatchDomainModel>.applySortingMode(): MutableList<MatchDomainModel> {
		sortWith(getMatchComparator(sortMode, sortDirection))
		return this
	}

	private fun getMatchComparator(
		sortMode: MatchSortMode,
		sortDirection: SortDirection,
	) = when (sortMode) {
		MatchSortMode.ByMatchAge -> compareBy<MatchDomainModel> {
			it.dateMillis
		}

		MatchSortMode.ByWinningPlayer -> compareBy<MatchDomainModel> {
			it.players.getWinningPlayer(scoringMode).name
		}

		MatchSortMode.ByWinningScore -> compareBy<MatchDomainModel> {
			it.players.maxOfOrNull { player ->
				player.categoryScores.sumOf {
					it.scoreAsBigDecimal ?: BigDecimal.ZERO
				}
			} ?: BigDecimal.ZERO
		}

		MatchSortMode.ByPlayerCount -> compareBy<MatchDomainModel> {
			it.players.size
		}
	}.let {
		if (sortDirection == SortDirection.Descending) {
			it.reversed()
		} else it
	}

	// region Statistics Generation

	private fun generateTotalScoreStatistics(matches: List<MatchDomainModel>) = StatisticsForCategory(
		category = CategoryDomainModel(
			name = TextFieldValue(""),
			position = 0
		),
		data = matches
			.flatMap { match ->
				match.players.map { player ->
					ScoringPlayerDomainModel(
						name = player.name,
						score = player.categoryScores.sumOf { it.scoreAsBigDecimal ?: BigDecimal.ZERO }
					)
				}
			}
	)

	private fun generateCategoryStatistics(
		categories: List<CategoryDomainModel>,
		matches: List<MatchDomainModel>
	): List<StatisticsForCategory?> {
		return categories.map { category ->
			val categoryData = matches.flatMap { match ->
				match.players.map { player ->
					ScoringPlayerDomainModel(
						name = player.name,
						score = player
							.categoryScores
							.find { it.categoryID == category.id }
							?.scoreAsBigDecimal
							?: BigDecimal.ZERO
					)
				}
			}

			if (categoryData.isNotEmpty()) {
				StatisticsForCategory(
					category = category,
					data = categoryData
				)
			} else {
				null
			}
		}
	}

	private fun getPlayCount(matches: List<MatchDomainModel>) = matches
		.foldRight(initial = 0) { element, sum ->
			sum + element.players.count()
		}

	private fun getUniquePlayerCount(matches: List<MatchDomainModel>) = matches
		.flatMap { it.players }
		.distinctBy { it.name }
		.count()

	private fun getWinnersOrderedByNumberOfWins(
		matches: List<MatchDomainModel>,
		scoringMode: ScoringMode
	): List<WinningPlayerDomainModel> {
		val winners = mutableMapOf<String, Int>()

		matches.forEach {
			if (it.players.isEmpty()) return@forEach
			val winnerName = it.players.getWinningPlayer(scoringMode).name
			winners[winnerName] = winners[winnerName]?.plus(1) ?: 1
		}

		return winners
			.toList()
			.sortedByDescending { (_, value) -> value }
			.map {
				WinningPlayerDomainModel(
					name = it.first,
					numberOfWins = it.second
				)
			}
	}

	// endregion

	fun toMatchesForGameUiState(): MatchesForGameUiState = when {
		loading -> {
			MatchesForGameUiState.Loading(
				loading = true,
				screenTitle = nameOfGame,
			)
		}

		else -> {
			MatchesForGameUiState.Content(
				ads = ads,
				screenTitle = nameOfGame,
				searchInput = searchValue,
				isSortDialogShowing = isSortDialogShowing,
				sortDirection = sortDirection,
				sortMode = sortMode,
				scoringMode = scoringMode,
				matchesLazyListState = matchesLazyListState,
				matches = matches
					.toMutableList()
					.applySearchTerms()
					.applySortingMode(),
			)
		}
	}

	fun toStatisticsForGameUiState() = when {
		loading -> {
			StatisticsForGameUiState.Loading(
				loading = true,
				screenTitle = "",
			)
		}

		matches.isEmpty() || matches.all { it.players.isEmpty() } -> {
			StatisticsForGameUiState.Empty(
				screenTitle = nameOfGame,
			)
		}

		else -> {
			val totalScoreStatistics = generateTotalScoreStatistics(matches)
			val categoryStatistics = generateCategoryStatistics(categories, matches)
			val playersWithHighScore = totalScoreStatistics
				.dataHighToLow
				.takeWhile {
					it.score == totalScoreStatistics.dataHighToLow.first().score
				}
			val winners = getWinnersOrderedByNumberOfWins(matches, scoringMode)
			val playersWithMostWins = winners
				.takeWhile {
					it.numberOfWins == winners.first().numberOfWins
				}
			val selectedCategory = if (indexOfSelectedCategory == 0) {
				totalScoreStatistics
			} else {
				categoryStatistics[indexOfSelectedCategory - 1]
			}
			val playersWithMostWinsOverflow =
				playersWithMostWins.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
			val playersWithHighScoreOverflow =
				playersWithHighScore.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
			val winnersOverflow =
				winners.size - StatisticsForGameConstants.numberOfRowsToShowExpanded

			StatisticsForGameUiState.Content(
				ads = ads,
				screenTitle = nameOfGame,
				matchCount = matches.count(),
				playCount = getPlayCount(matches),
				uniquePlayerCount = getUniquePlayerCount(matches),
				isBestWinnerExpanded = isBestWinnerExpanded,
				playersWithMostWins = playersWithMostWins,
				playersWithMostWinsOverflow = playersWithMostWinsOverflow,
				isHighScoreExpanded = isHighScoreExpanded,
				playersWithHighScore = playersWithHighScore,
				playersWithHighScoreOverflow = playersWithHighScoreOverflow,
				isUniqueWinnersExpanded = isUniqueWinnersExpanded,
				winners = winners,
				winnersOverflow = winnersOverflow,
				categoryNames = categories.map { it.name.text },
				indexOfSelectedCategory = indexOfSelectedCategory,
				isCategoryDataEmpty = selectedCategory == null,
				categoryTopScorers = selectedCategory?.topScorers ?: listOf(),
				categoryLow = selectedCategory?.low?.toStringForDisplay() ?: "",
				categoryMean = selectedCategory?.mean?.toStringForDisplay() ?: "",
				categoryRange = selectedCategory?.range?.toStringForDisplay() ?: ""
			)
		}
	}
}

sealed interface MatchesForGameUiState {

	val screenTitle: String

	data class Content(
		override val screenTitle: String,
		val ads: List<NativeAd>,
		val searchInput: TextFieldValue,
		val isSortDialogShowing: Boolean,
		val sortDirection: SortDirection,
		val sortMode: MatchSortMode,
		val matchesLazyListState: LazyListState,
		val matches: List<MatchDomainModel>,
		val scoringMode: ScoringMode,
	) : MatchesForGameUiState

	data class Loading(
		val loading: Boolean,
		override val screenTitle: String,
	) : MatchesForGameUiState
}

sealed interface StatisticsForGameUiState {

	val screenTitle: String

	data class Loading(
		val loading: Boolean,
		override val screenTitle: String,
	) : StatisticsForGameUiState

	data class Empty(
		override val screenTitle: String,
	) : StatisticsForGameUiState

	data class Content(
		override val screenTitle: String,
		val ads: List<NativeAd>,
		val matchCount: Int,
		val playCount: Int,
		val uniquePlayerCount: Int,
		val isBestWinnerExpanded: Boolean,
		val playersWithMostWins: List<WinningPlayerDomainModel>,
		val playersWithMostWinsOverflow: Int,
		val isHighScoreExpanded: Boolean,
		val playersWithHighScore: List<ScoringPlayerDomainModel>,
		val playersWithHighScoreOverflow: Int,
		val isUniqueWinnersExpanded: Boolean,
		val winners: List<WinningPlayerDomainModel>,
		val winnersOverflow: Int,
		val categoryNames: List<String>,
		val indexOfSelectedCategory: Int,
		val isCategoryDataEmpty: Boolean,
		val categoryTopScorers: List<ScoringPlayerDomainModel>,
		val categoryLow: String,
		val categoryMean: String,
		val categoryRange: String,
	) : StatisticsForGameUiState
}
