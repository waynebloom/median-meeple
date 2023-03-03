package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import java.util.*

class SingleMatchViewModel(
    private val initialMatchEntity: MatchEntity,
    private val saveCallback: (EntityStateBundle<MatchEntity>) -> Unit
): ViewModel() {

    private var matchEntityWasChanged = false
    private var saveWasTapped = false

    var notesState: String by mutableStateOf(initialMatchEntity.matchNotes)

    private fun getMatchToCommit() = EntityStateBundle(
        entity = initialMatchEntity.copy(
            matchNotes = notesState,
            timeModified = Date().time
        ),
        databaseAction = if (matchEntityWasChanged) {
            DatabaseAction.UPDATE
        } else DatabaseAction.NO_ACTION
    )

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?, focusManager: FocusManager) {
        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            focusManager.clearFocus(true)
            saveCallback(getMatchToCommit())
        }
    }

    fun updateNotes(value: String) {
        matchEntityWasChanged = true
        notesState = value
    }
}

class SingleMatchViewModelFactory (
    private val matchEntity: MatchEntity,
    private val saveCallback: (EntityStateBundle<MatchEntity>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = SingleMatchViewModel(
        initialMatchEntity = matchEntity,
        saveCallback = saveCallback
    ) as T
}