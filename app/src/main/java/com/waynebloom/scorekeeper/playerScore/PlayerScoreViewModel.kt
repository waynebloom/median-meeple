package com.waynebloom.scorekeeper.playerScore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreEntityState
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.ext.statefulUpdateElement
import com.waynebloom.scorekeeper.ext.toStringForDisplay

class EditPlayerScoreViewModel(
    playerObject: PlayerDataRelationModel,
    matchObject: MatchDataRelationModel,
    private val categoryScores: MutableList<CategoryScoreDataModel>,
    var categoryTitles: List<CategoryDataModel>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (
        EntityStateBundle<PlayerDataModel>,
        List<CategoryScoreEntityState>) -> Unit
): ViewModel() {

    private var playerEntityNeedsUpdate = false
    private var saveWasTapped = false

    var initialPlayerEntity = playerObject.entity
    var playerRank by mutableStateOf(TextFieldValue())
    var playerRankIsValid by mutableStateOf(true)
    var name by mutableStateOf(TextFieldValue(initialPlayerEntity.name))
    var isDetailedMode by mutableStateOf(initialPlayerEntity.showDetailedScore)
    var categoryData by mutableStateOf(listOf<CategoryScoreEntityState>())
    var totalScoreData by mutableStateOf(
        CategoryScoreEntityState(
            entity = CategoryScoreDataModel(
                value = initialPlayerEntity.totalScore
            )
        )
    )
    private var isScoreDataValid by mutableStateOf(true)
    var isNameValid by mutableStateOf(name.text.isNotBlank())
    var uncategorizedScoreData by mutableStateOf(CategoryScoreEntityState(entity = CategoryScoreDataModel()))

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
                    .find { it.categoryId == subscoreTitle.id }

                if (correspondingSubscore != null) {
                    CategoryScoreEntityState(entity = correspondingSubscore)
                } else {
                    CategoryScoreEntityState(
                        entity = CategoryScoreDataModel(
                            categoryId = subscoreTitle.id,
                            playerId = initialPlayerEntity.id
                        ),
                        databaseAction = DatabaseAction.INSERT
                    )
                }
            }
    }

    private fun initializeUncategorizedSubscore(playerObject: PlayerDataRelationModel) {
        uncategorizedScoreData.apply {
            bigDecimal = playerObject.getUncategorizedScore()
            textFieldValue = TextFieldValue(
                text = playerObject.getUncategorizedScore().toStringForDisplay()
            )
        }
    }

    // endregion

    fun isSubmitButtonEnabled() = isScoreDataValid && isNameValid && playerRankIsValid

    fun onCategoryFieldChanged(id: Long, value: TextFieldValue) {
        categoryData.statefulUpdateElement(
            predicate = { it.entity.categoryId == id },
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
            && value.text.toInt() <= Constants.maximumPlayersInMatch
            && value.text.toInt() > 0
        playerEntityNeedsUpdate = true
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveClick(keyboardController: SoftwareKeyboardController?) {
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

    private fun getPlayerToCommit(): EntityStateBundle<PlayerDataModel> {
        val categorySum = categoryData.sumOf { it.bigDecimal }
        val uncategorizedScore = uncategorizedScoreData.bigDecimal
        val scoreTotalAsString = (categorySum + uncategorizedScore).toStringForDisplay()

        return EntityStateBundle(
            entity = initialPlayerEntity.copy(
                name = name.text,
                position = playerRank.text.toInt(),
                totalScore = scoreTotalAsString,
                showDetailedScore = isDetailedMode,
            ),
            databaseAction = if (playerEntityNeedsUpdate) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }

    private fun prepareSubscoreEntitiesForCommit() {
        categoryData.forEach {
            it.entity.value = it.bigDecimal.toStringForDisplay()
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
    private val playerObject: PlayerDataRelationModel,
    private val matchObject: MatchDataRelationModel,
    private val playerSubscores: List<CategoryScoreDataModel>,
    private val subscoreTitles: List<CategoryDataModel>,
    private val isGameManualRanked: Boolean,
    private val saveCallback: (
        EntityStateBundle<PlayerDataModel>,
        List<CategoryScoreEntityState>) -> Unit
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
