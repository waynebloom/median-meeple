package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.enums.GamesTopBarState
import com.waynebloom.scorekeeper.enums.ListState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GamesViewModel: ViewModel() {

    var searchString by mutableStateOf("")
        private set
    var lazyListState by mutableStateOf(LazyListState())
        private set
    var listState by mutableStateOf(ListState.Default)
        private set
    var topBarState by mutableStateOf(GamesTopBarState.Default)
        private set
    var isSearchBarFocused by mutableStateOf(false)
        private set

    fun getGamesToDisplay(games: List<GameEntity>) =
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
            delay(DurationMs.Long.toLong())
            if (lazyListState.firstVisibleItemIndex > 0)
                scrollToTop()
        }
    }

    fun onTopBarStateChanged(value: GamesTopBarState) {
        topBarState = value
    }

    private suspend fun scrollToTop() = lazyListState.animateScrollToItem(0)

    fun updateListState(games: List<GameEntity>) {
        listState = when {
            games.isEmpty() -> ListState.ListEmpty
            getGamesToDisplay(games).isEmpty() -> ListState.SearchResultsEmpty
            searchString.isNotBlank() -> ListState.SearchResultsNotEmpty
            else -> ListState.Default
        }
    }
}

class GamesViewModelFactory: ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = GamesViewModel() as T
}