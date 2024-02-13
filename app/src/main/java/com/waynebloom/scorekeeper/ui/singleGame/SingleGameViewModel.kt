package com.waynebloom.scorekeeper.ui.singleGame

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.ObserveAd
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.di.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.isEqualTo
import com.waynebloom.scorekeeper.ext.toStringForDisplay
import com.waynebloom.scorekeeper.room.domain.usecase.GetGameWithRelations
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.GameUiModel
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.model.PlayerUiModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.StatisticsForGameConstants
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.ScoringPlayer
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.StatisticsForCategory
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.WinningPlayer
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.ScoringPlayerUiModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.WinningPlayerUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleGameViewModel @Inject constructor(
    getGame: GetGameWithRelations,
    mutableStateFlowFactory: MutableStateFlowFactory,
    observeAd: ObserveAd,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val viewModelState: MutableStateFlow<SingleGameViewModelState>
    val matchesForGameUiState: StateFlow<MatchesForGameUiState>
    val statisticsForGameUiState: StateFlow<StatisticsForGameUiState>

    private val gameId = savedStateHandle.get<Long>("gameId")!!

    init {
        viewModelState = mutableStateFlowFactory.newInstance(SingleGameViewModelState())
        matchesForGameUiState = viewModelState
            .map(SingleGameViewModelState::toMatchesForGameUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = viewModelState.value.toMatchesForGameUiState()
            )
        statisticsForGameUiState = viewModelState
            .map(SingleGameViewModelState::toStatisticsForGameUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = viewModelState.value.toStatisticsForGameUiState()
            )

        viewModelScope.launch { createInitialState(game = getGame(gameId)) }
        viewModelScope.launch {
            observeAd().collectLatest { latestAd ->
                viewModelState.update {
                    it.copy(ad = latestAd)
                }
            }
        }
    }

    private fun createInitialState(game: GameUiModel) = with(game) {
        viewModelState.update { state ->

            if (matches.isEmpty()) {
                state.copy(
                    loading = false,
                    nameOfGame = name.value.text,
                    primaryColorId = color
                )
            } else {
                val totalScoreStatistics = generateTotalScoreStatistics(matches)
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

                state.copy(
                    loading = false,
                    nameOfGame = name.value.text,
                    primaryColorId = color,
                    matches = matches,
                    scoringMode = scoringMode,
                    matchCount = matches.count(),
                    playCount = getPlayCount(matches),
                    playerCount = getUniquePlayerCount(matches),
                    playersWithMostWins = playersWithMostWins,
                    playersWithHighScore = playersWithHighScore,
                    uniqueWinners = winners,
                    totalScoreStatistics = totalScoreStatistics,
                    categoryStatistics = generateCategoryStatistics(categories, matches)
                )
            }
        }
    }

    private fun scrollToTop() = viewModelScope.launch {
        delay(DurationMs.long.toLong())
        viewModelState.value
            .matchesLazyListState
            .animateScrollToItem(0)
    }

    // region Statistics generation

    private fun getTotalScoreData(matches: List<MatchUiModel>) = matches
        .flatMap { match ->
            match.players.map { player ->
                ScoringPlayer(
                    name = player.name.value.text,
                    score = player.totalScore
                )
            }
        }

    private fun generateTotalScoreStatistics(
        matches: List<MatchUiModel>
    ) = StatisticsForCategory(
        category = CategoryUiModel(
            name = TextFieldInput(),
            position = 0
        ),
        data = getTotalScoreData(matches)
    )

    private fun MatchUiModel.getDataForCategory(category: CategoryUiModel) = players
        .filter { it.showDetailedScore }
        .map { player ->
            ScoringPlayer(
                name = player.name.value.text,
                score = player.categoryScores[category.position].score
            )
        }

    private fun CategoryUiModel.getData(matches: List<MatchUiModel>) = matches
        .flatMap { it.getDataForCategory(this) }

    private fun generateCategoryStatistics(
        categories: List<CategoryUiModel>,
        matches: List<MatchUiModel>
    ) = categories
        .map {

            val categoryData = it.getData(matches)

            if (categoryData.isNotEmpty()) {
                StatisticsForCategory(
                    category = it,
                    data = it.getData(matches)
                )
            } else {
                null
            }
        }

    private fun getPlayCount(matches: List<MatchUiModel>) = matches
        .foldRight(initial = 0) { element, sum ->
            sum + element.players.count()
        }

    private fun getUniquePlayerCount(matches: List<MatchUiModel>) = matches
        .flatMap { it.players }
        .distinctBy { it.name.value.text }
        .count()

    private fun getWinnersOrderedByNumberOfWins(
        matches: List<MatchUiModel>,
        scoringMode: ScoringMode
    ): List<WinningPlayer> {
        val winners = mutableMapOf<String, Int>()

        matches.forEach {
            if (it.players.isEmpty()) return@forEach
            val winnerName = it.players.getWinningPlayer(scoringMode).name.value.text
            winners[winnerName] = winners[winnerName]?.plus(1) ?: 1
        }

        return winners
            .toList()
            .sortedByDescending { (_, value) -> value }
            .map {
                WinningPlayer(
                    name = it.first,
                    numberOfWins = it.second
                )
            }
    }

    // endregion

    // region Matches Ui State

    fun onSearchInputChanged(value: TextFieldValue) = viewModelState.update {
        scrollToTop()
        it.copy(searchInput = value)
    }

    fun onSortButtonClick() = viewModelState.update {
        it.copy()
    }

    fun onSortModeChanged(value: MatchSortMode) = viewModelState.update {
        scrollToTop()
        it.copy(sortMode = value)
    }

    fun onSortDirectionChanged(value: SortDirection) = viewModelState.update {
        scrollToTop()
        it.copy(sortDirection = value)
    }

    fun onSortDialogDismiss() = viewModelState.update {
        it.copy(isSortDialogShowing = false)
    }

    // endregion

    // region Statistics Ui State

    fun onBestWinnerButtonClick() = viewModelState.update {
        it.copy(isBestWinnerExpanded = !it.isBestWinnerExpanded)
    }

    fun onHighScoreButtonClick() = viewModelState.update {
        it.copy(isHighScoreExpanded = !it.isHighScoreExpanded)
    }

    fun onUniqueWinnersButtonClick() = viewModelState.update {
        it.copy(isUniqueWinnersExpanded = !it.isBestWinnerExpanded)
    }

    fun onCategoryClick(index: Int) = viewModelState.update {
        it.copy(indexOfSelectedCategory = index)
    }

    // endregion
}

