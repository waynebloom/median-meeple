package com.waynebloom.scorekeeper.ui.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import java.math.BigDecimal

data class PlayerUiModel(
    val categoryScores: List<Pair<CategoryUiModel, BigDecimal>>,
    val name: TextFieldInput,
    val position: Int,
    val showDetailedScore: Boolean,
    val totalScore: BigDecimal
)