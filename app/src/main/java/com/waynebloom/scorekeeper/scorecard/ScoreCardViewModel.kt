package com.waynebloom.scorekeeper.scorecard

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.database.domain.CategoryRepository
import com.waynebloom.scorekeeper.database.domain.GameRepository
import com.waynebloom.scorekeeper.database.domain.MatchRepository
import com.waynebloom.scorekeeper.database.domain.PlayerRepository
import com.waynebloom.scorekeeper.database.domain.ScoreRepository
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ScoreCardViewModel @Inject constructor(
	savedStateHandle: SavedStateHandle,
	mutableStateFlowFactory: MutableStateFlowFactory,
	private val gameRepository: GameRepository,
	private val matchRepository: MatchRepository,
	private val playerRepository: PlayerRepository,
	private val categoryRepository: CategoryRepository,
	private val scoreRepository: ScoreRepository,
): ViewModel() {

	// TODO: POST-RELEASE
	//  - Allow a user to hide one or more categories.
	//  - Allow a user to un-hide the misc category.
	//  - ? Display a meeple instead of the boring circle to highlight the winner

	private val viewModelState: MutableStateFlow<ScoreCardViewModelState>
	val uiState: StateFlow<ScoreCardUiState>

	val gameId = savedStateHandle.get<Long>("gameId")!!
	private var matchId = savedStateHandle.get<Long>("matchId")!!
	private lateinit var dbCategories: List<CategoryDomainModel>

	init {
		viewModelState = mutableStateFlowFactory.newInstance(ScoreCardViewModelState())
		uiState = viewModelState
			.map(ScoreCardViewModelState::toUiState)
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.Eagerly,
				initialValue = viewModelState.value.toUiState())

		viewModelScope.launch {
			val game = gameRepository.getOne(gameId).first()
			dbCategories = categoryRepository.getByGameID(gameId).first()
			dbCategories = dbCategories

				// Display a readable name for the default category.
				.map { category ->
					if (category.position == -1) {
						val name = if (dbCategories.size > 1) {
							"Misc"
						} else {
							"Total"
						}
						category.copy(name = TextFieldValue(name))
					} else {
						category
					}
				}

				// Moving the default category to the end of the list.
				.sortedBy {
					if (it.position > -1) {
						it.position
					} else {
						Int.MAX_VALUE
					}
				}

			if (matchId != -1L) {
				var players = playerRepository.getByMatchIDWithRelations(matchId).first()
				val match = matchRepository.getOne(matchId).first()
				val scoreCard = players.map { player ->
					dbCategories.map { category ->
						val existingScore = player.categoryScores.find {
							it.categoryID == category.id
						}
						existingScore ?: ScoreDomainModel(
							categoryID = category.id,
							playerID = player.id
						)
					}
				}
				val totals = players.map { player ->
					player.categoryScores.sumOf { score ->
						score.scoreAsBigDecimal ?: BigDecimal.ZERO
					}
				}

				val miscDataExists = players.flatMap { it.categoryScores }
					.filter { categoryScore ->
						// The default category is forced to be the last in the list.
						categoryScore.categoryID == dbCategories.last().id
					}
					.any {
						(it.scoreAsBigDecimal ?: BigDecimal.ZERO) != BigDecimal.ZERO
					}

				// The default category should be hidden by default for games with custom categories.
				// If it is the only category, it should be shown.
				val hiddenCategories = if (miscDataExists || dbCategories.size == 1) {
					listOf()
				} else {
					listOf(dbCategories.lastIndex)
				}
				players = if (game.scoringMode == ScoringMode.Manual) {
					players
				} else {
					assignRanksByTotalScore(players, totals)
				}

				val indexOfMatch = matchRepository.getIndexOf(gameId, matchId).first() + 1
				viewModelState.update {
					it.copy(
						loading = false,
						totals = totals,
						game = game,
						indexOfMatch = indexOfMatch,
						dateMillis = match.dateMillis,
						location = match.location,
						notes = TextFieldValue(match.notes),
						players = players,
						categoryNames = dbCategories.map { category -> category.name.text },
						hiddenCategories = hiddenCategories,
						scoreCard = scoreCard,
						manualRanks = game.scoringMode == ScoringMode.Manual
					)
				}
			} else {
				val hiddenCategories = if (dbCategories.size == 1) {
					listOf()
				} else {
					listOf(dbCategories.lastIndex)
				}

				val indexOfMatch = matchRepository.getIndexOf(gameId, matchId).first() + 1
				viewModelState.update { state ->
					state.copy(
						loading = false,
						game = game,
						indexOfMatch = indexOfMatch,
						dateMillis = Date().time,
						categoryNames = dbCategories.map { category ->
							category.name.text
						},
						hiddenCategories = hiddenCategories,
						manualRanks = game.scoringMode == ScoringMode.Manual
					)
				}
			}
		}
	}

	private fun assignRanksByTotalScore(
		players: List<PlayerDomainModel>,
		totals: List<BigDecimal>
	): List<PlayerDomainModel> {
		return players.mapIndexed { index, player ->
			val rank = totals.count { total ->
				total > totals[index]
			}
			player.copy(position = rank)
		}
	}

	fun onPlayerClick(index: Int) = viewModelState.update {
		it.copy(
			playerIndexToChange = index,
			dialogTextFieldValue = TextFieldValue(it.players[index].name)
		)
	}

	fun onAddPlayer(name: String, manualRank: Int) = viewModelState.update {
		val newTotals = it.totals.plus(BigDecimal.ZERO)
		val newScoreCard = it.scoreCard.toMutableList().apply {
			add(
				List(dbCategories.size) { index ->
					ScoreDomainModel(
						categoryID = dbCategories[index].id,
					)
				}
			)
		}
		val newPlayers = it.players.plus(
			if (manualRank != -1) {
				PlayerDomainModel(name = name, position = manualRank)
			} else {
				PlayerDomainModel(name = name)
			}
		)
		it.copy(totals = newTotals, players = newPlayers, scoreCard = newScoreCard)
	}

	fun onDeletePlayerClick(index: Int) = viewModelState.update {
		val deletedPlayer = it.players[index]
		if (deletedPlayer.id != -1L) {
			viewModelScope.launch {
				playerRepository.deleteBy(deletedPlayer.id)
			}
		}
		val newScoreCard = it.scoreCard.toMutableList().apply {
			removeAt(index)
		}
		it.copy(
			players = it.players.minus(deletedPlayer),
			scoreCard = newScoreCard
		)
	}

	fun onDialogTextFieldChange(value: TextFieldValue) = viewModelState.update {
		it.copy(dialogTextFieldValue = value)
	}

	fun onDateChange(millis: Long) = viewModelState.update {
		it.copy(dateMillis = millis)
	}

	fun onLocationChange(value: String) = viewModelState.update {
		it.copy(location = value)
	}

	fun onNotesChange(value: TextFieldValue) = viewModelState.update {
		it.copy(notes = value)
	}

	fun onPlayerChange(value: String, manualRank: Int) = viewModelState.update {
		val newPlayers = it.players.mapIndexed { index, player ->
			if (index == it.playerIndexToChange) {
				if (manualRank != -1) {
					player.copy(name = value, position = manualRank)
				} else {
					player.copy(name = value)
				}
			} else {
				player
			}
		}
		it.copy(players = newPlayers)
	}

	fun onCellChange(value: TextFieldValue, row: Int, col: Int) = viewModelState.update {
		val newScoreCard = it.scoreCard.mapIndexed { colIndex, currentCol ->
			if (colIndex == col) {
				currentCol.mapIndexed { rowIndex, currentRow ->
					if (rowIndex == row) {
						currentRow.copy(
							scoreAsTextFieldValue = value,
							scoreAsBigDecimal = value.text.toBigDecimalOrNull()
						)
					} else {
						currentRow
					}
				}
			} else {
				currentCol
			}
		}
		val difference = (newScoreCard[col][row].scoreAsBigDecimal ?: BigDecimal.ZERO)
			.minus(it.scoreCard[col][row].scoreAsBigDecimal ?: BigDecimal.ZERO)
		val newTotals = it.totals.mapIndexed { index, total ->
			if (index == col) {
				total + difference
			} else {
				total
			}
		}
		if (it.manualRanks) {
			it.copy(totals = newTotals, scoreCard = newScoreCard)
		} else {
			it.copy(totals = newTotals, scoreCard = newScoreCard, players = assignRanksByTotalScore(it.players, newTotals))
		}
	}

	fun onSaveClick(onFinish: () -> Unit) {
		val state = viewModelState.value
		if (matchId == -1L) {
			viewModelScope.launch(Dispatchers.IO) {
				matchId = matchRepository.upsertReturningID(
					MatchDomainModel(
						gameId = gameId,
						notes = state.notes.text,
						location = state.location,
						dateMillis = state.dateMillis,
					)
				)
			}
		} else {
			matchRepository.upsert(
				MatchDomainModel(
					id = matchId,
					gameId = gameId,
					notes = state.notes.text,
					location = state.location,
					dateMillis = state.dateMillis,
				)
			)
		}

		state.players.forEachIndexed { colIndex, player ->
			if (player.id == -1L) {
				viewModelScope.launch(Dispatchers.IO) {
					val playerID = async {
						playerRepository.upsertReturningID(player.copy(matchID = matchId))
					}

					state.scoreCard[colIndex].forEachIndexed { rowIndex, score ->
						scoreRepository.upsert(score.copy(
							playerID = playerID.await(),
							categoryID = dbCategories[rowIndex].id
						))
					}
				}

			} else {
				playerRepository.upsert(player)

				state.scoreCard[colIndex].forEachIndexed { rowIndex, score ->
					if (score.id == -1L) {
						scoreRepository.upsert(score.copy(
							playerID = player.id,
							categoryID = dbCategories[rowIndex].id
						))
					} else {
						scoreRepository.upsert(score)
					}
				}
			}
		}

		onFinish()
	}

	fun onDeleteClick(onFinish: () -> Unit) {
		matchRepository.deleteBy(matchId)
		onFinish()
	}
}

