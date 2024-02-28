package com.waynebloom.scorekeeper.library

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.GetAdAsFlow
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.LibraryTopBarState
import com.waynebloom.scorekeeper.enums.ListDisplayState
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetGames
import com.waynebloom.scorekeeper.room.domain.usecase.GetGamesAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.InsertEmptyGame
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
class LibraryViewModel @Inject constructor(
    getGamesAsFlow: GetGamesAsFlow,
    private val insertEmptyGame: InsertEmptyGame,
    mutableStateFlowFactory: MutableStateFlowFactory,
    getAdAsFlow: GetAdAsFlow,
): ViewModel() {

    private val viewModelState: MutableStateFlow<LibraryViewModelState>
    val uiState: StateFlow<LibraryUiState>

    init {
        viewModelState = mutableStateFlowFactory.newInstance(LibraryViewModelState())
        uiState = viewModelState
            .map(LibraryViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = viewModelState.value.toUiState())

        viewModelScope.launch {
            getGamesAsFlow().collectLatest { games ->
                viewModelState.update {
                    it.copy(games = games)
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

data class LibraryViewModelState(
    val games: List<GameDomainModel> = listOf(),
    val lazyListState: LazyListState = LazyListState(),
    val searchInput: TextFieldValue = TextFieldValue(),
    val isSearchBarFocused: Boolean = false,
    val ad: NativeAd? = null,
) {

    fun toUiState() = LibraryUiState(
        ad = ad,
        games = filterGamesWithSearchInput(),
        isSearchBarFocused = isSearchBarFocused,
        lazyListState = lazyListState,
        searchInput = searchInput,
    )

    private fun filterGamesWithSearchInput() = games.filter {
        it.name.value.text.lowercase().contains(searchInput.text.lowercase())
    }
}

data class LibraryUiState(
    val games: List<GameDomainModel> = listOf(),
    val lazyListState: LazyListState = LazyListState(),
    val searchInput: TextFieldValue = TextFieldValue(),
    val isSearchBarFocused: Boolean = false,
    val ad: NativeAd? = null,
)
