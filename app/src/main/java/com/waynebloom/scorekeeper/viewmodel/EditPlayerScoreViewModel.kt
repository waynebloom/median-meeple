package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.data.model.EntityStateBundle
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.ext.toTrimmedScoreString
import com.waynebloom.scorekeeper.ext.statefulUpdateElement
import java.math.BigDecimal
import java.math.RoundingMode

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
    var submitButtonEnabled by mutableStateOf(true)
    var uncategorizedScoreBundle by mutableStateOf(SubscoreStateBundle(entity = SubscoreEntity()))

    // region Initialization

    init {
        subscoreTitles = subscoreTitles.sortedBy { it.position }
        initializeSubscores()
        initializeUncategorizedSubscore(playerObject)
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

    private fun initializeUncategorizedSubscore(playerObject: PlayerObject) {
        uncategorizedScoreBundle.apply {
            bigDecimal = playerObject.getUncategorizedScore()
            textFieldValue = TextFieldValue(
                text = playerObject.getUncategorizedScore().toTrimmedScoreString()
            )
        }
    }

    // endregion

    private fun getPlayerToCommit(): EntityStateBundle<PlayerEntity> {
        val subscoreSum = subscoreStateBundles.sumOf { it.bigDecimal }
        val uncategorizedScore = uncategorizedScoreBundle.bigDecimal
        val scoreTotalAsString = (subscoreSum + uncategorizedScore).toTrimmedScoreString()

        return EntityStateBundle(
            entity = initialPlayerEntity.copy(
                name = nameState,
                showDetailedScore = showDetailedScoreState,
                score = scoreTotalAsString
            ),
            databaseAction = if (playerEntityNeedsUpdate) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }
    
    private fun prepareSubscoreEntitiesForCommit() {
        subscoreStateBundles.forEach { 
            it.entity.value = it.bigDecimal.toTrimmedScoreString()
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?) {
        prepareSubscoreEntitiesForCommit()

        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            saveCallback(getPlayerToCommit(), subscoreStateBundles)
        }
    }

    fun onSubscoreFieldUpdate(subscoreTitleId: Long, textFieldValue: TextFieldValue) {
        subscoreStateBundles.statefulUpdateElement(
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

        updateSubmitButtonEnabledState(latestInput = textFieldValue.text)
        recalculateTotalScore()
    }

    fun onTotalScoreFieldUpdate(textFieldValue: TextFieldValue) {
        val scoreBigDecimal = textFieldValue.text.toBigDecimalOrNull()
        val textWasChanged = totalScoreBundle.textFieldValue.text != textFieldValue.text

        if (textWasChanged && scoreBigDecimal != null) {
            val adjustedUncategorizedScore =
                uncategorizedScoreBundle.bigDecimal + scoreBigDecimal - totalScoreBundle.bigDecimal
            uncategorizedScoreBundle.setScoreFromBigDecimal(adjustedUncategorizedScore)
            playerEntityNeedsUpdate = true
        }

        totalScoreBundle.setScoreFromTextValue(textFieldValue)
        updateSubmitButtonEnabledState(latestInput = textFieldValue.text)
    }

    fun onUncategorizedFieldUpdate(textFieldValue: TextFieldValue) {
        val scoreBigDecimal = textFieldValue.text.toBigDecimalOrNull()
        val textWasChanged = uncategorizedScoreBundle.textFieldValue.text != textFieldValue.text

        if (textWasChanged && scoreBigDecimal != null) {
            val adjustedTotalScore =
                totalScoreBundle.bigDecimal + scoreBigDecimal - uncategorizedScoreBundle.bigDecimal
            totalScoreBundle.setScoreFromBigDecimal(adjustedTotalScore)
            playerEntityNeedsUpdate = true
        }

        uncategorizedScoreBundle.setScoreFromTextValue(textFieldValue)
        updateSubmitButtonEnabledState(latestInput = textFieldValue.text)
    }

    private fun recalculateTotalScore() {
        if (submitButtonEnabled) {
            val sumOfSubscores = subscoreStateBundles.sumOf { it.bigDecimal } +
                uncategorizedScoreBundle.bigDecimal
            totalScoreBundle.setScoreFromBigDecimal(sumOfSubscores)
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

    private fun updateSubmitButtonEnabledState(latestInput: String) {
        submitButtonEnabled = if (latestInput.toBigDecimalOrNull() != null) {
            subscoreStateBundles
                .plus(listOf(totalScoreBundle, uncategorizedScoreBundle))
                .all { it.validityState == ScoreStringValidityState.Valid }
        } else false
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