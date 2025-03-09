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
import com.waynebloom.scorekeeper.database.domain.CategoryRepository
import com.waynebloom.scorekeeper.database.domain.GameRepository
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.transformElement
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGameViewModel @Inject constructor(
	private val categoryRepository: CategoryRepository,
	private val gameRepository: GameRepository,
	mutableStateFlowFactory: MutableStateFlowFactory,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val viewModelState: MutableStateFlow<EditGameViewModelState>
	val uiState: StateFlow<EditGameUiState>

	private var gameID = savedStateHandle.get<Long>("gameId")!!

	// ASAP: Make sure whatever uses this is using it correctly.
	lateinit var composableCoroutineScope: CoroutineScope

	init {
		viewModelState = mutableStateFlowFactory.newInstance(EditGameViewModelState())
		uiState = viewModelState
			.map(EditGameViewModelState::toUiState)
			.stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

		// ASAP: All of my launched jobs have been running on the main thread by default. Fix this.
		viewModelScope.launch {

			if (gameID != -1L) {
				val game = gameRepository.getOne(gameID).first()
				val categories = categoryRepository.getByGameID(gameID)
					.first()
					.filterNot { it.name.text == "defaultMiscCategory" }
					.sortedBy { it.position }

				viewModelState.update {
					it.copy(
						loading = false,
						categories = categories,
						colorIndex = game.displayColorIndex,
						name = game.name,
						scoringMode = game.scoringMode
					)
				}
			} else {
				viewModelState.update {
					it.copy(
						loading = false,
						scoringMode = ScoringMode.Descending
					)
				}
			}
		}
	}

	fun onSaveClick(onFinish: () -> Unit) = viewModelScope.launch {
		viewModelState.value.let { state ->
			if (gameID == -1L) {
				gameID = gameRepository.upsertReturningID(
					GameDomainModel(
						name = state.name,
						displayColorIndex = state.colorIndex,
						scoringMode = state.scoringMode
					)
				)
				categoryRepository.upsertReturningID(
					category = CategoryDomainModel(
						name = TextFieldValue("defaultMiscCategory"),
						position = -1
					),
					gameID = gameID
				)
			} else {
				gameRepository.upsertReturningID(
					GameDomainModel(
						id = gameID,
						name = state.name,
						displayColorIndex = state.colorIndex,
						scoringMode = state.scoringMode
					)
				)
			}
			state.categories.forEach { category ->
				categoryRepository.upsertReturningID(category, gameID)
			}
		}
		onFinish()
	}

	// region Game Details

	fun onColorClick(index: Int) = viewModelState.update {
		it.copy(colorIndex = index, showColorMenu = false)
	}

	fun onDeleteClick(onFinish: () -> Unit) = viewModelScope.launch {
		gameRepository.deleteBy(gameID)
		onFinish()
	}

	fun onNameChanged(value: TextFieldValue) = viewModelState.update {
		it.copy(name = value)
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

	fun onCategoryClick(index: Int) {
		viewModelState.update {
			it.copy(
				indexOfSelectedCategory = index,
				isCategoryDialogOpen = true
			)
		}
	}

	fun onEditButtonClick() = viewModelState.update {
		it.copy(isCategoryDialogOpen = true)
	}

	fun onCategoryInputChanged(input: TextFieldValue, index: Int) = viewModelState.update {
		val category = it.categories.getOrNull(index) ?: return@update it
		val updatedCategories = it.categories.toMutableList().apply {
			this[index] = category.copy(name = input)
		}
		it.copy(categories = updatedCategories)
	}

	fun onNewCategoryClick() = viewModelScope.launch {
		val newCategoryPosition = viewModelState.value.categories.lastIndex + 1
		val newCategory = CategoryDomainModel(
			name = TextFieldValue(),
			position = newCategoryPosition
		)

		viewModelState.update {
			val updatedCategories = it.categories.plus(newCategory)

			it.copy(
				isCategoryDialogOpen = true,
				categories = updatedCategories,
				indexOfSelectedCategory = updatedCategories.lastIndex,
			)
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

	fun onCategoryDoneClick() = viewModelState.update {
		it.copy(indexOfSelectedCategory = -1)
	}

	fun onDeleteCategoryClick(index: Int) = viewModelState.update { state ->
		val deletedCategory = state.categories[index]
		if (deletedCategory.id != -1L) {
			viewModelScope.launch {
				categoryRepository.deleteBy(deletedCategory.id)
			}
		}

		val newCategories = state.categories.toMutableList()
		newCategories.listIterator(index).apply {

			// remove the element that is being deleted
			next(); remove()

			// adjust the position property of the remaining elements
			while (hasNext()) {
				val category = next()
				val adjustedPosition = category.position - 1
				set(category.copy(position = adjustedPosition))
			}
		}

		state.copy(
			categories = newCategories,
			indexOfSelectedCategory = -1,
		)
	}

	fun onCategoryDialogDismiss() = viewModelState.update {
		it.copy(
			indexOfSelectedCategory = -1,
			isCategoryDialogOpen = false
		)
	}

	// endregion
}

private data class EditGameViewModelState(
	val loading: Boolean = true,
	val categories: List<CategoryDomainModel> = emptyList(),
	val colorIndex: Int = 0,
	val dragState: DragState = DragState(),
	val indexOfSelectedCategory: Int = -1,
	val isCategoryDialogOpen: Boolean = false,
	val name: TextFieldValue = TextFieldValue(),
	val scoringMode: ScoringMode = ScoringMode.Descending,
	val showColorMenu: Boolean = false,
) {

	fun toUiState() = if (loading) {
		EditGameUiState.Loading
	} else {
		EditGameUiState.Content(
			categories = getCategoriesAdjustedForActiveDragAction(),
			colorIndex = colorIndex,
			dragState = dragState,
			indexOfSelectedCategory = indexOfSelectedCategory,
			isCategoryDialogOpen = isCategoryDialogOpen,
			name = name,
			scoringMode = scoringMode,
			showColorMenu = showColorMenu
		)
	}

	private fun getCategoriesAdjustedForActiveDragAction(): List<CategoryDomainModel> {
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

	data object Loading : EditGameUiState

	data class Content(
		val categories: List<CategoryDomainModel>,
		val colorIndex: Int,
		val dragState: DragState,
		val indexOfSelectedCategory: Int,
		val isCategoryDialogOpen: Boolean,
		val name: TextFieldValue,
		val scoringMode: ScoringMode,
		val showColorMenu: Boolean,
	) : EditGameUiState
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
