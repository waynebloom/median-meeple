package com.waynebloom.scorekeeper.viewmodel

import android.content.res.Resources
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.data.model.ScoringStatisticsForCategory
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.ListState
import com.waynebloom.scorekeeper.enums.MatchSortMode
import com.waynebloom.scorekeeper.enums.MatchesForSingleGameTopBarState
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SingleGameScreen
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.isEqualTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SingleGameViewModel(
    gameObject: GameObject,
    resources: Resources,
): ViewModel() {

    companion object {
        const val NumberOfItemsToShowExpanded = 15
    }

    // Recomposition-aware properties

    var matches: List<MatchObject>
    var playerCount: String
    private var scoringMode: ScoringMode
    var screenTitle: String
    var statisticsObjects: List<ScoringStatisticsForCategory>
    var winners: Map<String, Int>

    // State properties

    var selectedTab by mutableStateOf(SingleGameScreen.MatchesForSingleGame)
    var bestWinnerIsExpanded by mutableStateOf(false)
    var highScoreIsExpanded by mutableStateOf(false)
    var uniqueWinnersIsExpanded by mutableStateOf(false)
    var currentCategoryIndex by mutableStateOf(0)

    var isSearchBarFocused by mutableStateOf(false)
    var matchesTopBarState by mutableStateOf(MatchesForSingleGameTopBarState.Default)
    var matchesToDisplay by mutableStateOf(listOf<MatchObject>())
    var matchesLazyListState by mutableStateOf(LazyListState())
    var matchesListState by mutableStateOf(ListState.Default)
    var searchString by mutableStateOf("")
        private set
    var sortDirection by mutableStateOf(SortDirection.Descending)
        private set
    var sortMode by mutableStateOf(MatchSortMode.ByMatchAge)
        private set

    // Other

    private val totalScoreString: String

    // region Initialization and recomposition

    init {

        totalScoreString = resources.getString(R.string.field_total_score)
        screenTitle = gameObject.entity.name

        matches = gameObject.matches
        scoringMode = gameObject.getScoringMode()
        updateDisplayedMatchesAndListState()

        if (matches.isNotEmpty()) {
            playerCount = calculateUniquePlayers(matches).toString()
            statisticsObjects = generateStatisticsObjects(categoryTitles = gameObject.subscoreTitles)
            winners = mapAndOrderWinnersByWins()
        } else {
            playerCount = "0"
            statisticsObjects = listOf()
            winners = mapOf()
        }
    }

    fun onRecompose(gameObject: GameObject) = this.apply {

        screenTitle = gameObject.entity.name

        matches = gameObject.matches
        scoringMode = gameObject.getScoringMode()
        updateDisplayedMatchesAndListState()

        if (matches.isNotEmpty()) {
            playerCount = calculateUniquePlayers(matches).toString()
            statisticsObjects = generateStatisticsObjects(categoryTitles = gameObject.subscoreTitles)
            winners = mapAndOrderWinnersByWins()
        } else {
            playerCount = "0"
            statisticsObjects = listOf()
            winners = mapOf()
        }
    }

    private fun generateStatisticsObjects(categoryTitles: List<CategoryTitleEntity>) =
        listOf(
            ScoringStatisticsForCategory(
                categoryTitle = totalScoreString,
                data = getTotalScoreData()
            )
        ) + categoryTitles.map {
            ScoringStatisticsForCategory(
                categoryTitle = it.title,
                data = getScoreDataByCategoryId(it.id)
            )
        }

    // endregion

    // region Statistics Data Generation

    private fun getTotalScoreData() = matches.flatMap { match ->
        match.players.map { player ->
            Pair(
                first = player.entity.name,
                second = player.entity.score.toBigDecimal()
            )
        }
    }

    private fun getScoreDataByCategoryId(categoryID: Long) =
        matches.flatMap { match ->
            match.players
                .filter { it.entity.showDetailedScore }
                .map { player ->
                    val scoreInCategoryOrZero = player.score.find {
                        it.categoryTitleId == categoryID
                    }?.value?.toBigDecimalOrNull() ?: BigDecimal.ZERO

                    Pair(
                        first = player.entity.name,
                        second = scoreInCategoryOrZero
                    )
                }
        }

    private fun calculateUniquePlayers(matchData: List<MatchObject>) = matchData
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
        match: MatchObject,
        substring: String
    ): Boolean {
        return match.players.any {
            it.entity.name.lowercase().contains(substring.lowercase())
        }
    }

    private fun matchContainsExactScoreMatch(
        match: MatchObject,
        bigDecimalToSearch: BigDecimal?
    ): Boolean {
        return if (bigDecimalToSearch != null) {
            match.players.any {
                it.entity.score.toBigDecimal().isEqualTo(bigDecimalToSearch)
            }
        } else false
    }

    private fun shouldShowMatch(
        match: MatchObject,
        searchString: String
    ): Boolean {
        if (searchString.isEmpty()) return true
        return matchContainsPlayerWithString(match, searchString) ||
                matchContainsExactScoreMatch(match, searchString.toBigDecimalOrNull())
    }

    private fun updateListState(allMatches: List<MatchObject>) {
        matchesListState = when {
            allMatches.isEmpty() -> ListState.ListEmpty
            matchesToDisplay.isEmpty() -> ListState.SearchResultsEmpty
            searchString.isNotBlank() -> ListState.SearchResultsNotEmpty
            else -> ListState.Default
        }
    }

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
     */
    private fun updateDisplayedMatchesAndListState() {
        val allMatches = matches
        val matchesToSort: MutableList<MatchObject> = mutableListOf()
        val emptyMatches: MutableList<MatchObject> = mutableListOf()
        allMatches.forEach {
            if (shouldShowMatch(it, searchString)) {
                if (it.players.isEmpty()) {
                    emptyMatches.add(it)
                } else matchesToSort.add(it)
            }
        }
        var matchesInOrder: List<MatchObject> = when (sortMode) {
            MatchSortMode.ByMatchAge -> matchesToSort.reversed()
            MatchSortMode.ByWinningPlayer -> matchesToSort.sortedBy { match ->
                match.players.getWinningPlayer(scoringMode).entity.name
            }
            MatchSortMode.ByWinningScore -> matchesToSort.sortedBy { match ->
                match.players.getWinningPlayer(scoringMode).entity.score
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

class SingleGameViewModelFactory(
    private val gameObject: GameObject,
    private val resources: Resources,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleGameViewModel(
        gameObject = gameObject,
        resources = resources
    ) as T
}
