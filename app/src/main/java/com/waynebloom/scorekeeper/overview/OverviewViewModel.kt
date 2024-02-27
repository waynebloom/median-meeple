package com.waynebloom.scorekeeper.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.ObserveAd
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.navigation.Destination
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.domain.usecase.GetGames
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatches
import com.waynebloom.scorekeeper.room.domain.usecase.InsertEmptyGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    getGames: GetGames,
    getMatches: GetMatches,
    private val insertEmptyGame: InsertEmptyGame,
    mutableStateFlowFactory: MutableStateFlowFactory,
    observeAd: ObserveAd
): ViewModel() {

    private val viewModelState: MutableStateFlow<OverviewUiState>

    val uiState: StateFlow<OverviewUiState>

    // region init

    init {
        OverviewUiState(
            games = listOf(),
            matches = listOf()
        ).let { initialState ->
            viewModelState = mutableStateFlowFactory.newInstance(initialState)
            uiState = viewModelState.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = initialState
            )
        }

        launchInitialState(getGames, getMatches)
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

    private fun launchInitialState(getGames: GetGames, getMatches: GetMatches) =
        viewModelScope.launch {
            viewModelState.update {
                it.copy(
                    games = getGames(),
                    matches = getMatches(),
                    loading = false
                )
            }
        }

    // endregion

    fun addEmptyGame(navController: NavHostController) = viewModelScope.launch {
        val id = insertEmptyGame()
        navController.navigate("${Destination.EditGame.route}/$id")
    }
}

data class OverviewUiState(
    val ad: NativeAd? = null,
    private val games: List<GameDataRelationModel>,
    val loading: Boolean = true,
    private val matches: List<MatchDataRelationModel>
) {

    companion object {
        private const val NumberOfGamesToDisplay = 6
        private const val NumberOfMatchesToDisplay = 10
    }

    val displayedGames: List<GameDataRelationModel>
        get() = games.take(NumberOfGamesToDisplay)

    val displayedMatches: List<MatchDataRelationModel>
        get() = matches
            .sortedByDescending { it.entity.timeModified }
            .take(NumberOfMatchesToDisplay)
}
