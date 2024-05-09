
package com.waynebloom.scorekeeper.viewmodel
/*
import android.content.res.Resources
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.singleGame.statisticsForGame.domain.model.StatisticsForCategory
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.MatchesForSingleGameTopBarState
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.isEqualTo
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class SingleGameViewModel @Inject constructor(
    getGame: GetGame,
    resources: Resources,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    // Recomposition-aware properties

    var matches: List<MatchDataRelationModel> = listOf()
    var playerCount: String
    private var scoringMode: ScoringMode = ScoringMode.Descending
    var screenTitle: String = ""
    var statisticsObjects: List<StatisticsForCategory> = listOf()
    var winners: Map<String, Int>
    var color: String = "DEEP_ORANGE"
    var game = GameDataRelationModel()

    // State properties

    var selectedTab by mutableStateOf(SingleGameScreen.MatchesForSingleGame)
    var bestWinnerIsExpanded by mutableStateOf(false)
    var highScoreIsExpanded by mutableStateOf(false)
    var uniqueWinnersIsExpanded by mutableStateOf(false)
    var currentCategoryIndex by mutableStateOf(0)

    var isSearchBarFocused by mutableStateOf(false)
    var matchesTopBarState by mutableStateOf(MatchesForSingleGameTopBarState.Default)
    var matchesToDisplay by mutableStateOf(listOf<MatchDataRelationModel>())
    var matchesLazyListState by mutableStateOf(LazyListState())
    var matchesListDisplayState by mutableStateOf(ListDisplayState.ShowAll)
    var searchString by mutableStateOf("")
        private set
    var sortDirection by mutableStateOf(SortDirection.Descending)
        private set
    var sortMode by mutableStateOf(MatchSortMode.ByMatchAge)
        private set

    // Other

    private val totalScoreString: String
    private val gameId = savedStateHandle.get<Long>("gameId")!!

    // region Initialization and recomposition

    init {

        viewModelScope.launch {
            val gameUiModel = getGame(gameId)
            game.entity.apply {
                name = gameUiModel.name.value.text
                color = gameUiModel.color
                scoringMode = gameUiModel.scoringMode.ordinal
            }
            onRecompose(game)
            screenTitle = game.entity.name
            matches = game.matches
            scoringMode = game.getScoringMode()
            color = game.entity.color
        }

        totalScoreString = resources.getString(R.string.field_total_score)

        updateDisplayedMatchesAndListState()

        if (matches.isNotEmpty()) {
            playerCount = calculateUniquePlayers(matches).toString()
//            statisticsObjects = generateStatisticsObjects(categoryTitles = gameObject.categories)
            winners = mapAndOrderWinnersByWins()
        } else {
            playerCount = "0"
            statisticsObjects = listOf()
            winners = mapOf()
        }
    }

    fun onRecompose(gameObject: GameDataRelationModel) = this.apply {

        screenTitle = gameObject.entity.name

        matches = gameObject.matches
        scoringMode = gameObject.getScoringMode()
        updateDisplayedMatchesAndListState()

        if (matches.isNotEmpty()) {
            playerCount = calculateUniquePlayers(matches).toString()
            statisticsObjects = generateStatisticsObjects(categoryTitles = gameObject.categories)
            winners = mapAndOrderWinnersByWins()
        } else {
            playerCount = "0"
            statisticsObjects = listOf()
            winners = mapOf()
        }
    }

    private fun generateStatisticsObjects(categoryTitles: List<CategoryDataModel>) =
        listOf(
            StatisticsForCategory(
                categoryTitle = totalScoreString,
                data = getTotalScoreData()
            )
        ) + categoryTitles.map {
            StatisticsForCategory(
                categoryTitle = it.name,
                data = getScoreDataByCategoryId(it.id)
            )
        }

    // endregion

    // region Statistics Data Generation

    private fun getTotalScoreData() = matches.flatMap { match ->
        match.players.map { player ->
            Pair(
                first = player.entity.name,
                second = player.entity.totalScore.toBigDecimal()
            )
        }
    }

    private fun getScoreDataByCategoryId(categoryID: Long) =
        matches.flatMap { match ->
            match.players
                .filter { it.entity.showDetailedScore }
                .map { player ->
                    val scoreInCategoryOrZero = player.score.find {
                        it.categoryId == categoryID
                    }?.value?.toBigDecimalOrNull() ?: BigDecimal.ZERO

                    Pair(
                        first = player.entity.name,
                        second = scoreInCategoryOrZero
                    )
                }
        }

    private fun calculateUniquePlayers(matchData: List<MatchDataRelationModel>) = matchData
        .flatMap { it.players }
        .map { it.entity.name }
        .distinct()
        .size

    private fun mapAndOrderWinnersByWins(): Map<String, Int> {
        val winners = mutableMapOf<String, Int>()

        matches.forEach {
            if (it.players.isEmpty()) return@forEach
            val winnerName = it.players.getWinningPlayer(scoringMode).entity.name

            if (winners.containsKey(winnerName)) {
                winners[winnerName] = winners.getValue(winnerName) + 1
            } else {
                winners[winnerName] = 1
            }
        }

        return winners.toList().sortedByDescending { (_, value) -> value }.toMap()
    }

    // endregion

    // region Filter logic

    private fun matchContainsPlayerWithString(
        match: MatchDataRelationModel,
        substring: String
    ): Boolean {
        return match.players.any {
            it.entity.name.lowercase().contains(substring.lowercase())
        }
    }

    private fun matchContainsExactScoreMatch(
        match: MatchDataRelationModel,
        bigDecimalToSearch: BigDecimal?
    ): Boolean {
        return if (bigDecimalToSearch != null) {
            match.players.any {
                it.entity.totalScore.toBigDecimal().isEqualTo(bigDecimalToSearch)
            }
        } else false
    }

    private fun shouldShowMatch(
        match: MatchDataRelationModel,
        searchString: String
    ): Boolean {
        if (searchString.isEmpty()) return true
        return matchContainsPlayerWithString(match, searchString) ||
                matchContainsExactScoreMatch(match, searchString.toBigDecimalOrNull())
    }

    private fun updateListState(allMatches: List<MatchDataRelationModel>) {
        matchesListDisplayState = when {
            allMatches.isEmpty() -> ListDisplayState.Empty
            matchesToDisplay.isEmpty() -> ListDisplayState.EmptyFiltered
            searchString.isNotBlank() -> ListDisplayState.ShowFiltered
            else -> ListDisplayState.ShowAll
        }
    }

    */
