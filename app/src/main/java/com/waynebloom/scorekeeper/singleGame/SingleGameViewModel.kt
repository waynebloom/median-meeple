package com.waynebloom.scorekeeper.singleGame

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.GetAdAsFlow
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.isEqualTo
import com.waynebloom.scorekeeper.ext.toStringForDisplay
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetGameWithRelationsAsFlow
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.StatisticsForGameConstants
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.StatisticsForCategory
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.ScoringPlayerDomainModel
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.WinningPlayerDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
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
    getGameWithRelationsAsFlow: GetGameWithRelationsAsFlow,
    mutableStateFlowFactory: MutableStateFlowFactory,
    getAdAsFlow: GetAdAsFlow,
    savedStateHandle: SavedStateHandle,
): ViewModel() {

    private val viewModelState: MutableStateFlow<SingleGameViewModelState>
    val matchesForGameUiState: StateFlow<MatchesForGameUiState>
    val statisticsForGameUiState: StateFlow<StatisticsForGameUiState>

    val gameId = savedStateHandle.get<Long>("gameId")!!

    init {
        viewModelState = mutableStateFlowFactory.newInstance(SingleGameViewModelState())
        matchesForGameUiState = viewModelState
            .map(SingleGameViewModelState::toMatchesForGameUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = viewModelState.value.toMatchesForGameUiState())
        statisticsForGameUiState = viewModelState
            .map(SingleGameViewModelState::toStatisticsForGameUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = viewModelState.value.toStatisticsForGameUiState())

        viewModelScope.launch {
            getGameWithRelationsAsFlow(gameId).collectLatest { latest ->
                if (latest == null) {
                    this.cancel()
                    return@collectLatest
                }

                viewModelState.update {
                    it.copy(
                        loading = false,
                        nameOfGame = latest.name.value.text,
                        primaryColorId = latest.color,
                        matches = latest.matches,
                        categories = latest.categories,
                        scoringMode = latest.scoringMode,
                    )
                }
            }
        }
        viewModelScope.launch {
            getAdAsFlow().collectLatest { latestAd ->
                viewModelState.update {
                    it.copy(ad = latestAd)
                }
            }
        }
    }

    private fun scrollToTop() = viewModelScope.launch {
        delay(DurationMs.long.toLong())
        viewModelState.value
            .matchesLazyListState
            .animateScrollToItem(0)
    }

    // region MatchesForGame

    fun onSearchInputChanged(value: TextFieldValue) = viewModelState.update {
        scrollToTop()
        it.copy(searchInput = value)
    }

    fun onSortButtonClick() = viewModelState.update {
        it.copy(isSortDialogShowing = true)
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

    // region StatisticsForGame

    fun onBestWinnerButtonClick() = viewModelState.update {
        it.copy(isBestWinnerExpanded = !it.isBestWinnerExpanded)
    }

    fun onHighScoreButtonClick() = viewModelState.update {
        it.copy(isHighScoreExpanded = !it.isHighScoreExpanded)
    }

    fun onUniqueWinnersButtonClick() = viewModelState.update {
        it.copy(isUniqueWinnersExpanded = !it.isUniqueWinnersExpanded)
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
    val matches: List<MatchDomainModel> = listOf(),
    val scoringMode: ScoringMode = ScoringMode.Descending,
    // endregion

    // region Statistics
    val isBestWinnerExpanded: Boolean = false,
    val isHighScoreExpanded: Boolean = false,
    val isUniqueWinnersExpanded: Boolean = false,
    val categoryNames: List<String> = listOf(),
    val categories: List<CategoryDomainModel> = listOf(),
    val indexOfSelectedCategory: Int = 0,
    // endregion
) {

    // region Matches Filtering & Sort Logic

    private fun PlayerDomainModel.showWithFilter(filter: String): Boolean {
        val nameMatches = name.text.lowercase().contains(filter.lowercase())
        val totalScoreMatches = filter.toBigDecimalOrNull()?.let {
            totalScore.isEqualTo(it)
        } ?: false

        return nameMatches || totalScoreMatches
    }

    private fun MatchDomainModel.atLeastOnePlayerMatchesFilter(filter: String) =
        players.any { it.showWithFilter(filter) }

    private fun matchMatchesFilters(match: MatchDomainModel): Boolean {
        return if (searchInput.text.isNotEmpty()) {
            match.atLeastOnePlayerMatchesFilter(filter = searchInput.text)
        } else {
            true
        }
    }

    private fun getFilteredMatches(): List<MatchDomainModel> {
        val filteredMatches: MutableList<MatchDomainModel> = mutableListOf()
        val matchesWithNoPlayers: MutableList<MatchDomainModel> = mutableListOf()

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
                match.players.getWinningPlayer(scoringMode).name.text
            }
            MatchSortMode.ByWinningScore -> filteredMatches.sortBy { match ->
                match.players.getWinningPlayer(scoringMode).totalScore
            }
            MatchSortMode.ByPlayerCount -> filteredMatches.sortBy { it.players.size }
        }

        if (sortDirection == SortDirection.Descending)
            filteredMatches.reverse()

        filteredMatches.addAll(matchesWithNoPlayers)

        return filteredMatches
    }

    // endregion

    // region Statistics Generation

    private fun
            getTotalScoreData(matches: List<MatchDomainModel>) = matches
        .flatMap { match ->
            match.players.map { player ->
                ScoringPlayerDomainModel(
                    name = player.name.text,
                    score = player.totalScore
                )
            }
        }

    private fun generateTotalScoreStatistics(
        matches: List<MatchDomainModel>
    ) = StatisticsForCategory(
        category = CategoryDomainModel(
            name = TextFieldValue(""),
            position = 0
        ),
        data = getTotalScoreData(matches)
    )

    private fun MatchDomainModel.getDataForCategory(category: CategoryDomainModel) = players
        .filter { it.useCategorizedScore }
        .map { player ->
            ScoringPlayerDomainModel(
                name = player.name.text,
                score = player.categoryScores[category.position].score
            )
        }

    private fun CategoryDomainModel.getData(matches: List<MatchDomainModel>) = matches
        .flatMap { it.getDataForCategory(this) }

    private fun generateCategoryStatistics(
        categories: List<CategoryDomainModel>,
        matches: List<MatchDomainModel>
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

    private fun getPlayCount(matches: List<MatchDomainModel>) = matches
        .foldRight(initial = 0) { element, sum ->
            sum + element.players.count()
        }

    private fun getUniquePlayerCount(matches: List<MatchDomainModel>) = matches
        .flatMap { it.players }
        .distinctBy { it.name.text }
        .count()

    private fun getWinnersOrderedByNumberOfWins(
        matches: List<MatchDomainModel>,
        scoringMode: ScoringMode
    ): List<WinningPlayerDomainModel> {
        val winners = mutableMapOf<String, Int>()

        matches.forEach {
            if (it.players.isEmpty()) return@forEach
            val winnerName = it.players.getWinningPlayer(scoringMode).name.text
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

        matches.isEmpty() || matches.all { it.players.isEmpty() } -> {
            StatisticsForGameUiState.Empty(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId
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
            val selectedCategory = categoryStatistics[indexOfSelectedCategory]
            val playersWithMostWinsOverflow =
                playersWithMostWins.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
            val playersWithHighScoreOverflow =
                playersWithHighScore.size - StatisticsForGameConstants.numberOfRowsToShowExpanded
            val winnersOverflow =
                winners.size - StatisticsForGameConstants.numberOfRowsToShowExpanded

            StatisticsForGameUiState.Content(
                screenTitle = nameOfGame,
                primaryColorId = primaryColorId,
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
                categoryNames = categoryNames,
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
        val matches: List<MatchDomainModel>,
        val scoringMode: ScoringMode,
    ): MatchesForGameUiState

    data class Loading(
        val loading: Boolean,
        override val screenTitle: String,
        override val primaryColorId: String,
    ): MatchesForGameUiState
}

sealed interface StatisticsForGameUiState {

    val screenTitle: String
    val primaryColorId: String

    data class Loading(
        val loading: Boolean,
        override val screenTitle: String,
        override val primaryColorId: String,
    ): StatisticsForGameUiState

    data class Empty(
        override val screenTitle: String,
        override val primaryColorId: String,
    ): StatisticsForGameUiState

    data class Content(
        override val screenTitle: String,
        override val primaryColorId: String,
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
    ): StatisticsForGameUiState
}
