package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.enums.LibraryTopBarState
import com.waynebloom.scorekeeper.enums.ListDisplayState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GamesViewModel: ViewModel() {

    var searchString by mutableStateOf("")
        private set
    var lazyListState by mutableStateOf(LazyListState())
        private set
    var listDisplayState by mutableStateOf(ListDisplayState.ShowAll)
        private set
    var topBarState by mutableStateOf(LibraryTopBarState.Default)
        private set
    var isSearchBarFocused by mutableStateOf(false)
        private set

    fun getGamesToDisplay(games: List<GameDataModel>) =
        games.filter { it.name.lowercase().contains(searchString.lowercase()) }

    fun onClearFiltersTap() {
        searchString = ""
    }

    fun onSearchBarFocusedChanged(value: Boolean) {
        isSearchBarFocused = value
    }

    fun onSearchStringChanged(value: String, coroutineScope: CoroutineScope) {
        coroutineScope.launch {
            searchString = value
            delay(DurationMs.long.toLong())
            if (lazyListState.firstVisibleItemIndex > 0)
                scrollToTop()
        }
    }

    fun onTopBarStateChanged(value: LibraryTopBarState) {
        topBarState = value
    }

    private suspend fun scrollToTop() = lazyListState.animateScrollToItem(0)

    fun updateListState(games: List<GameDataModel>) {
        listDisplayState = when {
            games.isEmpty() -> ListDisplayState.Empty
            getGamesToDisplay(games).isEmpty() -> ListDisplayState.EmptyFiltered
            searchString.isNotBlank() -> ListDisplayState.ShowFiltered
            else -> ListDisplayState.ShowAll
        }
    }
}

class GamesViewModelFactory: ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GamesViewModel() as T
}
