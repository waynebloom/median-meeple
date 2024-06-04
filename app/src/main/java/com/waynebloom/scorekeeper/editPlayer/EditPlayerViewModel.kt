package com.waynebloom.scorekeeper.editPlayer

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waynebloom.scorekeeper.dagger.factory.MutableStateFlowFactory
import com.waynebloom.scorekeeper.enums.ValidityState
import com.waynebloom.scorekeeper.ext.isValidBigDecimal
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.usecase.DeletePlayer
import com.waynebloom.scorekeeper.room.domain.usecase.GetCategoriesByGameId
import com.waynebloom.scorekeeper.room.domain.usecase.GetCategoryScoresByPlayerIdAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.GetGame
import com.waynebloom.scorekeeper.room.domain.usecase.GetPlayerWithRelationsAsFlow
import com.waynebloom.scorekeeper.room.domain.usecase.GetPlayersByMatchId
import com.waynebloom.scorekeeper.room.domain.usecase.InsertCategoryScore
import com.waynebloom.scorekeeper.room.domain.usecase.InsertPlayer
import com.waynebloom.scorekeeper.room.domain.usecase.UpdateCategoryScore
import com.waynebloom.scorekeeper.room.domain.usecase.UpdatePlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class EditPlayerViewModel @Inject constructor(
    mutableStateFlowFactory: MutableStateFlowFactory,
    savedStateHandle: SavedStateHandle,
    getPlayerWithRelationsAsFlow: GetPlayerWithRelationsAsFlow,
    getCategoryScoresByPlayerIdAsFlow: GetCategoryScoresByPlayerIdAsFlow,
    getGame: GetGame,
    getCategoriesByGameId: GetCategoriesByGameId,
    getPlayersByMatchId: GetPlayersByMatchId,
    private val insertPlayer: InsertPlayer,
    private val updatePlayer: UpdatePlayer,
    private val deletePlayer: DeletePlayer,
    private val insertCategoryScore: InsertCategoryScore,
    private val updateCategoryScore: UpdateCategoryScore,
): ViewModel() {

    private val viewModelState: MutableStateFlow<EditPlayerViewModelState>
    val uiState: StateFlow<EditPlayerUiState>

    val gameId = savedStateHandle.get<Long>("gameId")!!
    private val matchId = savedStateHandle.get<Long>("matchId")!!
    private var playerId = savedStateHandle.get<Long>("playerId")!!
    private val dataCollectorJobs: List<Job>

    init {
        viewModelState = mutableStateFlowFactory.newInstance(EditPlayerViewModelState())
        uiState = viewModelState
            .map(EditPlayerViewModelState::toUiState)
            .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

        with(viewModelScope) {

            var numberOfPlayers = 0
            val numberOfPlayersJob = launch {
                numberOfPlayers = getPlayersByMatchId(matchId).size
            }

            val insertNewPlayerJob = if (playerId == -1L) {
                launch {
                    numberOfPlayersJob.join()
                    playerId = insertPlayer(
                        PlayerDomainModel(
                            matchId = matchId,
                            rank = numberOfPlayers + 1,
                        )
                    )
                }
            } else {
                null
            }

            dataCollectorJobs = listOf(
                launch {
                    numberOfPlayersJob.join()
                    insertNewPlayerJob?.join()

                    getPlayerWithRelationsAsFlow(playerId).collectLatest { player ->

                        viewModelState.update {
                            it.copy(
                                game = getGame(gameId),
                                numberOfPlayers = numberOfPlayers,
                                name = TextFieldValue(player.name),
                                rank = TextFieldValue(player.rank.toString()),
                                useCategorizedScore = player.useCategorizedScore,
                                totalScore = TextFieldValue(player.totalScore.toString()),
                            )
                        }
                    }
                },
                launch {
                    insertNewPlayerJob?.join()

                    getCategoryScoresByPlayerIdAsFlow(playerId).collectLatest { categoryScores ->
                        val categories = getCategoriesByGameId(gameId).sortedBy { it.position }
                        val categoryScoresSorted = categories.map { category ->
                            categoryScores.find { it.categoryId == category.id }
                                ?: CategoryScoreDomainModel(
                                    categoryId = category.id,
                                    scoreAsBigDecimal = BigDecimal.ZERO
                                )
                        }

                        viewModelState.update {
                            it.copy(
                                categories = categories,
                                categoryScores = categoryScoresSorted,
                                categoryScoresAsText = categoryScoresSorted.map { categoryScore ->
                                    TextFieldValue(categoryScore.scoreAsBigDecimal.toString())
                                },
                                categoryScoreValidityStates = categoryScoresSorted.map {
                                    ValidityState.Valid
                                },
                            )
                        }
                    }
                }
            )
        }
    }

    fun onNameChange(value: TextFieldValue) = viewModelState.update {
        it.copy(name = value, isNameValid = value.text.isNotBlank())
    }

    fun onRankChange(value: TextFieldValue) {
        val rankAsInt = value.text.toIntOrNull()
        val isRankValid = rankAsInt != null && rankAsInt > 0 && rankAsInt <= viewModelState.value.numberOfPlayers
        viewModelState.update {
            it.copy(rank = value, isRankValid = isRankValid)
        }
    }

    fun onTotalScoreChange(value: TextFieldValue) = viewModelState.update {
        val validityState = value.text.isValidBigDecimal()
        it.copy(totalScore = value, totalScoreValidityState = validityState)
    }

    fun onCategoryScoreChange(index: Int, value: TextFieldValue) = viewModelState.update {
        val validityState = value.text.isValidBigDecimal()
        it.copy(
            categoryScoresAsText = it.categoryScoresAsText.toMutableList().apply {
                this[index] = value
            },
            categoryScoreValidityStates = it.categoryScoreValidityStates.toMutableList().apply {
                this[index] = validityState
            }
        )
    }

    fun onUseCategorizedScoreToggle() = viewModelState.update {
        it.copy(useCategorizedScore = !it.useCategorizedScore)
    }

    fun onSaveClick() {
        with(viewModelState.value) {

            val totalScore = if (useCategorizedScore) {
                categoryScoresAsText.sumOf { it.text.toBigDecimal() }
            } else {
                totalScore.text.toBigDecimal()
            }

            val player = PlayerDomainModel(
                id = playerId,
                matchId = matchId,
                name = name.text,
                rank = rank.text.toIntOrNull() ?: -1,
                useCategorizedScore = useCategorizedScore,
                totalScore = totalScore,
            )

            val categoryScores = categoryScores.mapIndexed { index, score ->
                score.copy(
                    playerId = playerId,
                    scoreAsBigDecimal = categoryScoresAsText[index].text.toBigDecimal()
                )
            }

            viewModelScope.launch {
                updatePlayer(player)
                categoryScores.forEach {
                    if (it.id != -1L) {
                        updateCategoryScore(it)
                    } else {
                        insertCategoryScore(it)
                    }
                }
            }
        }
    }

    fun onDeleteClick() = viewModelScope.launch {
        dataCollectorJobs.forEach { it.cancel() }
        deletePlayer(playerId)
    }
}

