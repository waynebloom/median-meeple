package com.waynebloom.scorekeeper.feature.editGame

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.waynebloom.scorekeeper.ui.constants.Dimensions
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.repository.CategoryRepository
import com.waynebloom.scorekeeper.database.repository.GameRepository
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.common.ScoringMode
import com.waynebloom.scorekeeper.util.ext.transformElement
import com.waynebloom.scorekeeper.navigation.graph.EditGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class EditGameViewModel @Inject constructor(
	private val categoryRepository: CategoryRepository,
	private val gameRepository: GameRepository,
	mutableStateFlowFactory: MutableStateFlowFactory,
	savedStateHandle: SavedStateHandle,
) : ViewModel() {

	private val gameID = savedStateHandle.toRoute<EditGame>().gameID
	private val _uiState = mutableStateFlowFactory.newInstance(EditGameViewModelState())
	val uiState = _uiState
		.onStart { loadData() }
		.map(EditGameViewModelState::toUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(500),
			initialValue = _uiState.value.toUiState()
		)

	private fun loadData() {
		if (gameID != -1L) {
			viewModelScope.launch(Dispatchers.IO) {
				val game = gameRepository
					.getOne(gameID)
					.filterNotNull()
					.first()
				val categories = categoryRepository
					.getByGameID(gameID)
					.map {
						it.filterNot { it.name.text == "defaultMiscCategory" }
							.sortedBy { it.position }
					}
					.first()

				_uiState.update {
					it.copy(
						loading = false,
						categories = categories,
						colorIndex = game.displayColorIndex,
						name = game.name,
						scoringMode = game.scoringMode
					)
				}
			}
			return
		}

		_uiState.update {
			it.copy(
				loading = false,
				scoringMode = ScoringMode.Descending
			)
		}
	}

	fun onSaveClick(onFinish: () -> Unit) {
		val state = _uiState.value
		var realGameID = gameID
		viewModelScope.launch(Dispatchers.IO) {
			if (gameID == -1L) {
				realGameID = gameRepository.upsert(
					GameDomainModel(
						name = state.name,
						displayColorIndex = state.colorIndex,
						scoringMode = state.scoringMode
					)
				)
				categoryRepository.upsert(
					category = CategoryDomainModel(
						name = TextFieldValue("defaultMiscCategory"),
						position = -1
					),
					gameID = realGameID
				)
			} else {
				gameRepository.upsert(
					GameDomainModel(
						id = gameID,
						name = state.name,
						displayColorIndex = state.colorIndex,
						scoringMode = state.scoringMode
					)
				)
			}
			state.categories.forEach { category ->
				categoryRepository.upsert(category, realGameID)
			}
		}

		onFinish()
	}

	// region Game Details

	fun onColorClick(index: Int) = _uiState.update {
		it.copy(colorIndex = index, showColorMenu = false)
	}

	fun onDeleteClick(onFinish: () -> Unit) = viewModelScope.launch {
		gameRepository.deleteBy(gameID)
		onFinish()
	}

	fun onNameChanged(value: TextFieldValue) = _uiState.update {
		it.copy(name = value)
	}

	fun onScoringModeChanged(value: ScoringMode) = _uiState.update {
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
		_uiState.update {
			it.copy(
				indexOfSelectedCategory = index,
				isCategoryDialogOpen = true
			)
		}
	}

	fun onEditButtonClick() = _uiState.update {
		it.copy(isCategoryDialogOpen = true)
	}

	fun onCategoryInputChanged(input: TextFieldValue, index: Int) = _uiState.update {
		val category = it.categories.getOrNull(index) ?: return@update it
		val updatedCategories = it.categories.toMutableList().apply {
			this[index] = category.copy(name = input)
		}
		it.copy(categories = updatedCategories)
	}

	fun onNewCategoryClick() {
		val newCategoryPosition = _uiState.value.categories.lastIndex + 1
		val newCategory = CategoryDomainModel(
			name = TextFieldValue(),
			position = newCategoryPosition
		)

		_uiState.update {
			val updatedCategories = it.categories.plus(newCategory)

			it.copy(
				isCategoryDialogOpen = true,
				categories = updatedCategories,
				indexOfSelectedCategory = updatedCategories.lastIndex,
			)
		}
	}

	fun onDrag(dragDistance: Offset, rowHeight: Float) = _uiState.update {
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
		val dragResult = _uiState.value.dragState.let { it.dragStart to it.dragEnd }

		_uiState.update { state ->
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

	fun onDragStart(index: Int) = _uiState.update {
		it.copy(dragState = it.dragState.copy(dragStart = index))
	}

	fun onCategoryDoneClick() = _uiState.update {
		it.copy(indexOfSelectedCategory = -1)
	}

	fun onDeleteCategoryClick(index: Int) = _uiState.update { state ->
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

	fun onCategoryDialogDismiss() = _uiState.update {
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
