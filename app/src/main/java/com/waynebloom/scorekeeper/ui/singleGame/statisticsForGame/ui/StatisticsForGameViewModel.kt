/*
package com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.di.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.toStringForDisplay
import com.waynebloom.scorekeeper.room.domain.usecase.GetGameWithRelations
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.StatisticsForGameConstants.numberOfRowsToShowExpanded
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.ScoringPlayer
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.StatisticsForCategory
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model.WinningPlayer
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.ScoringPlayerUiModel
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.WinningPlayerUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel TODO: Remove this file
class StatisticsForGameViewModel @Inject constructor(
    getGame: GetGameWithRelations,
    mutableStateFlowFactory: MutableStateFlowFactory,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val viewModelState: MutableStateFlow<StatisticsForGameViewModelState>
    val uiState: StateFlow<StatisticsForGameUiState>

    //    private val gameId = savedStateHandle.get<Long>("gameId")!!
    private val gameId = 1L

    init {

        viewModelState = mutableStateFlowFactory.newInstance(StatisticsForGameViewModelState())
        uiState = viewModelState
            .map(StatisticsForGameViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = viewModelState.value.toUiState()
            )

        viewModelScope.launch {

            val game = getGame(gameId)

            viewModelState.update {

                with(game) {

                    if (matches.isEmpty()) {
                        it.copy(loading = false)
                    } else {
                        val totalScoreStatistics = generateTotalScoreStatistics(matches)
                        val playersWithHighScore = totalScoreStatistics
                            .dataHighToLow
                            .takeWhile {
                                it.score == totalScoreStatistics.dataHighToLow.first().score
                            }
                        val winners = getWinners(matches, scoringMode)
                        val playersWithMostWins = winners
                            .takeWhile {
                                it.numberOfWins == winners.first().numberOfWins
                            }

                        it.copy(
                            loading = false,
                            matchCount = matches.count(),
                            playCount = getPlayCount(matches),
                            playerCount = getPlayerCount(matches),
                            playersWithMostWins = playersWithMostWins,
                            playersWithHighScore = playersWithHighScore,
                            uniqueWinners = winners,
                            totalScoreStatistics = totalScoreStatistics,
                            categoryStatistics = generateCategoryStatistics(categories, matches)
                        )
                    }
                }
            }
        }
    }

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

    private fun getPlayerCount(matches: List<MatchUiModel>) = matches
        .flatMap { it.players }
        .distinctBy { it.name.value.text }
        .count()

    private fun getWinners(
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
}

private data class StatisticsForGameViewModelState(
    val loading: Boolean = true,
    val primaryColorId: String = "",
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
    val categoryStatistics: List<StatisticsForCategory?> = listOf(),
    val totalScoreStatistics: StatisticsForCategory? = null
) {

    fun toUiState() = if (loading) {
        StatisticsForGameUiState.Loading(
            loading = true,
            screenTitle = "",
            primaryColorId = primaryColorId
        )
    } else {

        if (matchCount < 1) {
            StatisticsForGameUiState.Empty("A Game", primaryColorId)
        } else {

            val selectedCategory = categoryStatistics[indexOfSelectedCategory]
            val playersWithMostWinsOverflow = playersWithMostWins.size - numberOfRowsToShowExpanded
            val playersWithHighScoreOverflow = playersWithHighScore.size - numberOfRowsToShowExpanded
            val uniqueWinnersOverflow = uniqueWinners.size - numberOfRowsToShowExpanded

            StatisticsForGameUiState.Content(
                screenTitle = "A Game",
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
}*/
