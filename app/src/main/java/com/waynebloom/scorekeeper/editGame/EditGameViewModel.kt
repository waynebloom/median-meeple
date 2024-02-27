package com.waynebloom.scorekeeper.editGame

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.transformElement
import com.waynebloom.scorekeeper.room.domain.usecase.DeleteCategory
import com.waynebloom.scorekeeper.room.domain.usecase.DeleteGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetCategoriesByGameId
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.InsertCategory
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateCategory
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateGame
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.GameDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGameViewModel @Inject constructor(
    private val deleteCategory: DeleteCategory,
    private val deleteGame: DeleteGame,
    getCategoriesByGameId: GetCategoriesByGameId,
    getGame: GetGame,
    private val insertCategory: InsertCategory,
    mutableStateFlowFactory: MutableStateFlowFactory,
    savedStateHandle: SavedStateHandle,
    private val updateCategoryUseCase: UpdateCategory,
    private val updateGameUseCase: UpdateGame,
): ViewModel() {

    private val viewModelState: MutableStateFlow<EditGameViewModelState>
    val uiState: StateFlow<EditGameUiState>

    private val gameId = savedStateHandle.get<Long>("gameId")!!
    lateinit var composableCoroutineScope: CoroutineScope

    init {

        val initialState = EditGameViewModelState()
        viewModelState = mutableStateFlowFactory.newInstance(initialState)
        uiState = viewModelState
            .map(EditGameViewModelState::toUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = initialState.toUiState()
            )

        // fetching game data from db
        viewModelScope.launch {

            val game = getGame(gameId)
            val categories = getCategoriesByGameId(gameId).sortedBy { it.position }

            viewModelState.update {

                it.copy(
                    loading = false,
                    categories = categories,
                    color = game.color,
                    nameInput = game.name,
                    scoringMode = game.scoringMode
                )
            }
        }
    }

    fun onConfirmClick() {
        viewModelScope.launch {
            viewModelState.value.let { state ->
                updateGameUseCase(
                    GameDomainModel(
                        id = gameId,
                        name = state.nameInput,
                        color = state.color,
                        scoringMode = state.scoringMode
                    )
                )

                state.categories.forEach {
                    updateCategoryUseCase(it, gameId)
                }
            }
        }
    }

    // region Game Details

    fun onColorClick(value: String) = viewModelState.update {
        it.copy(color = value, showColorMenu = false)
    }

    // TODO: make this better. Animation, confirmation state
    fun onDeleteClick() = viewModelScope.launch {
        deleteGame(gameId)
    }

    fun onNameChanged(value: TextFieldValue) = viewModelState.update {
        it.copy(
            nameInput = it.nameInput.copy(
                isValid = value.text.isNotBlank(),
                value = value
            )
        )
    }

    fun onScoringModeChanged(value: ScoringMode) = viewModelState.update {
        it.copy(scoringMode = value)
    }

    // endregion

    // region Category Section

    @Composable
    fun getCategoryRowHeight(): Float {
        return LocalDensity.current.run {
            Dimensions.Size.minTappableSize.toPx()
        }
    }

    fun onCategoryClick(index: Int) = viewModelState.update {
        it.copy(
            indexOfCategoryReceivingInput = index,
            isCategoryDialogOpen = true
        )
    }

    fun onEditButtonClick() = viewModelState.update {
        it.copy(isCategoryDialogOpen = true)
    }

    fun onCategoryInputChanged(input: TextFieldValue) = viewModelState.update {
        val index = it.indexOfCategoryReceivingInput ?: return
        val category = it.categories[index]
        val updatedCategory = category.copy(
            name = category.name.copy(value = input)
        )
        val updatedCategories = it.categories.toMutableList().apply {
            this[index] = updatedCategory
        }

        // TODO: need to figure out why this was needed. Comment your code better, loser
        // pushUpdate(updatedCategory)

        it.copy(categories = updatedCategories)
    }

    fun onNewCategoryClick() {
        viewModelScope.launch {

            val newCategoryPosition = viewModelState.value.categories.lastIndex + 1
            val newCategory = CategoryUiModel(
                name = TextFieldInput(),
                position = newCategoryPosition
            )
            val newId = insertCategory(
                category = newCategory,
                gameId = gameId
            )

            viewModelState.update {
                val updatedCategories = it.categories.plus(newCategory.copy(id = newId))

                it.copy(
                    categories = updatedCategories,
                    indexOfCategoryReceivingInput = updatedCategories.lastIndex,
                    isCategoryDialogOpen = true,
                )
            }
        }
    }

    fun onDrag(dragDistance: Offset, rowHeight: Float) = viewModelState.update {
        val newDistance = it.dragState.dragDistance + dragDistance
        val dragDelta = newDistance.y / rowHeight
        val draggedToIndex = it.dragState.dragStart + dragDelta
        it.copy(
            dragState = it.dragState.copy(
                dragDistance = newDistance,
                dragEnd = when {
                    draggedToIndex < 0 -> 0
                    draggedToIndex > it.categories.lastIndex
                    -> it.categories.lastIndex
                    else -> draggedToIndex.toInt()
                }
            )
        )
    }

    fun onDragEnd() {
        val dragResult = viewModelState.value.dragState.let { it.dragStart to it.dragEnd }

        viewModelState.update { state ->
            val updatedCategories = state.categories.toMutableList().apply {

                // swap positions in the List
                val itemBeingDragged = removeAt(dragResult.first)
                add(dragResult.second, itemBeingDragged)

                // update the position property of affected elements
                if (dragResult.first < dragResult.second) {
                    for (i in dragResult.first..dragResult.second) {
                        transformElement(i) { it.copy(position = i) }
                    }
                } else {
                    for (i in dragResult.first downTo dragResult.second) {
                        transformElement(i) { it.copy(position = i) }
                    }
                }
            }

            state.copy(
                categories = updatedCategories,
                dragState = DragState(),
            )
        }
    }

    fun onDragStart(index: Int) = viewModelState.update {
        it.copy(dragState = it.dragState.copy(dragStart = index))
    }

    fun onHideCategoryInput() = viewModelState.update {
        it.copy(indexOfCategoryReceivingInput = null)
    }

    fun onDeleteCategoryClick() {

        val index = viewModelState.value.indexOfCategoryReceivingInput!!
        val idOfDeletedCategory = viewModelState.value.categories[index].id

        viewModelState.update { state ->
            val updatedCategories = state.categories.toMutableList()
            updatedCategories.listIterator(index).apply {

                // remove the element that is being deleted
                next(); remove()

                // adjust the position property of the remaining elements
                while(hasNext()) {
                    val category = next()
                    val adjustedPosition = category.position - 1
                    set(category.copy(position = adjustedPosition))
                }
            }

            state.copy(
                categories = updatedCategories,
                indexOfCategoryReceivingInput = null,
            )
        }

        viewModelScope.launch {
            deleteCategory(idOfDeletedCategory)
        }
    }

    fun onCategoryDialogDismiss() = viewModelState.update {
        it.copy(
            indexOfCategoryReceivingInput = null,
            isCategoryDialogOpen = false
        )
    }

    // endregion
}