private data class ScoreCardViewModelState(
	val loading: Boolean = true,
	val hiddenCategories: List<Int> = listOf(),

	val totals: List<BigDecimal> = listOf(),
	val game: GameDomainModel = GameDomainModel(),
	val indexOfMatch: Int = 0,
	val dateMillis: Long = 0,
	val location: String = "",
	val notes: TextFieldValue = TextFieldValue(),
	val players: List<PlayerDomainModel> = listOf(),
	val categoryNames: List<String> = listOf(),
	val scoreCard: List<List<ScoreDomainModel>> = mutableListOf(),
	val playerIndexToChange: Int = 0,
	val manualRanks: Boolean = false,
	val dialogTextFieldValue: TextFieldValue = TextFieldValue(),
) {

	fun toUiState() = if (loading) {
		ScoreCardUiState.Loading
	} else {
		ScoreCardUiState.Content(
			totals = totals,
			game = game,
			indexOfMatch = indexOfMatch,
			dateMillis = dateMillis,
			location = location,
			notes = notes,
			players = players,
			categoryNames = categoryNames,
			hiddenCategories = hiddenCategories,
			scoreCard = scoreCard,
			playerIndexToChange = playerIndexToChange,
			manualRanks = manualRanks,
			dialogTextFieldValue = dialogTextFieldValue,
		)
	}
}

sealed interface ScoreCardUiState {

	data object Loading: ScoreCardUiState

	data class Content(
		val totals: List<BigDecimal>,
		val game: GameDomainModel,
		val indexOfMatch: Int,
		val dateMillis: Long,
		val location: String,
		val notes: TextFieldValue,
		val players: List<PlayerDomainModel>,
		val categoryNames: List<String>,
		val hiddenCategories: List<Int>,
		val scoreCard: List<List<ScoreDomainModel>>,
		val playerIndexToChange: Int,
		val manualRanks: Boolean,
		val dialogTextFieldValue: TextFieldValue,
	): ScoreCardUiState
}
