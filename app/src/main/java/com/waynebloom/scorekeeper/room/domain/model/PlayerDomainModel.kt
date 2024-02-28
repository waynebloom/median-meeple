package com.waynebloom.scorekeeper.room.domain.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import java.math.BigDecimal

data class PlayerDomainModel(
    val id: Long = 0,
    val categoryScores: List<CategoryScoreDomainModel>,
    val name: TextFieldInput,
    val position: Int,
    val showDetailedScore: Boolean,
    val totalScore: BigDecimal,
)
