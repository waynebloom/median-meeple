/*
package com.waynebloom.scorekeeper.ui.singleGame.matchesForGame

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
import com.waynebloom.scorekeeper.enums.MatchesForGameTopBarState
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.enums.SortDirection
import com.waynebloom.scorekeeper.ext.getWinningPlayer
import com.waynebloom.scorekeeper.ext.isEqualTo
import com.waynebloom.scorekeeper.room.domain.usecase.GetGameWithRelations
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.model.PlayerUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
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

@HiltViewModel TODO: remove this file
class MatchesForGameViewModel @Inject constructor(
    getGame: GetGameWithRelations,
    mutableStateFlowFactory: MutableStateFlowFactory,
    observeAd: ObserveAd,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val viewModelState: MutableStateFlow<MatchesForGameViewModelState>
    val uiState: StateFlow<MatchesForGameUiState>

//    private val gameId = savedStateHandle.get<Long>("gameId")!!
    private val gameId = 1L

    init {

        viewModelState = mutableStateFlowFactory.newInstance(MatchesForGameViewModelState())
        uiState = viewModelState
            .map(MatchesForGameViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = viewModelState.value.toUiState()
            )

        viewModelScope.launch {

            val game = getGame(gameId)

            viewModelState.update {

                it.copy(
                    loading = false,
                    nameOfGame = game.name.value.text,
                    primaryColorId = game.color,
                    matches = game.matches,
                    scoringMode = game.scoringMode
                )
            }
        }

        viewModelScope.launch {
            observeAd().collectLatest { latestAd ->
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

    fun onSearchInputChanged(value: TextFieldValue) = viewModelState.update {
        scrollToTop()
        it.copy(searchInput = value)
    }

    fun onTopBarBackClick() = viewModelState.update {
        it.copy(isSearchFieldFocused = false)
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
}

sealed interface MatchesForGameUiState {

    val screenTitle: String
    val primaryColorId: String

    data class Loading(
        val loading: Boolean,
        override val screenTitle: String,
        override val primaryColorId: String
    ): MatchesForGameUiState

    data class Content(
        override val screenTitle: String,
        override val primaryColorId: String,
        val searchInput: TextFieldValue,
        val isSearchFieldFocused: Boolean,
        val isSortDialogShowing: Boolean,
        val sortDirection: SortDirection,
        val sortMode: MatchSortMode,
        val ad: NativeAd?,
        val matchesLazyListState: LazyListState,
        val matches: List<MatchUiModel>,
        val scoringMode: ScoringMode,
    ): MatchesForGameUiState
}

private data class MatchesForGameViewModelState(
    val loading: Boolean = true,
    val nameOfGame: String = "",
    val primaryColorId: String = "",
    val searchInput: TextFieldValue = TextFieldValue(),
    val isSearchFieldFocused: Boolean = false,
    val isSortDialogShowing: Boolean = false,
    val sortDirection: SortDirection = SortDirection.Descending,
    val sortMode: MatchSortMode = MatchSortMode.ByMatchAge,
    val ad: NativeAd? = null,
    val matchesLazyListState: LazyListState = LazyListState(),
    val matches: List<MatchUiModel> = listOf(),
    val scoringMode: ScoringMode? = null
) {

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

    fun toUiState(): MatchesForGameUiState = if (loading) {
        MatchesForGameUiState.Loading(
            loading = true,
            screenTitle = nameOfGame,
            primaryColorId = primaryColorId
        )
    } else {

        MatchesForGameUiState.Content(
            screenTitle = nameOfGame,
            primaryColorId = primaryColorId,
            searchInput = searchInput,
            isSearchFieldFocused = isSearchFieldFocused,
            isSortDialogShowing = isSortDialogShowing,
            sortDirection = sortDirection,
            sortMode = sortMode,
            ad = ad,
            scoringMode = scoringMode!!,
            matchesLazyListState = matchesLazyListState,
            matches = getFilteredMatches()
        )
    }
}*/
