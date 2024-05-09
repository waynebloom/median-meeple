package com.waynebloom.scorekeeper.room.domain.model

import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput


data class GameDomainModel(
    val id: Long = 0,
    val categories: List<CategoryDomainModel> = listOf(),
    val color: String = "",
    val matches: List<MatchDomainModel> = listOf(),
    val name: TextFieldInput = TextFieldInput(),
    val scoringMode: ScoringMode = ScoringMode.Descending
)
