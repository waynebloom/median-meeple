package com.waynebloom.scorekeeper.library

import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.nativead.NativeAd
import com.waynebloom.scorekeeper.admob.domain.usecase.GetMultipleAdsAsFlow
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.domain.GameRepository
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.ext.toShortFormatString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
	private val gameRepository: GameRepository,
	private val getMultipleAdsAsFlow: GetMultipleAdsAsFlow,
	mutableStateFlowFactory: MutableStateFlowFactory,
) : ViewModel() {

	private val _uiState = mutableStateFlowFactory.newInstance(LibraryViewModelState())
	val uiState = _uiState
		.onStart { observeData() }
		.map(LibraryViewModelState::toUiState)
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = _uiState.value.toUiState()
		)

	private fun observeData() {
		viewModelScope.launch(Dispatchers.IO) {

			gameRepository.getAllWithRelations().collectLatest { games ->
				val gameCards = games.filterNotNull().map { game ->
					val highScore = game.matches
						.flatMap { it.players }
						.maxOfOrNull { player ->
							player.categoryScores.sumOf {
								it.scoreAsBigDecimal ?: BigDecimal.ZERO
							}
						}
						?: BigDecimal.ZERO
					LibraryGameCard(
						id = game.id,
						name = game.name.text,
						color = GameDomainModel.DisplayColors[game.displayColorIndex],
						highScore = highScore.toShortFormatString(),
						noOfMatches = game.matches.size.toString(),
					)
				}
				_uiState.update {
					it.copy(loading = false, gameCards = gameCards)
				}
			}

			getMultipleAdsAsFlow().collectLatest { ads ->
				_uiState.update {
					it.copy(ads = ads)
				}
			}
		}
	}

	fun onSearchInputChanged(value: TextFieldValue) = _uiState.update {
		it.copy(searchInput = value)
	}
}

private data class LibraryViewModelState(
	val loading: Boolean = true,
	val gameCards: List<LibraryGameCard> = listOf(),
	val lazyGridState: LazyStaggeredGridState = LazyStaggeredGridState(),
	val searchInput: TextFieldValue = TextFieldValue(),
	val isSearchBarFocused: Boolean = false,
	val ads: List<NativeAd> = emptyList()
) {

	fun toUiState() = if (loading) {
		LibraryUiState.Loading
	} else {
		LibraryUiState.Content(
			ads = ads,
			gameCards = filterGamesWithSearchInput(),
			isSearchBarFocused = isSearchBarFocused,
			lazyGridState = lazyGridState,
			searchInput = searchInput,
		)
	}

	private fun filterGamesWithSearchInput() = gameCards.filter {
		it.name.lowercase().contains(searchInput.text.lowercase())
	}
}

sealed interface LibraryUiState {
	data object Loading : LibraryUiState
	data class Content(
		val gameCards: List<LibraryGameCard> = listOf(),
		val lazyGridState: LazyStaggeredGridState = LazyStaggeredGridState(),
		val searchInput: TextFieldValue = TextFieldValue(),
		val isSearchBarFocused: Boolean = false,
		val ads: List<NativeAd> = emptyList()
	) : LibraryUiState
}

data class LibraryGameCard(
	val id: Long,
	val name: String,
	val color: Color,
	val highScore: String,
	val noOfMatches: String,
)
