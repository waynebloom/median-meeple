package com.waynebloom.scorekeeper.ui.model

import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput

data class GameUiModel(
    val id: Long = 0,
    val categories: List<CategoryUiModel> = listOf(),
    val color: String,
    val matches: List<MatchUiModel> = listOf(),
    val name: TextFieldInput,
    val scoringMode: ScoringMode
)