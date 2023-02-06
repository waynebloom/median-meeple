package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.data.model.EntityStateBundle
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction

enum class SubscoreTitleSectionHeaderState {
    TitleAndActionBar,
    EditItem,
    NewItem
}

enum class SubscoreTitleSectionListState {
    Horizontal,
    Vertical
}

class EditGameViewModel(
    initialGame: GameObject,
    private val saveCallback: (EntityStateBundle<GameEntity>,
                               List<EntityStateBundle<SubscoreTitleEntity>>) -> Unit
): ViewModel() {

    var initialGameEntity = initialGame.entity
    var gameColor: String by mutableStateOf(initialGame.entity.color)
    var gameName: String by mutableStateOf(initialGame.entity.name)
    var gameScoringMode: Int by mutableStateOf(initialGame.entity.scoringMode)
    private var subscoreTitleCommitBundles: List<EntityStateBundle<SubscoreTitleEntity>> by mutableStateOf(
        initialGame
            .subscoreTitles
            .sortedBy { it.position }
            .map { EntityStateBundle(entity = it) }
    )
    private var deletedBundles: MutableList<EntityStateBundle<SubscoreTitleEntity>> = mutableListOf()

    var colorMenuVisible: Boolean by mutableStateOf(false)
    var subscoreTitleInput: String by mutableStateOf("")
    var subscoreTitleSectionHeaderState by mutableStateOf(SubscoreTitleSectionHeaderState.TitleAndActionBar)
    var subscoreTitleSectionListState by mutableStateOf(SubscoreTitleSectionListState.Horizontal)
    private var gameEntityWasEdited = false
    private var saveWasTapped = false
    private var selectedSubscoreIndex = 0

    // region Subscore Titles

    fun addSubscoreTitle() {
        subscoreTitleCommitBundles = subscoreTitleCommitBundles.plus(
            EntityStateBundle(
                entity = SubscoreTitleEntity(
                    gameId = initialGameEntity.id,
                    position = subscoreTitleCommitBundles.size + 1,
                    title = subscoreTitleInput,
                ),
                databaseAction = DatabaseAction.INSERT
            )
        )
        subscoreTitleInput = ""
    }

    fun clearEditor() {
        subscoreTitleSectionHeaderState = SubscoreTitleSectionHeaderState.TitleAndActionBar
        subscoreTitleInput = ""
    }

    fun changePosition(index: Int, newPosition: Int) {
        subscoreTitleCommitBundles[index].apply {
            entity.position = newPosition
            databaseAction = DatabaseAction.UPDATE
        }
        subscoreTitleCommitBundles[newPosition].apply {
            entity.position = index
            databaseAction = DatabaseAction.UPDATE
        }
        subscoreTitleCommitBundles = subscoreTitleCommitBundles.sortedBy { it.entity.position }
    }

    fun deleteSubscoreTitle(index: Int) {
        deletedBundles.add(
            subscoreTitleCommitBundles[index].apply { databaseAction = DatabaseAction.DELETE }
        )
        subscoreTitleCommitBundles = subscoreTitleCommitBundles.minus(subscoreTitleCommitBundles[index])
        subscoreTitleCommitBundles
            .slice(index until subscoreTitleCommitBundles.size)
            .forEach { it.entity.position -= 1 }
        if (index == selectedSubscoreIndex) {
            clearEditor()
        }
    }

    fun getEditorFieldTitle(): Int  =
        if (subscoreTitleSectionHeaderState == SubscoreTitleSectionHeaderState.NewItem) {
            R.string.field_new_category
        } else R.string.field_edit_category

    fun getSubscoreTitlesToDisplay() =
        subscoreTitleCommitBundles.map { it.entity }

    fun showEditorForEditSubscoreTitle(index: Int) {
        subscoreTitleSectionHeaderState = SubscoreTitleSectionHeaderState.EditItem
        selectedSubscoreIndex = index
        subscoreTitleInput = subscoreTitleCommitBundles[index].entity.title
    }

    fun showEditorForNewSubscoreTitle() {
        subscoreTitleSectionHeaderState = SubscoreTitleSectionHeaderState.NewItem
    }

    fun showHorizontalList() {
        subscoreTitleSectionListState = SubscoreTitleSectionListState.Horizontal
    }

    fun showVerticalList() {
        subscoreTitleSectionListState = SubscoreTitleSectionListState.Vertical
    }

    fun updateCurrentSubscoreTitle() {
        subscoreTitleCommitBundles[selectedSubscoreIndex].apply {
            entity.title = subscoreTitleInput
            databaseAction = DatabaseAction.UPDATE
        }
        subscoreTitleSectionHeaderState = SubscoreTitleSectionHeaderState.TitleAndActionBar
        subscoreTitleInput = ""
    }

    // endregion

    private fun getGameToCommit(): EntityStateBundle<GameEntity> {
        return EntityStateBundle(
            entity = initialGameEntity.apply {
                color = gameColor
                name = gameName
                scoringMode = gameScoringMode
            },
            databaseAction = if (gameEntityWasEdited) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }

    private fun getSubscoreTitlesToCommit() = subscoreTitleCommitBundles + deletedBundles

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?) {
        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()
            saveCallback(getGameToCommit(), getSubscoreTitlesToCommit())
        }
    }

    fun selectColor(color: String) {
        gameColor = color
        colorMenuVisible = false
        gameEntityWasEdited = true
    }

    fun selectMode(mode: Int) {
        gameScoringMode = mode
        gameEntityWasEdited = true
    }

    @JvmName("setGameName1")
    fun setGameName(name: String) {
        gameName = name
        gameEntityWasEdited = true
    }
}

class EditGameViewModelViewModelFactory (
    private val initialGame: GameObject,
    private val saveCallback: (EntityStateBundle<GameEntity>, List<EntityStateBundle<SubscoreTitleEntity>>) -> Unit,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditGameViewModel(
        initialGame = initialGame,
        saveCallback = saveCallback
    ) as T
}