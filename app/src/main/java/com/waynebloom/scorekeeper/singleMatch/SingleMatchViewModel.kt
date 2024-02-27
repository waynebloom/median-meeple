package com.waynebloom.scorekeeper.singleMatch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle
import java.util.*

class SingleMatchViewModel(
    private val matchEntity: MatchDataModel,
    private val addPlayerCallback: () -> Unit,
    private val saveCallback: (EntityStateBundle<MatchDataModel>) -> Unit
): ViewModel() {

    private var matchEntityWasChanged = false
    private var saveWasTapped = false

    var notes: String by mutableStateOf(matchEntity.notes)
    var showMaximumPlayersError by mutableStateOf(false)

    private fun getMatchToCommit() = EntityStateBundle(
        entity = matchEntity.copy(
            notes = notes,
            timeModified = Date().time
        ),
        databaseAction = if (matchEntityWasChanged) {
            DatabaseAction.UPDATE
        } else DatabaseAction.NO_ACTION
    )

    fun onAddPlayerTap(playerCount: Int) {
        if (playerCount < Constants.maximumPlayersInMatch)
            addPlayerCallback()
        else showMaximumPlayersError = !showMaximumPlayersError
    }

    fun onNotesChanged(value: String) {
        matchEntityWasChanged = true
        notes = value
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?, focusManager: FocusManager) {
        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            focusManager.clearFocus(true)
            saveCallback(getMatchToCommit())
        }
    }

    fun shouldShowDetailedScoresButton(
        players: List<PlayerDataModel>,
        subscoreTitles: List<CategoryDataModel>
    ): Boolean {
        val categoriesExist = subscoreTitles.isNotEmpty()
        val detailedScoresExist = players.any { it.showDetailedScore }
        return categoriesExist && detailedScoresExist
    }
}

class SingleMatchViewModelFactory (
    private val matchEntity: MatchDataModel,
    private val addPlayerCallback: () -> Unit,
    private val saveCallback: (EntityStateBundle<MatchDataModel>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleMatchViewModel(
        matchEntity = matchEntity,
        addPlayerCallback = addPlayerCallback,
        saveCallback = saveCallback
    ) as T
}
