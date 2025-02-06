package com.waynebloom.scorekeeper.meepleBase

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.auth.domain.usecase.Login
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.ext.toShortFormatString
import com.waynebloom.scorekeeper.network.domain.usecase.GetGamesFromBase
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MeepleBaseViewModel @Inject constructor(
    private val login: Login,
    private val getGames: GetGamesFromBase,
    mutableStateFlowFactory: MutableStateFlowFactory,
): ViewModel() {

    private val viewModelState: MutableStateFlow<MeepleBaseViewModelState>
    val uiState: StateFlow<MeepleBaseUiState>

    init {
        viewModelState = mutableStateFlowFactory.newInstance(MeepleBaseViewModelState())
        uiState = viewModelState
            .map(MeepleBaseViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = viewModelState.value.toUiState())
    }

    fun onEmailChange(value: TextFieldValue) = viewModelState.update {
        it.copy(email = value)
    }

    fun onPwChange(value: TextFieldValue) = viewModelState.update {
        it.copy(pw = value)
    }

    fun onLoginClick() = viewModelScope.launch {
        val email = viewModelState.value.email.text
        val pw = viewModelState.value.pw.text
        login(email, pw).collect()
    }

    fun onRequestGames() = viewModelScope.launch {
        getGames().collectLatest { games ->
            val gameCards = games.map { game ->

                // NOTE: this is fake data for now
                val highScore = Math
                    .random()
                    .times(50)
                    .plus(50)
                    .roundToInt()
                    .toBigDecimal()

                LibraryGameCard(
                    id = game.id,
                    name = game.name.text,
                    color = GameDomainModel.DisplayColors[game.displayColorIndex],
                    highScore = highScore.toShortFormatString(),
                    noOfMatches = game.matches.size.toString(),
                )
            }
            viewModelState.update {
                it.copy(loading = false, gameCards = gameCards)
            }
        }
    }
}

private data class MeepleBaseViewModelState(
    val loading: Boolean = false,
    val email: TextFieldValue = TextFieldValue(),
    val pw: TextFieldValue = TextFieldValue(),
    val gameCards: List<LibraryGameCard> = listOf(),
    val lazyGridState: LazyStaggeredGridState = LazyStaggeredGridState(),
    val searchInput: TextFieldValue = TextFieldValue(),
    val isSearchBarFocused: Boolean = false,
) {

    fun toUiState() = if (loading) {
        MeepleBaseUiState.Loading
    } else {
        MeepleBaseUiState.Content(
            email = email,
            pw = pw,
            gameCards = filterGamesWithSearchInput(),
            isSearchBarFocused = isSearchBarFocused,
            lazyGridState = lazyGridState,
            searchInput = searchInput,
        )
    }

    private fun filterGamesWithSearchInput() = gameCards.filter {
        it.name.lowercase().contains(searchInput.text.lowercase())
    }
}

sealed interface MeepleBaseUiState {
    data object Loading: MeepleBaseUiState

    data class Content(
        val email: TextFieldValue = TextFieldValue(),
        val pw: TextFieldValue = TextFieldValue(),
        val gameCards: List<LibraryGameCard> = listOf(),
        val lazyGridState: LazyStaggeredGridState = LazyStaggeredGridState(),
        val searchInput: TextFieldValue = TextFieldValue(),
        val isSearchBarFocused: Boolean = false,
    ): MeepleBaseUiState
}

data class LibraryGameCard(
    val id: Long,
    val name: String,
    val color: Color,
    val highScore: String,
    val noOfMatches: String,
)