private data class EditGameViewModelState(
    val loading: Boolean = true,
    val categories: List<CategoryUiModel> = emptyList(),
    val color: String = "",
    val dragState: DragState = DragState(),
    val indexOfCategoryReceivingInput: Int? = null,
    val isCategoryDialogOpen: Boolean = false,
    val nameInput: TextFieldInput = TextFieldInput(),
    val scoringMode: ScoringMode = ScoringMode.Descending,
    val showColorMenu: Boolean = false,
) {

    fun toUiState() = if (loading) {
        EditGameUiState.Loading
    } else {
        EditGameUiState.Content(
            categories = getCategoriesAdjustedForActiveDragAction(),
            color = color,
            dragState = dragState,
            indexOfCategoryReceivingInput = indexOfCategoryReceivingInput,
            isCategoryDialogOpen = isCategoryDialogOpen,
            nameInput = nameInput,
            scoringMode = scoringMode,
            showColorMenu = showColorMenu
        )
    }

    private fun getCategoriesAdjustedForActiveDragAction(): List<CategoryUiModel> {
        return if (dragState.isDragging && dragState.isDraggedToNewIndex) {
            categories.toMutableList().apply {
                val itemBeingDragged = removeAt(dragState.dragStart)
                add(dragState.dragEnd, itemBeingDragged)
            }
        } else {
            categories
        }
    }
}

sealed interface EditGameUiState {

    data object Loading: EditGameUiState

    data class Content(
        val categories: List<CategoryUiModel>,
        val color: String,
        val dragState: DragState,
        val indexOfCategoryReceivingInput: Int?,
        val isCategoryDialogOpen: Boolean,
        val nameInput: TextFieldInput,
        val scoringMode: ScoringMode,
        val showColorMenu: Boolean,
    ): EditGameUiState {

        @Composable
        fun getResolvedColor() = LocalCustomThemeColors.current.getColorByKey(color)
    }
}

data class DragState(
    val dragEnd: Int = -1,
    val dragStart: Int = -1,
    val dragDistance: Offset = Offset.Zero
) {

    val isDragging: Boolean
        get() = dragStart != -1 && dragEnd != -1

    val isDraggedToNewIndex: Boolean
        get() = dragStart != dragEnd
}
