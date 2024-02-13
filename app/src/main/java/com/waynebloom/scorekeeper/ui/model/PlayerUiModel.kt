package com.waynebloom.scorekeeper.ui.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import java.math.BigDecimal

data class PlayerUiModel(
    val id: Long = 0,
    val categoryScores: List<CategoryScoreUiModel>,
    val name: TextFieldInput,
    val position: Int,
    val showDetailedScore: Boolean,
    val totalScore: BigDecimal
)