package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.ext.statefulUpdateElement

// TODO total and uncategorized score fields don't disable submit button when they are invalid

class EditPlayerScoreViewModel(
    playerObject: PlayerObject,
    private val playerSubscores: MutableList<SubscoreEntity>,
    var subscoreTitles: List<SubscoreTitleEntity>,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
): ViewModel() {

    private var playerEntityNeedsUpdate = false
    private var saveWasTapped = false

    var initialPlayerEntity = playerObject.entity
    var nameState by mutableStateOf(initialPlayerEntity.name)
    var showDetailedScoreState by mutableStateOf(initialPlayerEntity.showDetailedScore)
    var subscoreStateBundles by mutableStateOf(listOf<SubscoreStateBundle>())
    var totalScoreBundle by mutableStateOf(
        SubscoreStateBundle(
            entity = SubscoreEntity(
                value = initialPlayerEntity.score
            )
        )
    )
    var scoreValuesAreValid by mutableStateOf(true)
    var uncategorizedScoreBundle by mutableStateOf(
        SubscoreStateBundle(
            entity = SubscoreEntity(
                value = playerObject.getUncategorizedScoreRemainder()
            )
        )
    )

    // region Initialization

    init {
        subscoreTitles = subscoreTitles.sortedBy { it.position }
        initializeSubscores()
    }

    private fun initializeSubscores() {
        subscoreStateBundles = subscoreTitles
            .map { subscoreTitle ->
                val correspondingSubscore = playerSubscores
                    .find { it.subscoreTitleId == subscoreTitle.id }

                if (correspondingSubscore != null) {
                    SubscoreStateBundle(entity = correspondingSubscore)
                } else {
                    SubscoreStateBundle(
                        entity = SubscoreEntity(
                            subscoreTitleId = subscoreTitle.id,
                            playerId = initialPlayerEntity.id
                        ),
                        databaseAction = DatabaseAction.INSERT
                    )
                }
            }
    }

    // endregion

    private fun checkForInvalidScoreValues(latestInput: String) {
        if (latestInput.toLongOrNull() != null) {
            subscoreStateBundles.forEach {
                if (!it.scoreStringIsValidLong) {
                    scoreValuesAreValid = false
                    return
                }
            }
            scoreValuesAreValid = true
        } else {
            scoreValuesAreValid = false
        }
    }

    private fun getPlayerToCommit(): EntityStateBundle<PlayerEntity> {
        return EntityStateBundle(
            entity = initialPlayerEntity.copy(
                name = nameState,
                showDetailedScore = showDetailedScoreState,
                score = subscoreStateBundles.sumOf { it.entity.value }
                    + uncategorizedScoreBundle.entity.value
            ),
            databaseAction = if (playerEntityNeedsUpdate) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?) {
        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            saveCallback(getPlayerToCommit(), subscoreStateBundles)
        }
    }

    private fun recalculateTotalScore() {
        if (scoreValuesAreValid) {
            val sumOfSubscores = subscoreStateBundles.sumOf { it.entity.value } +
                uncategorizedScoreBundle.entity.value
            totalScoreBundle.setScoreFromLong(sumOfSubscores)
        }
    }

    fun setName(name: String) {
        nameState = name
        playerEntityNeedsUpdate = true
    }

    fun setShowDetailedScore(showDetailedScore: Boolean) {
        showDetailedScoreState = showDetailedScore
        playerEntityNeedsUpdate = true
    }

    fun updateSubscoreStateById(subscoreTitleId: Long, textFieldValue: TextFieldValue) {
        subscoreStateBundles = subscoreStateBundles.statefulUpdateElement(
            predicate = { it.entity.subscoreTitleId == subscoreTitleId },
            update = {
                val textWasChanged = it.textFieldValue.text != textFieldValue.text
                if (textWasChanged) {
                    it.updateDatabaseAction(DatabaseAction.UPDATE)
                    it.setScoreFromTextValue(textFieldValue)
                    playerEntityNeedsUpdate = true
                } else {
                    it.textFieldValue = textFieldValue
                }
            }
        )

        checkForInvalidScoreValues(latestInput = textFieldValue.text)
        recalculateTotalScore()
    }

    fun updateTotalScore(textFieldValue: TextFieldValue) {
        val scoreLong = textFieldValue.text.toLongOrNull()
        if (scoreLong != null) {
            val adjustedUncategorizedScore =
                uncategorizedScoreBundle.entity.value + scoreLong - totalScoreBundle.entity.value
            uncategorizedScoreBundle.setScoreFromLong(adjustedUncategorizedScore)
            playerEntityNeedsUpdate = true
        }
        totalScoreBundle.setScoreFromTextValue(textFieldValue)
    }

    fun updateUncategorizedScoreRemainder(textFieldValue: TextFieldValue) {
        val scoreLong = textFieldValue.text.toLongOrNull()
        if (scoreLong != null) {
            val adjustedTotalScore =
                totalScoreBundle.entity.value + scoreLong - uncategorizedScoreBundle.entity.value
            totalScoreBundle.setScoreFromLong(adjustedTotalScore)
            playerEntityNeedsUpdate = true
        }
        uncategorizedScoreBundle.setScoreFromTextValue(textFieldValue)
    }
}

class EditPlayerScoreViewModelFactory(
    private val playerObject: PlayerObject,
    private val playerSubscores: List<SubscoreEntity>,
    private val subscoreTitles: List<SubscoreTitleEntity>,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditPlayerScoreViewModel(
        playerObject = playerObject,
        playerSubscores = playerSubscores.toMutableList(),
        subscoreTitles = subscoreTitles,
        saveCallback = saveCallback
    ) as T
}