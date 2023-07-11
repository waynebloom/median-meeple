package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal

data class PlayerDomainModel(
    val id: Long = -1,
    val matchId: Long = -1,
    val categoryScores: List<CategoryScoreDomainModel> = emptyList(),
    val name: TextFieldValue = TextFieldValue("New Player"),
    val position: Int,
    val useCategorizedScore: Boolean = false,
    val totalScore: BigDecimal = BigDecimal.ZERO,
)
