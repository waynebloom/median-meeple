package com.waynebloom.scorekeeper.singleMatch

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
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
class SingleMatchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    mutableStateFlowFactory: MutableStateFlowFactory,
    getMatchWithRelationsAsFlow: GetMatchWithRelationsAsFlow,
    getGame: GetGame,
    private val updateMatch: UpdateMatch,
    private val insertMatch: InsertMatch,
    private val deleteMatch: DeleteMatch,
): ViewModel() {

    private val viewModelState: MutableStateFlow<SingleMatchUiState>
    val uiState: StateFlow<SingleMatchUiState>

    val gameId = savedStateHandle.get<Long>("gameId")!!
    var matchId = savedStateHandle.get<Long>("matchId")!!
    private val dataFetchJob: Job

    companion object {
        const val MAXIMUM_PLAYERS = 100
    }

    init {
        viewModelState = mutableStateFlowFactory.newInstance(SingleMatchUiState())
        uiState = viewModelState.stateIn(viewModelScope, SharingStarted.Eagerly, SingleMatchUiState())

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

    fun onSaveClick() = viewModelScope.launch {
        updateMatch(viewModelState.value.match)
    }

    fun onDeleteClick() = viewModelScope.launch {
        dataFetchJob.cancel()
        deleteMatch(matchId)
    }

    fun onNotesChanged(value: TextFieldValue) = viewModelState.update {
        it.copy(notes = value)
    }
}

data class SingleMatchUiState(
    val game: GameDomainModel = GameDomainModel(),
    val match: MatchDomainModel = MatchDomainModel(),
    val notes: TextFieldValue = match.notes.value,
    val isNew: Boolean = false,
)