private data class SingleGameViewModelState(
    // region Shared
    val loading: Boolean = true,
    val nameOfGame: String = "",
    val primaryColorId: String = "",
    // endregion

    // region Matches
    val searchInput: TextFieldValue = TextFieldValue(),
    val isSortDialogShowing: Boolean = false,
    val sortDirection: SortDirection = SortDirection.Descending,
    val sortMode: MatchSortMode = MatchSortMode.ByMatchAge,
    val ad: NativeAd? = null,
    val matchesLazyListState: LazyListState = LazyListState(),
    val matches: List<MatchUiModel> = listOf(),
    val scoringMode: ScoringMode = ScoringMode.Descending,
    // endregion

    // region Statistics
    val matchCount: Int = 0,
    val playCount: Int = 0,
    val playerCount: Int = 0,
    val isBestWinnerExpanded: Boolean = false,
    val playersWithMostWins: List<WinningPlayer> = listOf(),
    val isHighScoreExpanded: Boolean = false,
    val playersWithHighScore: List<ScoringPlayer> = listOf(),
    val isUniqueWinnersExpanded: Boolean = false,
    val uniqueWinners: List<WinningPlayer> = listOf(),
    val categoryNames: List<String> = listOf(),
    val indexOfSelectedCategory: Int = 0,
    val totalScoreStatistics: StatisticsForCategory? = null,
    val categoryStatistics: List<StatisticsForCategory?> = listOf()
    // endregion
) {

    // region Matches Filtering & Sort Logic

    private fun PlayerUiModel.matchesFilter(filter: String): Boolean {
        val nameMatches = name.value.text.lowercase().contains(filter.lowercase())
        val totalScoreMatches = filter.toBigDecimalOrNull()?.let {
            totalScore.isEqualTo(it)
        } ?: false

        return nameMatches || totalScoreMatches
    }

    private fun MatchUiModel.atLeastOnePlayerMatchesFilter(filter: String) =
        players.any { it.matchesFilter(filter) }

    private fun matchMatchesFilters(match: MatchUiModel): Boolean {
        return if (searchInput.text.isNotEmpty()) {
            match.atLeastOnePlayerMatchesFilter(filter = searchInput.text)
        } else {
            true
        }
    }

    private fun getFilteredMatches(): List<MatchUiModel> {
        val filteredMatches: MutableList<MatchUiModel> = mutableListOf()
        val matchesWithNoPlayers: MutableList<MatchUiModel> = mutableListOf()

        matches.forEach {
            if (matchMatchesFilters(it)) {
                if (it.players.isEmpty()) {
                    matchesWithNoPlayers.add(it)
                } else {
                    filteredMatches.add(it)
                }
            }
        }

        when (sortMode) {
            MatchSortMode.ByMatchAge -> filteredMatches.reverse()
            MatchSortMode.ByWinningPlayer -> filteredMatches.sortBy { match ->
                match.players.getWinningPlayer(scoringMode!!).name.value.text
            }
            MatchSortMode.ByWinningScore -> filteredMatches.sortBy { match ->
                match.players.getWinningPlayer(scoringMode!!).totalScore
            }
            MatchSortMode.ByPlayerCount -> filteredMatches.sortBy { it.players.size }
        }

        if (sortDirection == SortDirection.Descending)
            filteredMatches.reverse()

        filteredMatches.addAll(matchesWithNoPlayers)

        return filteredMatches
    }

    // endregion

    fun toMatchesForGameUiState(): MatchesForGameUiState = when {
        loading -> {
            MatchesForGameUiState.Loading(
                loading = true,
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId
            )
        }
        matches.isEmpty() -> {
            MatchesForGameUiState.Empty(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId
            )
        }
        else -> {
            MatchesForGameUiState.Content(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId,
                searchInput = searchInput,
                isSortDialogShowing = isSortDialogShowing,
                sortDirection = sortDirection,
                sortMode = sortMode,
                ad = ad,
                scoringMode = scoringMode,
                matchesLazyListState = matchesLazyListState,
                matches = getFilteredMatches()
            )
        }
    }

    fun toStatisticsForGameUiState() = when {
        loading -> {
            StatisticsForGameUiState.Loading(
                loading = true,
                screenTitle = "",
                primaryColorId = primaryColorId
            )
        }
        matches.isEmpty() -> {
            StatisticsForGameUiState.Empty(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId
            )
        }
        else -> {
            val selectedCategory = categoryStatistics[indexOfSelectedCategory]
            val playersWithMostWinsOverflow =
                playersWithMostWins.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
            val playersWithHighScoreOverflow =
                playersWithHighScore.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
            val uniqueWinnersOverflow =
                uniqueWinners.size - StatisticsForGameConstants.numberOfRowsToShowExpanded

            StatisticsForGameUiState.Content(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId,
                matchCount = matchCount.toString(),
                playCount = playCount.toString(),
                uniquePlayerCount = playerCount.toString(),
                isBestWinnerExpanded = isBestWinnerExpanded,
                playersWithMostWins = playersWithMostWins.map(WinningPlayer::toUiModel),
                playersWithMostWinsOverflow = playersWithMostWinsOverflow,
                isHighScoreExpanded = isHighScoreExpanded,
                playersWithHighScore = playersWithHighScore.map(ScoringPlayer::toUiModel),
                playersWithHighScoreOverflow = playersWithHighScoreOverflow,
                isUniqueWinnersExpanded = isUniqueWinnersExpanded,
                uniqueWinners = uniqueWinners.map(WinningPlayer::toUiModel),
                uniqueWinnersOverflow = uniqueWinnersOverflow,
                categoryNames = categoryNames,
                indexOfSelectedCategory = indexOfSelectedCategory,
                isCategoryDataEmpty = selectedCategory == null,
                categoryTopScorers = selectedCategory?.topScorers?.map(ScoringPlayer::toUiModel) ?: listOf(),
                categoryLow = selectedCategory?.low?.toStringForDisplay() ?: "",
                categoryMean = selectedCategory?.mean?.toStringForDisplay() ?: "",
                categoryRange = selectedCategory?.range?.toStringForDisplay() ?: ""
            )
        }
    }
}

