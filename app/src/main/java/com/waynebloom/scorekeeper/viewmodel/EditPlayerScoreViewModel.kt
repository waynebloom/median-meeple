package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.data.model.EntityStateBundle
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.ext.toTrimmedScoreString
import com.waynebloom.scorekeeper.ext.statefulUpdateElement

class EditPlayerScoreViewModel(
    playerObject: PlayerObject,
    matchObject: MatchObject,
    private val playerSubscores: MutableList<SubscoreEntity>,
    var subscoreTitles: List<SubscoreTitleEntity>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
): ViewModel() {

    private var playerEntityNeedsUpdate = false
    private var saveWasTapped = false

    var initialPlayerEntity = playerObject.entity
    var playerRankTextFieldValue by mutableStateOf(TextFieldValue())
    var playerRankIsValid by mutableStateOf(true)
    var nameTextFieldValue by mutableStateOf(TextFieldValue(initialPlayerEntity.name))
    var showDetailedScoreState by mutableStateOf(initialPlayerEntity.showDetailedScore)
    var subscoreStateBundles by mutableStateOf(listOf<SubscoreStateBundle>())
    var totalScoreBundle by mutableStateOf(
        SubscoreStateBundle(
            entity = SubscoreEntity(
                value = initialPlayerEntity.score
            )
        )
    )
    var subscoresAreValid by mutableStateOf(true)
    var nameIsValid by mutableStateOf(nameTextFieldValue.text.isNotBlank())
    var uncategorizedScoreBundle by mutableStateOf(SubscoreStateBundle(entity = SubscoreEntity()))

    // region Initialization

    init {
        subscoreTitles = subscoreTitles.sortedBy { it.position }
        initializeRank(
            playerCount = matchObject.players.size,
            currentPlayerRankValue = playerObject.entity.position
        )
        initializeSubscores()
        initializeUncategorizedSubscore(playerObject)
    }

    private fun initializeRank(playerCount: Int, currentPlayerRankValue: Int) {
        playerRankTextFieldValue = if (isGameManualRanked && currentPlayerRankValue == 0) {
            playerEntityNeedsUpdate = true
            TextFieldValue(playerCount.toString())
        } else TextFieldValue(currentPlayerRankValue.toString())
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
                name = nameTextFieldValue.text,
                position = playerRankTextFieldValue.text.toInt(),
                score = scoreTotalAsString,
                showDetailedScore = showDetailedScoreState,
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

    fun onPlayerRankUpdate(textFieldValue: TextFieldValue) {
        playerRankTextFieldValue = textFieldValue
        playerRankIsValid = textFieldValue.text.isNotEmpty()
            && textFieldValue.text.isDigitsOnly()
            && textFieldValue.text.toInt() <= 100
            && textFieldValue.text.toInt() > 0
        playerEntityNeedsUpdate = true
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

        updateSubscoresAreValidState(latestInput = textFieldValue.text)
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
        updateSubscoresAreValidState(latestInput = textFieldValue.text)
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
        updateSubscoresAreValidState(latestInput = textFieldValue.text)
    }

    private fun recalculateTotalScore() {
        if (subscoresAreValid) {
            val sumOfSubscores = subscoreStateBundles.sumOf { it.bigDecimal } +
                uncategorizedScoreBundle.bigDecimal
            totalScoreBundle.setScoreFromBigDecimal(sumOfSubscores)
        }
    }

    fun setName(name: TextFieldValue) {
        nameIsValid = name.text.isNotBlank()
        nameTextFieldValue = name
        playerEntityNeedsUpdate = true
    }

    fun setShowDetailedScore(showDetailedScore: Boolean) {
        showDetailedScoreState = showDetailedScore
        playerEntityNeedsUpdate = true
    }

    fun isSubmitButtonEnabled() = subscoresAreValid && nameIsValid && playerRankIsValid

    fun shouldShowDetailedModeWarning() = showDetailedScoreState && subscoreTitles.isEmpty()

    private fun updateSubscoresAreValidState(latestInput: String) {
        subscoresAreValid = if (latestInput.toBigDecimalOrNull() != null) {
            subscoreStateBundles
                .plus(listOf(totalScoreBundle, uncategorizedScoreBundle))
                .all { it.validityState == ScoreStringValidityState.Valid }
        } else false
    }
}

class EditPlayerScoreViewModelFactory(
    private val playerObject: PlayerObject,
    private val matchObject: MatchObject,
    private val playerSubscores: List<SubscoreEntity>,
    private val subscoreTitles: List<SubscoreTitleEntity>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditPlayerScoreViewModel(
        playerObject = playerObject,
        matchObject = matchObject,
        playerSubscores = playerSubscores.toMutableList(),
        subscoreTitles = subscoreTitles,
        isGameManualRanked = isGameManualRanked,
        saveCallback = saveCallback
    ) as T
}