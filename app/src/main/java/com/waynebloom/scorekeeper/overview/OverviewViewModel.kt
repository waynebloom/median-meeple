package com.waynebloom.scorekeeper.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.GetAdAsFlow
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.overview.OverviewViewModel.Companion.NumberOfGamesToDisplay
import com.waynebloom.scorekeeper.overview.OverviewViewModel.Companion.NumberOfMatchesToDisplay
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetGamesAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatchesAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.InsertEmptyGame
import dagger.hilt.android.lifecycle.HiltViewModel
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
class OverviewViewModel @Inject constructor(
    getGamesAsFlow: GetGamesAsFlow,
    getMatchesAsFlow: GetMatchesAsFlow,
    private val insertEmptyGame: InsertEmptyGame,
    mutableStateFlowFactory: MutableStateFlowFactory,
    getAdAsFlow: GetAdAsFlow,
): ViewModel() {

    private val viewModelState: MutableStateFlow<OverviewViewModelState>
    val uiState: StateFlow<OverviewUiState>

    companion object {
        const val NumberOfGamesToDisplay = 6
        const val NumberOfMatchesToDisplay = 10
    }

    init {
        viewModelState = mutableStateFlowFactory.newInstance(OverviewViewModelState())
        uiState = viewModelState
            .map(OverviewViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = viewModelState.value.toUiState())

        viewModelScope.launch {
            getGamesAsFlow().collectLatest { games ->
                viewModelState.update {
                    it.copy(games = games.take(NumberOfGamesToDisplay))
                }
            }
            getMatchesAsFlow().collectLatest { matches ->
                viewModelState.update {
                    it.copy(matches = matches.take(NumberOfMatchesToDisplay))
                }
            }
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
}

data class OverviewViewModelState(
    val games: List<GameDataRelationModel> = listOf(),
    val matches: List<MatchDataRelationModel> = listOf(),
    val ad: NativeAd? = null,
) {

    fun toUiState() = OverviewUiState(
        games = games.take(NumberOfGamesToDisplay),
        matches = matches.take(NumberOfMatchesToDisplay),
        ad = ad
    )
}

data class OverviewUiState(
    val games: List<GameDataRelationModel>,
    val matches: List<MatchDataRelationModel>,
    val ad: NativeAd? = null,
)