sealed interface MatchesForGameUiState {

    val screenTitle: String
    val primaryColorId: String

    data class Content(
        override val screenTitle: String,
        override val primaryColorId: String,
        val searchInput: TextFieldValue,
        val isSortDialogShowing: Boolean,
        val sortDirection: SortDirection,
        val sortMode: MatchSortMode,
        val ad: NativeAd?,
        val matchesLazyListState: LazyListState,
        val matches: List<MatchUiModel>,
        val scoringMode: ScoringMode,
    ): MatchesForGameUiState

    data class Empty(
        override val screenTitle: String,
        override val primaryColorId: String
    ): MatchesForGameUiState

    data class Loading(
        val loading: Boolean,
        override val screenTitle: String,
        override val primaryColorId: String
    ): MatchesForGameUiState
}

sealed interface StatisticsForGameUiState {

    val screenTitle: String
    val primaryColorId: String

    data class Loading(
        val loading: Boolean,
        override val screenTitle: String,
        override val primaryColorId: String
    ): StatisticsForGameUiState

    data class Empty(
        override val screenTitle: String,
        override val primaryColorId: String
    ): StatisticsForGameUiState

    data class Content(
        override val screenTitle: String,
        override val primaryColorId: String,
        val matchCount: String,
        val playCount: String,
        val uniquePlayerCount: String,
        val isBestWinnerExpanded: Boolean,
        val playersWithMostWins: List<WinningPlayerUiModel>,
        val playersWithMostWinsOverflow: Int,
        val isHighScoreExpanded: Boolean,
        val playersWithHighScore: List<ScoringPlayerUiModel>,
        val playersWithHighScoreOverflow: Int,
        val isUniqueWinnersExpanded: Boolean,
        val uniqueWinners: List<WinningPlayerUiModel>,
        val uniqueWinnersOverflow: Int,
        val categoryNames: List<String>,
        val indexOfSelectedCategory: Int,
        val isCategoryDataEmpty: Boolean,
        val categoryTopScorers: List<ScoringPlayerUiModel>,
        val categoryLow: String,
        val categoryMean: String,
        val categoryRange: String
    ): StatisticsForGameUiState
}