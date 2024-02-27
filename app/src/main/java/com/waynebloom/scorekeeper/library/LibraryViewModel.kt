package com.waynebloom.scorekeeper.library

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.ObserveAd
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.LibraryTopBarState
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.navigation.Destination
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

    fun addEmptyGame(navController: NavHostController) = viewModelScope.launch {
        val id = insertEmptyGame()
        navController.navigate("${Destination.EditGame.route}/$id")
    }

    fun onSearchInputChanged(value: TextFieldValue) = viewModelScope.launch {
        viewModelState.update { it.copy(searchInput = value) }
        delay(DurationMs.long.toLong())
        if (viewModelState.value.lazyListState.firstVisibleItemIndex > 0)
            scrollToTop()
    }


    private suspend fun scrollToTop() =
        viewModelState.value.lazyListState.animateScrollToItem(0)
}

/**
 * TODO
 *
 * Make a ViewModelState since there is a loading state
 *
 * Cleanify the data flow (DomainModel vs. DataModel)
 */

data class LibraryUiState(
    val ad: NativeAd? = null,
    private val games: List<GameDataRelationModel> = listOf(),
    val isSearchBarFocused: Boolean = false,
    val lazyListState: LazyListState = LazyListState(),
    val loading: Boolean = true,
    val searchInput: TextFieldValue = TextFieldValue(),
    val topBarState: LibraryTopBarState = LibraryTopBarState.Default,
) {

    // TODO: the match filtering based on search is probably not working, check on it.
    val displayedGames: List<GameDataRelationModel>
        get() = if (searchInput.text.isNotBlank()) {
            games.filter { it.entity.name.lowercase().contains(searchInput.text.lowercase()) }
        } else games

    val listDisplayState: ListDisplayState
        get() = if (displayedGames.isEmpty()) {
            if (searchInput.text.isBlank()) {
                ListDisplayState.Empty
            } else {
                ListDisplayState.EmptyFiltered
            }
        } else if (searchInput.text.isNotBlank()) {
            ListDisplayState.ShowFiltered
        } else {
            ListDisplayState.ShowAll
        }
}