private data class EditPlayerViewModelState(
    val game: GameDomainModel = GameDomainModel(),
    val numberOfPlayers: Int = 0,
    val categoryScores: List<CategoryScoreDomainModel> = emptyList(),

    val name: TextFieldValue = TextFieldValue(""),
    val isNameValid: Boolean = true,
    val rank: TextFieldValue = TextFieldValue(""),
    val isRankValid: Boolean = true,
    val useCategorizedScore: Boolean = false,
    val categories: List<CategoryDomainModel> = emptyList(),
    val categoryScoresAsText: List<TextFieldValue> = emptyList(),
    val categoryScoreValidityStates: List<ValidityState> = emptyList(),
    val totalScore: TextFieldValue = TextFieldValue("0"),
    val totalScoreValidityState: ValidityState = ValidityState.Valid,
    val isScoreDataValid: Boolean = true,
) {
    fun toUiState(): EditPlayerUiState {
        return EditPlayerUiState(
            game = game,
            numberOfPlayers = numberOfPlayers,
            name = name,
            isNameValid = isNameValid,
            rank = rank,
            isRankValid = isRankValid,
            useCategorizedScore = useCategorizedScore,
            categories = categories,
            categoryScores = categoryScoresAsText,
            categoryScoreValidityStates = categoryScoreValidityStates,
            totalScore = totalScore,
            totalScoreValidityState = totalScoreValidityState,
            isScoreDataValid = isScoreDataValid,
        )
    }
}

data class EditPlayerUiState(
    val game: GameDomainModel = GameDomainModel(),
    val numberOfPlayers: Int = 0,
    val name: TextFieldValue = TextFieldValue(""),
    val isNameValid: Boolean = true,
    val rank: TextFieldValue = TextFieldValue(""),
    val isRankValid: Boolean = true,
    val useCategorizedScore: Boolean = false,
    val categories: List<CategoryDomainModel> = emptyList(),
    val categoryScores: List<TextFieldValue> = emptyList(),
    val categoryScoreValidityStates: List<ValidityState> = emptyList(),
    val totalScore: TextFieldValue = TextFieldValue("0"),
    val totalScoreValidityState: ValidityState = ValidityState.Valid,
    val isScoreDataValid: Boolean = true,
)
