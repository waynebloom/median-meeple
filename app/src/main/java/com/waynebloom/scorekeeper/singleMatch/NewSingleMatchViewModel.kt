package com.waynebloom.scorekeeper.singleMatch

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.DeleteMatch
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatchWithRelationsAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.InsertMatch
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateMatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewSingleMatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mutableStateFlowFactory: MutableStateFlowFactory,
    getMatchWithRelationsAsFlow: GetMatchWithRelationsAsFlow,
    getGame: GetGame,
    private val updateMatch: UpdateMatch,
    private val insertMatch: InsertMatch,
    private val deleteMatch: DeleteMatch,
): ViewModel() {

    private val viewModelState: MutableStateFlow<NewSingleMatchUiState>
    val uiState: StateFlow<NewSingleMatchUiState>

    val gameId = savedStateHandle.get<Long>("gameId")!!
    var matchId = savedStateHandle.get<Long>("matchId")!!
    private val dataFetchJob: Job

    companion object {
        const val MAXIMUM_PLAYERS = 100
    }

    init {
        viewModelState = mutableStateFlowFactory.newInstance(NewSingleMatchUiState())
        uiState = viewModelState.stateIn(viewModelScope, SharingStarted.Eagerly, NewSingleMatchUiState())

        dataFetchJob = viewModelScope.launch {

            if (matchId == -1L) {
                matchId = insertMatch(MatchDomainModel(gameId = gameId))
            }

            getMatchWithRelationsAsFlow(matchId).collectLatest { match ->
                viewModelState.update {
                    it.copy(game = getGame(gameId), match = match)
                }
            }
        }
    }

    fun onCellClick(row: Int, col: Int) {

    }

    fun onCellEdit(value: TextFieldValue, row: Int, col: Int) {
        viewModelState.value.scoreMatrix[row][col] = value
        viewModelState.tryEmit(viewModelState.value)
    }

    /*fun onSaveClick() = viewModelScope.launch {
        updateMatch(viewModelState.value.match)
    }

    fun onDeleteClick() = viewModelScope.launch {
        dataFetchJob.cancel()
        deleteMatch(matchId)
    }

    fun onNotesChanged(value: TextFieldValue) {
        // TODO: implement
    }*/
}

data class NewSingleMatchUiState(
    val categories: List<CategoryDomainModel>,
    val players: PlayerDomainModel,
    val scoreMatrix: MutableList<MutableList<TextFieldValue>>,
)
