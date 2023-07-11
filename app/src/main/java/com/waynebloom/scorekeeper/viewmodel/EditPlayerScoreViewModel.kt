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
import com.waynebloom.scorekeeper.data.model.subscore.CategoryScoreEntity
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.ext.statefulUpdateElement
import com.waynebloom.scorekeeper.ext.toTrimmedScoreString

class EditPlayerScoreViewModel(
    playerObject: PlayerObject,
    matchObject: MatchObject,
    private val categoryScores: MutableList<CategoryScoreEntity>,
    var categoryTitles: List<CategoryTitleEntity>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
): ViewModel() {

    private var playerEntityNeedsUpdate = false
    private var saveWasTapped = false

    var initialPlayerEntity = playerObject.entity
    var playerRank by mutableStateOf(TextFieldValue())
    var playerRankIsValid by mutableStateOf(true)
    var name by mutableStateOf(TextFieldValue(initialPlayerEntity.name))
    var isDetailedMode by mutableStateOf(initialPlayerEntity.showDetailedScore)
    var categoryData by mutableStateOf(listOf<SubscoreStateBundle>())
    var totalScoreData by mutableStateOf(
        SubscoreStateBundle(
            entity = CategoryScoreEntity(
                value = initialPlayerEntity.score
            )
        )
    )
    private var isScoreDataValid by mutableStateOf(true)
    var isNameValid by mutableStateOf(name.text.isNotBlank())
    var uncategorizedScoreData by mutableStateOf(SubscoreStateBundle(entity = CategoryScoreEntity()))

    // region Initialization

    init {
        categoryTitles = categoryTitles.sortedBy { it.position }
        initializeRank(
            playerCount = matchObject.players.size,
            currentPlayerRankValue = playerObject.entity.position
        )
        initializeSubscores()
        initializeUncategorizedSubscore(playerObject)
    }

    private fun initializeRank(playerCount: Int, currentPlayerRankValue: Int) {
        playerRank = if (isGameManualRanked && currentPlayerRankValue == 0) {
            playerEntityNeedsUpdate = true
            TextFieldValue(playerCount.toString())
        } else TextFieldValue(currentPlayerRankValue.toString())
    }

    private fun initializeSubscores() {
        categoryData = categoryTitles
            .map { subscoreTitle ->
                val correspondingSubscore = categoryScores
                    .find { it.categoryTitleId == subscoreTitle.id }

                if (correspondingSubscore != null) {
                    SubscoreStateBundle(entity = correspondingSubscore)
                } else {
                    SubscoreStateBundle(
                        entity = CategoryScoreEntity(
                            categoryTitleId = subscoreTitle.id,
                            playerId = initialPlayerEntity.id
                        ),
                        databaseAction = DatabaseAction.INSERT
                    )
                }
            }
    }

    private fun initializeUncategorizedSubscore(playerObject: PlayerObject) {
        uncategorizedScoreData.apply {
            bigDecimal = playerObject.getUncategorizedScore()
            textFieldValue = TextFieldValue(
                text = playerObject.getUncategorizedScore().toTrimmedScoreString()
            )
        }
    }

    // endregion

    fun isSubmitButtonEnabled() = isScoreDataValid && isNameValid && playerRankIsValid

    fun onCategoryFieldChanged(id: Long, value: TextFieldValue) {
        categoryData.statefulUpdateElement(
            predicate = { it.entity.categoryTitleId == id },
            update = {
                val textWasChanged = it.textFieldValue.text != value.text
                if (textWasChanged) {
                    it.updateDatabaseAction(DatabaseAction.UPDATE)
                    it.setScoreFromTextValue(value)
                    playerEntityNeedsUpdate = true
                } else {
                    it.textFieldValue = value
                }
            }
        )

        updateScoreDataValidity(latestInput = value.text)
        recalculateTotalScore()
    }

    fun onDetailedModeChanged(value: Boolean) {
        isDetailedMode = value
        playerEntityNeedsUpdate = true
    }

    fun onNameChanged(value: TextFieldValue) {
        name = value
        isNameValid = value.text.isNotBlank()
        playerEntityNeedsUpdate = true
    }

    fun onPlayerRankChanged(value: TextFieldValue) {
        playerRank = value
        playerRankIsValid = value.text.isNotEmpty()
            && value.text.isDigitsOnly()
            && value.text.toInt() <= 100
            && value.text.toInt() > 0
        playerEntityNeedsUpdate = true
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?) {
        prepareSubscoreEntitiesForCommit()

        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            saveCallback(getPlayerToCommit(), categoryData)
        }
    }

    fun onTotalFieldChanged(value: TextFieldValue) {
        val scoreBigDecimal = value.text.toBigDecimalOrNull()
        val textWasChanged = totalScoreData.textFieldValue.text != value.text

        if (textWasChanged && scoreBigDecimal != null) {
            val adjustedUncategorizedScore =
                uncategorizedScoreData.bigDecimal + scoreBigDecimal - totalScoreData.bigDecimal
            uncategorizedScoreData.setScoreFromBigDecimal(adjustedUncategorizedScore)
            playerEntityNeedsUpdate = true
        }

        totalScoreData.setScoreFromTextValue(value)
        updateScoreDataValidity(latestInput = value.text)
    }

    fun onUncategorizedFieldChanged(textFieldValue: TextFieldValue) {
        val scoreBigDecimal = textFieldValue.text.toBigDecimalOrNull()
        val textWasChanged = uncategorizedScoreData.textFieldValue.text != textFieldValue.text

        if (textWasChanged && scoreBigDecimal != null) {
            val adjustedTotalScore =
                totalScoreData.bigDecimal + scoreBigDecimal - uncategorizedScoreData.bigDecimal
            totalScoreData.setScoreFromBigDecimal(adjustedTotalScore)
            playerEntityNeedsUpdate = true
        }

        uncategorizedScoreData.setScoreFromTextValue(textFieldValue)
        updateScoreDataValidity(latestInput = textFieldValue.text)
    }

    private fun getPlayerToCommit(): EntityStateBundle<PlayerEntity> {
        val categorySum = categoryData.sumOf { it.bigDecimal }
        val uncategorizedScore = uncategorizedScoreData.bigDecimal
        val scoreTotalAsString = (categorySum + uncategorizedScore).toTrimmedScoreString()

        return EntityStateBundle(
            entity = initialPlayerEntity.copy(
                name = name.text,
                position = playerRank.text.toInt(),
                score = scoreTotalAsString,
                showDetailedScore = isDetailedMode,
            ),
            databaseAction = if (playerEntityNeedsUpdate) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }

    private fun prepareSubscoreEntitiesForCommit() {
        categoryData.forEach {
            it.entity.value = it.bigDecimal.toTrimmedScoreString()
        }
    }

    private fun recalculateTotalScore() {
        if (isScoreDataValid) {
            val sumOfSubscores = categoryData.sumOf { it.bigDecimal } +
                uncategorizedScoreData.bigDecimal
            totalScoreData.setScoreFromBigDecimal(sumOfSubscores)
        }
    }

    private fun updateScoreDataValidity(latestInput: String) {
        isScoreDataValid = if (latestInput.toBigDecimalOrNull() != null) {
            categoryData
                .plus(listOf(totalScoreData, uncategorizedScoreData))
                .all { it.validityState == ScoreStringValidityState.Valid }
        } else false
    }
}

class EditPlayerScoreViewModelFactory(
    private val playerObject: PlayerObject,
    private val matchObject: MatchObject,
    private val playerSubscores: List<CategoryScoreEntity>,
    private val subscoreTitles: List<CategoryTitleEntity>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<SubscoreStateBundle>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditPlayerScoreViewModel(
        playerObject = playerObject,
        matchObject = matchObject,
        categoryScores = playerSubscores.toMutableList(),
        categoryTitles = subscoreTitles,
        isGameManualRanked = isGameManualRanked,
        saveCallback = saveCallback
    ) as T
}