/**
     * This will filter and sort the passed matches based on
     * the current string being searched and the current sort
     * mode.
     *
     * Order of operations:
     *      1. Iterate through the list, adding items that match the search string
     *         to a sublist.
     *          a. If a match has no scores, add it to a separate sublist.
     *      2. Sort the filtered items sublist as designated by the sorting mode.
     *      3. Reverse the order of the list if the user selects descending sort.
     *      4. Add the empty matches sublist to the end of the sorted list.
     *      5. Update state with the sorted list.
     *//*

    private fun updateDisplayedMatchesAndListState() {
        val allMatches = matches
        val matchesToSort: MutableList<MatchDataRelationModel> = mutableListOf()
        val emptyMatches: MutableList<MatchDataRelationModel> = mutableListOf()
        allMatches.forEach {
            if (shouldShowMatch(it, searchString)) {
                if (it.players.isEmpty()) {
                    emptyMatches.add(it)
                } else matchesToSort.add(it)
            }
        }
        var matchesInOrder: List<MatchDataRelationModel> = when (sortMode) {
            MatchSortMode.ByMatchAge -> matchesToSort.reversed()
            MatchSortMode.ByWinningPlayer -> matchesToSort.sortedBy { match ->
                match.players.getWinningPlayer(scoringMode).entity.name
            }
            MatchSortMode.ByWinningScore -> matchesToSort.sortedBy { match ->
                match.players.getWinningPlayer(scoringMode).entity.totalScore
            }
            MatchSortMode.ByPlayerCount -> matchesToSort.sortedBy { it.players.size }
        }

        if (sortDirection == SortDirection.Descending)
            matchesInOrder = matchesInOrder.reversed()

        matchesToDisplay = matchesInOrder.plus(emptyMatches)
        updateListState(allMatches)
    }

    // endregion

    fun clearFilters() {
        searchString = ""
    }

    fun getMostWinsTieDegree(): Int {
        val highestWinCount = winners.values.maxOrNull() ?: 0
        return winners.values.count { it == highestWinCount }
    }

    fun getTotalScoreStatistics() = statisticsObjects.first()

    fun onSearchStringChanged(value: String, coroutineScope: CoroutineScope) =
        coroutineScope.launch {
            searchString = value
            delay(DurationMs.long.toLong())
            if (matchesLazyListState.firstVisibleItemIndex > 0)
                scrollToTop()
        }

    fun onSortModeChanged(value: MatchSortMode, coroutineScope: CoroutineScope) =
        coroutineScope.launch {
            sortMode = value
            delay(DurationMs.long.toLong())
            if (matchesLazyListState.firstVisibleItemIndex > 0)
                scrollToTop()
        }

    fun onSortDirectionChanged(value: SortDirection, coroutineScope: CoroutineScope) =
        coroutineScope.launch {
            sortDirection = value
            delay(DurationMs.long.toLong())
            if (matchesLazyListState.firstVisibleItemIndex > 0)
                scrollToTop()
        }

    private suspend fun scrollToTop() = matchesLazyListState.animateScrollToItem(0)
}

*/
/*class SingleGameViewModelFactory(
    private val gameObject: GameObject,
    private val resources: Resources,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleGameViewModel(
        gameObject = gameObject,
        resources = resources
    ) as T
}*/
