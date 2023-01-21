package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.ext.updateElement

class EditPlayerScoreViewModel(
    playerObject: PlayerObject,
    private val playerSubscores: MutableList<SubscoreEntity>,
    var subscoreTitles: List<SubscoreTitleEntity>,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<EntityStateBundle<SubscoreEntity>>) -> Unit
): ViewModel() {

    private var playerEntityNeedsUpdate = false
    private var saveWasTapped = false

    var initialPlayerEntity = playerObject.entity
    var nameState: String by mutableStateOf(initialPlayerEntity.name)
    var showDetailedScoreState: Boolean by mutableStateOf(initialPlayerEntity.showDetailedScore)
    var subscoreStateBundles: List<EntityStateBundle<SubscoreEntity>> by mutableStateOf(listOf())
    var totalScoreState: Long by mutableStateOf(initialPlayerEntity.score ?: 0)
    var uncategorizedScoreRemainderState: Long by
        mutableStateOf(playerObject.getUncategorizedScoreRemainder())

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
                    EntityStateBundle(entity = correspondingSubscore)
                } else {
                    EntityStateBundle(
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

    private fun getPlayerToCommit(): EntityStateBundle<PlayerEntity> {
        return EntityStateBundle(
            entity = initialPlayerEntity.copy (
                name = nameState,
                showDetailedScore = showDetailedScoreState,
                score = subscoreStateBundles.sumOf { it.entity.value ?: 0 } + uncategorizedScoreRemainderState
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

    fun setName(name: String) {
        nameState = name
        playerEntityNeedsUpdate = true
    }

    fun setShowDetailedScore(showDetailedScore: Boolean) {
        showDetailedScoreState = showDetailedScore
        playerEntityNeedsUpdate = true
    }

    fun updateSubscore(subscoreTitleId: Long, value: String) {
        playerEntityNeedsUpdate = true
        subscoreStateBundles = subscoreStateBundles.updateElement(
            predicate = { it.entity.subscoreTitleId == subscoreTitleId },
            transform = {
                it.copy(
                    entity = it.entity.copy(
                        value = value.toLongOrNull()
                    )
                ).apply { databaseAction = DatabaseAction.UPDATE }
            }
        )
    }

    fun updateTotalScore(score: String) {
        val scoreToLong = score.toLongOrNull() ?: 0
        uncategorizedScoreRemainderState += scoreToLong - totalScoreState
        totalScoreState = scoreToLong
        playerEntityNeedsUpdate = true
    }

    fun updateUncategorizedScoreRemainder(score: String) {
        val scoreToLong = score.toLongOrNull() ?: 0
        totalScoreState += scoreToLong - uncategorizedScoreRemainderState
        uncategorizedScoreRemainderState = scoreToLong
        playerEntityNeedsUpdate = true
    }
}

class EditPlayerScoreViewModelFactory(
    private val playerObject: PlayerObject,
    private val playerSubscores: List<SubscoreEntity>,
    private val subscoreTitles: List<SubscoreTitleEntity>,
    private val saveCallback: (EntityStateBundle<PlayerEntity>,
        List<EntityStateBundle<SubscoreEntity>>) -> Unit
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditPlayerScoreViewModel(
        playerObject = playerObject,
        playerSubscores = playerSubscores.toMutableList(),
        subscoreTitles = subscoreTitles,
        saveCallback = saveCallback
    ) as T
}