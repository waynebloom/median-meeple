package com.waynebloom.scorekeeper.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.data.model.EntityStateBundle
import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoringMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

enum class ScoringCategorySectionState {
    Default,
    Empty,
    EditMode,
}

class EditGameViewModel(
    initialGame: GameObject,
    private val saveCallback: (EntityStateBundle<GameEntity>,
        List<EntityStateBundle<CategoryTitleEntity>>) -> Unit
): ViewModel() {

    var lazyListState by mutableStateOf(LazyListState())
    var name: TextFieldValue by mutableStateOf(TextFieldValue(initialGame.entity.name))
        private set
    var isNameValid by mutableStateOf(name.text.isNotBlank())
        private set
    var scoringMode by mutableStateOf(ScoringMode.getModeByOrdinal(initialGame.entity.scoringMode))
    var themeColorString by mutableStateOf(initialGame.entity.color)
    private var colorMenuVisible: Boolean by mutableStateOf(false)

    val initialGameEntity: GameEntity = initialGame.entity
    private var gameEntityWasEdited = false
    private var saveWasTapped = false

    // region Scoring Category

    private var categoryStateList: List<EntityStateBundle<CategoryTitleEntity>>
        by mutableStateOf(initialGame
        .subscoreTitles
        .sortedBy { it.position }
        .map { EntityStateBundle(entity = it) })
    private var isScoringCategoryEditModeOn by mutableStateOf(false)
    private var rowHeightInPx by Delegates.notNull<Float>()
    private var totalDragDistance by mutableStateOf(Offset.Zero)
    private var dragStartIndex by mutableStateOf(-1)
    private var dragCurrentIndex by mutableStateOf(-1)
    var showCategoryInput by mutableStateOf(false)
        private set
    var categoryInput by mutableStateOf(TextFieldValue())
        private set
    var isCategoryInputValid by mutableStateOf(false)
        private set
    var isFreshInput by mutableStateOf(true)
        private set

    private var deletedCategories: MutableList<EntityStateBundle<CategoryTitleEntity>> = mutableListOf()
    private var indexOfCategoryReceivingEdit = 0

    // endregion

    companion object {
        const val scoringCategorySectionIndex = 2
    }

    fun onRecompose(gameId: Long, rowHeightInPx: Float) = this.apply {
        initialGameEntity.id = gameId
        this.rowHeightInPx = rowHeightInPx
    }

    // region Scoring Categories

    fun clearAndHideCategoryInput() {
        categoryInput = TextFieldValue()
        indexOfCategoryReceivingEdit = -1
        showCategoryInput = false
        isCategoryInputValid = false
        isFreshInput = true
    }

    fun deleteCategory(index: Int) {
        if (index == indexOfCategoryReceivingEdit) clearAndHideCategoryInput()

        val deleteTarget = categoryStateList[index]
        deleteTarget.databaseAction = DatabaseAction.DELETE
        deletedCategories.add(deleteTarget)

        categoryStateList = categoryStateList.minus(categoryStateList[index])
        if (categoryStateList.isEmpty()) isScoringCategoryEditModeOn = false
    }

    private fun moveCategoryToNewPosition(currentPosition: Int, newPosition: Int) {

        categoryStateList = categoryStateList.toMutableList().apply {
            val itemBeingMoved = removeAt(currentPosition)
            add(newPosition, itemBeingMoved)
        }
    }

    fun onCategoryInputChanged(value: TextFieldValue) {
        isCategoryInputValid = value.text.isNotBlank()
        isFreshInput = isFreshInput && value == categoryInput
        categoryInput = value
    }

    fun showInputForExistingCategory(
        index: Int,
        isTransitioningToEditMode: Boolean,
        coroutineScope: CoroutineScope,
    ) {
        val categoryTitle = categoryStateList[index].entity.title
        onCategoryInputChanged(TextFieldValue(categoryTitle))
        indexOfCategoryReceivingEdit = index
        showCategoryInput = true
        scrollToCategoryInput(
            isTransitioningToEditMode = isTransitioningToEditMode,
            coroutineScope = coroutineScope,
        )
    }

    fun showInputForNewCategory(coroutineScope: CoroutineScope) {
        scrollToCategoryInput(coroutineScope = coroutineScope)
        indexOfCategoryReceivingEdit = -1
        showCategoryInput = true
    }

    private fun scrollToCategoryInput(
        isTransitioningToEditMode: Boolean = false,
        coroutineScope: CoroutineScope,
    ) {
        coroutineScope.launch {
            if (isTransitioningToEditMode) delay(400) else delay(50)
            lazyListState.animateScrollToItem(scoringCategorySectionIndex)
        }
    }

    fun submitCategoryInput() {
        if (!isCategoryInputValid) {
            isFreshInput = false
            return
        }

        if (indexOfCategoryReceivingEdit != -1) {
            submitForExistingCategory(categoryInput, indexOfCategoryReceivingEdit)
        } else submitForNewCategory(categoryInput)

        clearAndHideCategoryInput()
    }

    private fun submitForExistingCategory(categoryTitle: TextFieldValue, index: Int) {
        categoryStateList[index].apply {
            entity.title = categoryTitle.text
            databaseAction = DatabaseAction.UPDATE
        }
    }

    private fun submitForNewCategory(categoryTitle: TextFieldValue) {
        categoryStateList = categoryStateList.plus(
            EntityStateBundle(
                entity = CategoryTitleEntity(
                    gameId = initialGameEntity.id,
                    title = categoryTitle.text,
                ),
                databaseAction = DatabaseAction.INSERT
            )
        )
    }

    fun toggleEditMode(indexOfEditTarget: Int?, coroutineScope: CoroutineScope) {

        isScoringCategoryEditModeOn = !isScoringCategoryEditModeOn
        if (isScoringCategoryEditModeOn) {

            if (indexOfEditTarget != null)
                showInputForExistingCategory(
                    index = indexOfEditTarget,
                    isTransitioningToEditMode = true,
                    coroutineScope = coroutineScope,
                )
        } else showCategoryInput = false
    }

    /**
     * Map [categoryStateList] to the title of their entity. Then if an item is being dragged,
     * adjust the ordering of the list to reflect its current position.
     */
    fun getCategoriesToDisplay(): List<String> {
        val categoryEntities = categoryStateList.map { it.entity.title }
        return if (dragStartIndex > -1 && dragCurrentIndex > -1) {
            categoryEntities.toMutableList().apply {
                val itemBeingDragged = removeAt(dragStartIndex)
                add(dragCurrentIndex, itemBeingDragged)
                toList()
            }
        } else categoryEntities
    }

    fun getCategoryListState() = when {
        categoryStateList.isEmpty() -> ScoringCategorySectionState.Empty
        isScoringCategoryEditModeOn -> ScoringCategorySectionState.EditMode
        else -> ScoringCategorySectionState.Default
    }

    fun getInputFieldTitle(): Int  =
        if (indexOfCategoryReceivingEdit == -1) {
            R.string.field_new_category
        } else R.string.field_edit_category

    // endregion

    // region Scoring Category Drag Logic

    fun onDragStart(indexBeingDragged: Int) {
        dragStartIndex = indexBeingDragged
    }

    fun onDragEnd() {
        moveCategoryToNewPosition(dragStartIndex, dragCurrentIndex)
        dragStartIndex = -1
        dragCurrentIndex = -1
        totalDragDistance = Offset.Zero
    }

    fun onDrag(dragDistance: Offset) {
        totalDragDistance += dragDistance

        val dragDelta = totalDragDistance.y / rowHeightInPx
        val draggedToIndex = (dragStartIndex + dragDelta).toInt()
        dragCurrentIndex = when {
            draggedToIndex < 0 -> 0
            draggedToIndex >= categoryStateList.size -> categoryStateList.lastIndex
            else -> draggedToIndex
        }
    }

    // endregion

    private fun getCategoryBundlesForCommit(): List<EntityStateBundle<CategoryTitleEntity>> {
        categoryStateList.forEachIndexed { index, bundle -> bundle.entity.position = index }
        return categoryStateList + deletedCategories
    }

    private fun getGameToCommit(): EntityStateBundle<GameEntity> {
        return EntityStateBundle(
            entity = initialGameEntity.apply {
                color = themeColorString
                name = this@EditGameViewModel.name.text
                scoringMode = this@EditGameViewModel.scoringMode.ordinal
            },
            databaseAction = if (gameEntityWasEdited) {
                DatabaseAction.UPDATE
            } else DatabaseAction.NO_ACTION
        )
    }

    fun onNameChanged(textFieldValue: TextFieldValue) {
        isNameValid = textFieldValue.text.isNotBlank()
        name = textFieldValue
        gameEntityWasEdited = true
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun onSaveTap(keyboardController: SoftwareKeyboardController?) {
        if (!saveWasTapped) {
            saveWasTapped = true
            keyboardController?.hide()

            saveCallback(getGameToCommit(), getCategoryBundlesForCommit())
        }
    }

    fun onScoringModeChanged(mode: ScoringMode) {
        scoringMode = mode
        gameEntityWasEdited = true
    }

    fun selectColor(color: String) {
        themeColorString = color
        colorMenuVisible = false
        gameEntityWasEdited = true
    }
}

class EditGameViewModelFactory (
    private val initialGame: GameObject,
    private val saveCallback: (EntityStateBundle<GameEntity>, List<EntityStateBundle<CategoryTitleEntity>>) -> Unit,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = EditGameViewModel(
        initialGame = initialGame,
        saveCallback = saveCallback
    ) as T
}