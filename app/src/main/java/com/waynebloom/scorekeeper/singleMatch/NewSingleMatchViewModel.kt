package com.waynebloom.scorekeeper.singleMatch

import androidx.lifecycle.ViewModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle

class NewSingleMatchViewModel(
    private val matchEntity: MatchDataModel,
    private val addPlayerCallback: () -> Unit,
    private val saveCallback: (EntityStateBundle<MatchDataModel>) -> Unit
): ViewModel() {

}

private data class SingleMatchViewModelState(
    val notes: String = "",
    val showMaximumPlayersError: Boolean = false
)

data class SingleMatchUiState(
    val notes: String,
    val showMaximumPlayersError: Boolean,
    // TODO: pick up here, converting new viewmodel
)
