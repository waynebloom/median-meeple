package com.waynebloom.scorekeeper.scorecard

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.DeleteMatch
import com.waynebloom.scorekeeper.room.domain.usecase.DeletePlayer
import com.waynebloom.scorekeeper.room.domain.usecase.GetCategoriesByGameId
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetIndexOfMatch
import com.waynebloom.scorekeeper.room.domain.usecase.GetMatch
import com.waynebloom.scorekeeper.room.domain.usecase.GetPlayersByMatchIdWithRelations
import com.waynebloom.scorekeeper.room.domain.usecase.InsertCategoryScore
import com.waynebloom.scorekeeper.room.domain.usecase.InsertMatch
import com.waynebloom.scorekeeper.room.domain.usecase.InsertPlayer
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateCategoryScore
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateMatch
import com.waynebloom.scorekeeper.room.domain.usecase.UpdatePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    getGame: GetGame,
    getMatch: GetMatch,
    getIndexOfMatch: GetIndexOfMatch,
    getCategoriesByGameId: GetCategoriesByGameId,
    getPlayersByMatchIdWithRelations: GetPlayersByMatchIdWithRelations,
    private val updateMatch: UpdateMatch,
    private val updatePlayer: UpdatePlayer,
    private val insertPlayer: InsertPlayer,
    private val deletePlayer: DeletePlayer,
    private val updateCategoryScore: UpdateCategoryScore,
    private val insertCategoryScore: InsertCategoryScore,
    private val insertMatch: InsertMatch,
    private val deleteMatch: DeleteMatch,
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
            val game = getGame(gameId)
            dbCategories = getCategoriesByGameId(gameId)
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
                var players = getPlayersByMatchIdWithRelations(matchId)
                val match = getMatch(matchId)
                val scoreCard = players.map { player ->
                    dbCategories.map { category ->
                        val existingScore = player.categoryScores.find {
                            it.categoryId == category.id
                        }
                        existingScore ?: CategoryScoreDomainModel(
                            categoryId = category.id,
                            playerId = player.id
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
                        categoryScore.categoryId == dbCategories.last().id
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

                viewModelState.update {
                    it.copy(
                        loading = false,
                        totals = totals,
                        game = game,
                        indexOfMatch = getIndexOfMatch(gameId, matchId) + 1,
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
                viewModelState.update { state ->
                    state.copy(
                        loading = false,
                        game = game,
                        indexOfMatch = getIndexOfMatch(gameId, matchId) + 1,
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
            player.copy(rank = rank)
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
                    CategoryScoreDomainModel(
                        categoryId = dbCategories[index].id,
                    )
                }
            )
        }
        val newPlayers = it.players.plus(
            if (manualRank != -1) {
                PlayerDomainModel(name = name, rank = manualRank)
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
                deletePlayer(deletedPlayer.id)
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
                    player.copy(name = value, rank = manualRank)
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

    fun onSaveClick(onFinish: () -> Unit) = viewModelScope.launch {
        viewModelState.value.let { state ->
            if (matchId == -1L) {
                matchId = insertMatch(
                    MatchDomainModel(
                        gameId = gameId,
                        notes = state.notes.text,
                        location = state.location,
                        dateMillis = state.dateMillis,
                    )
                )
            } else {
                updateMatch(
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
                    val playerId = insertPlayer(player.copy(matchId = matchId))

                    state.scoreCard[colIndex].forEachIndexed { rowIndex, score ->
                        insertCategoryScore(score.copy(
                            playerId = playerId,
                            categoryId = dbCategories[rowIndex].id
                        ))
                    }
                } else {
                    updatePlayer(player)

                    state.scoreCard[colIndex].forEachIndexed { rowIndex, score ->
                        if (score.id == -1L) {
                            insertCategoryScore(score.copy(
                                playerId = player.id,
                                categoryId = dbCategories[rowIndex].id
                            ))
                        } else {
                            updateCategoryScore(score)
                        }
                    }
                }
            }
        }
        onFinish()
    }

    fun onDeleteClick(onFinish: () -> Unit) = viewModelScope.launch {
        deleteMatch(matchId)
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
    val scoreCard: List<List<CategoryScoreDomainModel>> = mutableListOf(),
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
        val scoreCard: List<List<CategoryScoreDomainModel>>,
        val playerIndexToChange: Int,
        val manualRanks: Boolean,
        val dialogTextFieldValue: TextFieldValue,
    ): ScoreCardUiState
}
