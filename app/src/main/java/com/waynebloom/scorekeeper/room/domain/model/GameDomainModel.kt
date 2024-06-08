package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.ScoringMode


data class GameDomainModel(
    val id: Long = 0,
    val categories: List<CategoryDomainModel> = listOf(),
    val color: String = "",
    val matches: List<MatchDomainModel> = listOf(),
    val name: TextFieldValue = TextFieldValue(),
    val scoringMode: ScoringMode = ScoringMode.Descending
)
