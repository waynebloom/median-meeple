package com.waynebloom.scorekeeper.ui.library

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.ObserveAd
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.di.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.LibraryTopBarState
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetGames
import com.waynebloom.scorekeeper.room.domain.usecase.InsertEmptyGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    getGames: GetGames,
    private val insertEmptyGame: InsertEmptyGame,
    mutableStateFlowFactory: MutableStateFlowFactory,
    observeAd: ObserveAd
): ViewModel() {

    private val viewModelState: MutableStateFlow<LibraryUiState>

    val uiState: StateFlow<LibraryUiState>

    init {
        LibraryUiState().let { initialState ->
            viewModelState = mutableStateFlowFactory.newInstance(initialState)
            uiState = viewModelState.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = initialState
            )
        }

        launchInitialState(getGames)
        launchAdCollection(observeAd)
    }

    private fun launchAdCollection(observeAd: ObserveAd) =
        viewModelScope.launch {
            observeAd().collectLatest { latestAd ->
                viewModelState.update {
                    it.copy(ad = latestAd)
                }
            }
        }

    private fun launchInitialState(getGames: GetGames) =
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    games = getGames(),
                    loading = false
                )
            }
        }

    fun addEmptyGame() = viewModelScope.launch { insertEmptyGame() }

    fun onClearFiltersTap() = viewModelState.update {
        it.copy(searchInput = "")
    }

    fun onSearchBarFocusedChanged(value: Boolean) = viewModelState.update {
        it.copy(isSearchBarFocused = value)
    }

    fun onSearchInputChanged(value: String) = viewModelScope.launch {
        viewModelState.update { it.copy(searchInput = value) }
        delay(DurationMs.long.toLong())
        if (viewModelState.value.lazyListState.firstVisibleItemIndex > 0)
            scrollToTop()
    }


    private suspend fun scrollToTop() =
        viewModelState.value.lazyListState.animateScrollToItem(0)

    fun onTopBarStateChanged(value: LibraryTopBarState) = viewModelState.update {
        it.copy(topBarState = value)
    }
}

data class LibraryUiState(
    val ad: NativeAd? = null,
    private val games: List<GameDataRelationModel> = listOf(),
    val isSearchBarFocused: Boolean = false,
    val lazyListState: LazyListState = LazyListState(),
    val loading: Boolean = true,
    val searchInput: String = "",
    val topBarState: LibraryTopBarState = LibraryTopBarState.Default,
) {

    val displayedGames: List<GameDataRelationModel>
        get() = if (searchInput.isNotBlank()) {
            games.filter { it.entity.name.lowercase().contains(searchInput.lowercase()) }
        } else games

    val listDisplayState: ListDisplayState
        get() = if (displayedGames.isEmpty()) {
            if (searchInput.isBlank()) {
                ListDisplayState.Empty
            } else {
                ListDisplayState.EmptyFiltered
            }
        } else if (searchInput.isNotBlank()) {
            ListDisplayState.ShowFiltered
        } else {
            ListDisplayState.ShowAll
        }
}