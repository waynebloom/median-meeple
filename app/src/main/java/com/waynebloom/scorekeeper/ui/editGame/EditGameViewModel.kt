package com.waynebloom.scorekeeper.ui.editGame

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.constants.DurationMs
import com.waynebloom.scorekeeper.di.factory.MutableStateFlowFactory
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
import com.waynebloom.scorekeeper.shared.domain.usecase.GetString
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.GameUiModel
import com.waynebloom.scorekeeper.viewmodel.CategorySectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@HiltViewModel
class EditGameViewModel @Inject constructor(
    private val deleteCategory: DeleteCategory,
    private val deleteGame: DeleteGame,
    getCategoriesByGameId: GetCategoriesByGameId,
    getGame: GetGame,
    private val getString: GetString,
    private val insertCategory: InsertCategory,
    mutableStateFlowFactory: MutableStateFlowFactory,
    savedStateHandle: SavedStateHandle,
    private val updateCategoryUseCase: UpdateCategory,
    private val updateGameUseCase: UpdateGame
): ViewModel() {

    private val viewModelState: MutableStateFlow<EditGameUiState>
    val uiState: MutableStateFlow<EditGameUiState>

    lateinit var composableCoroutineScope: CoroutineScope
    private val gameId = savedStateHandle.get<Long>("gameId")!!

    private val contentState: EditGameUiState.Content
        get() = viewModelState.value.asContent()

    init {

        EditGameUiState.Loading().let { initialState ->
            viewModelState = mutableStateFlowFactory.newInstance(initialState)
            uiState = viewModelState
        }

        viewModelScope.launch {

            val game = getGame(gameId)
            val categories = getCategoriesByGameId(gameId).sortedBy { it.position }

            viewModelState.update {
                EditGameUiState.Content(
                    categories = categories,
                    color = game.color,
                    nameInput = game.name,
                    scoringMode = game.scoringMode,
                )
            }
        }
    }

    private fun pushCategoryUpdate(category: CategoryUiModel) = viewModelScope.launch {
        updateCategoryUseCase(category, gameId)
    }

    private fun pushGameUpdate() {

        with(contentState) {

            viewModelScope.launch {

                updateGameUseCase(game = GameUiModel(
                    id = gameId,
                    name = nameInput,
                    color = color,
                    scoringMode = scoringMode
                ))
            }
        }
    }

    private fun updateState(
        pushGameUpdate: Boolean = false,
        function: (EditGameUiState.Content) -> EditGameUiState.Content
    ) {
        viewModelState.update(
            function = { viewModelState -> function(viewModelState.asContent()) }
        )

        if (pushGameUpdate) pushGameUpdate()
    }

    // region Game Details

    fun onColorClick(value: String) = updateState(pushGameUpdate = true) {
        it.copy(
            color = value,
            showColorMenu = false
        )
    }

    // TODO: make this better. Animation, confirmation state
    fun onDeleteClick() = viewModelScope.launch { deleteGame(gameId) }

    fun onNameChanged(value: TextFieldValue) = updateState(pushGameUpdate = true) {
        it.copy(
            nameInput = it.nameInput.copy(
                hasReceivedInput = true,
                isValid = value.text.isNotBlank(),
                value = value
            )
        )
    }

    fun onScoringModeChanged(value: ScoringMode) = updateState(pushGameUpdate = true) {
        it.copy(scoringMode = value)
    }

    // endregion

    // region Category Section

    @Composable
    fun getCategoryRowHeight(): Float =
        LocalDensity.current.run { Dimensions.Size.minTappableSize.toPx() }

    fun onCategoryInputChanged(value: TextFieldValue) {
        updateState { state ->
            val updatedCategoryInput = state.categoryInput.copy(
                hasReceivedInput = true,
                isValid = value.text.isNotBlank(),
                value = value
            )
            val index = state.indexOfCategoryReceivingInput!!
            val updatedCategories = state.categories.toMutableList().apply {
                this[index] = this[index].copy(name = updatedCategoryInput)
            }

            pushCategoryUpdate(updatedCategories[index])
            state.copy(categories = updatedCategories)
        }
    }

    fun onCategoryInputFocusChanged(focusState: FocusState) {
        /*if (!focusState.isFocused) {
            updateState {
                it.copy(
                    indexOfCategoryReceivingInput = null,
                    showCategoryInput = false
                )
            }
        }*/
    }

    fun onCategoryClick(category: CategoryUiModel) {

        updateState {
            it.copy(
                categoryInputTitle = getString(R.string.field_edit_category),
                indexOfCategoryReceivingInput = it.categories.indexOf(category),
                isEditMode = true,
                showCategoryInput = true
            )
        }

        composableCoroutineScope.launch {
            delayToAllowForAnimationToComplete()
            scrollToCategoryInput()
        }
    }

    fun onDeleteCategoryClick(category: CategoryUiModel) {
        updateState {
            val updatedCategories = it.categories.minus(category)
            it.copy(categories = updatedCategories)
        }

        viewModelScope.launch {
            deleteCategory(category.id)
        }
    }

    fun onDrag(dragDistance: Offset, rowHeight: Float) = updateState {
        val newDistance = it.dragState.dragDistance + dragDistance
        val dragDelta = newDistance.y / rowHeight
        val draggedToIndex = it.dragState.dragStart + dragDelta
        it.copy(
            dragState = it.dragState.copy(
                dragDistance = newDistance,
                dragEnd = when {
                    draggedToIndex < 0 -> 0
                    draggedToIndex > it.displayedCategories.lastIndex
                        -> it.displayedCategories.lastIndex
                    else -> draggedToIndex.toInt()
                }
            )
        )
    }

    fun onDragEnd() {
        val dragResult = contentState.dragState.let { it.dragStart to it.dragEnd }

        updateState { state ->
            val updatedCategories = state.categories.toMutableList().apply {

                // swap positions in the List
                val itemBeingDragged = removeAt(dragResult.first)
                add(dragResult.second, itemBeingDragged)

                // update the position property after the swap
                transformElement(dragResult.first) { it.copy(position = dragResult.first) }
                transformElement(dragResult.second) { it.copy(position = dragResult.second) }
            }

            state.copy(
                categories = updatedCategories,
                dragState = EditGameUiState.Content.DragState(),
            )
        }

        viewModelScope.launch {
            updateCategoryUseCase(contentState.categories[dragResult.first], gameId)
            updateCategoryUseCase(contentState.categories[dragResult.second], gameId)
        }
    }

    fun onDragStart(index: Int) = updateState {
        it.copy(dragState = it.dragState.copy(dragStart = index))
    }

    fun onEditModeClick() = updateState {
        if (it.categorySectionDisplayState != CategorySectionState.EditMode) {
            it.copy(isEditMode = true)
        } else {
            it.copy(
                indexOfCategoryReceivingInput = null,
                isEditMode = false,
                showCategoryInput = false
            )
        }
    }

    fun onNewCategoryClick() {
        viewModelScope.launch {

            val newCategoryPosition = contentState.categories.lastIndex + 1
            val newCategory = CategoryUiModel(
                name = TextFieldInput(),
                position = newCategoryPosition
            )
            val newId = insertCategory(
                category = newCategory,
                gameId = gameId
            )

            updateState {
                val updatedCategories = it.categories.plus(newCategory.copy(id = newId))

                it.copy(
                    categories = updatedCategories,
                    categoryInputTitle = getString(R.string.field_new_category),
                    indexOfCategoryReceivingInput = updatedCategories.lastIndex,
                    showCategoryInput = true
                )
            }
        }

        composableCoroutineScope.launch {
            delayToAllowForAnimationToComplete()
            scrollToCategoryInput()
        }
    }

    private suspend fun delayToAllowForAnimationToComplete() {
        val isInEditMode = viewModelState
            .value
            .asContent()
            .categorySectionDisplayState == CategorySectionState.EditMode
        val duration = if (isInEditMode) {
            DurationMs.short
        } else DurationMs.veryShort

        delay(duration.toDuration(DurationUnit.MILLISECONDS))
    }

    private suspend fun scrollToCategoryInput() =
        viewModelState
            .value
            .asContent()
            .lazyListState
            .animateScrollToItem(EditGameUiState.CategorySectionIndex)

    // endregion

    sealed interface EditGameUiState {

        companion object {
            const val CastFailContent =
                "An attempt was made to cast EditGameUiState to Content, but it was not in the " +
                        "Content state."
            const val CategorySectionIndex = 2
        }

        fun asContent() = this as? Content ?: throw IllegalStateException(CastFailContent)

        data class Loading(val loading: Boolean = true): EditGameUiState

        data class Content(
            val categories: List<CategoryUiModel>,
            val categoryInputTitle: String = "",
            val color: String,
            val dragState: DragState = DragState(),
            val indexOfCategoryReceivingInput: Int? = null,
            private val isEditMode: Boolean = false,
            val lazyListState: LazyListState = LazyListState(),
            val nameInput: TextFieldInput,
            val scoringMode: ScoringMode,
            val showCategoryInput: Boolean = false,
            val showColorMenu: Boolean = false
        ): EditGameUiState {

            val displayedCategories: List<CategoryUiModel>
                get() = if (dragState.isDragging && dragState.isDraggedToNewIndex) {
                    categories.toMutableList().apply {
                        val itemBeingDragged = removeAt(dragState.dragStart)
                        add(dragState.dragEnd, itemBeingDragged)
                    }
                } else categories
            
            val categorySectionDisplayState: CategorySectionState
                get() = when {
                    categories.isEmpty() -> CategorySectionState.Empty
                    isEditMode -> CategorySectionState.EditMode
                    else -> CategorySectionState.Default
                }

            val categoryInput: TextFieldInput
                get() = if (indexOfCategoryReceivingInput != null) {
                    categories[indexOfCategoryReceivingInput].name
                } else TextFieldInput()

            @Composable
            fun getResolvedColor() = LocalCustomThemeColors.current.getColorByKey(color)

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
        }
    }
}