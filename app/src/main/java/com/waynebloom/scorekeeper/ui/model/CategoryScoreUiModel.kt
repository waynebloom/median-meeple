package com.waynebloom.scorekeeper.ui.model

import java.math.BigDecimal

data class CategoryScoreUiModel(
    val category: CategoryUiModel,
    val score: BigDecimal
)
