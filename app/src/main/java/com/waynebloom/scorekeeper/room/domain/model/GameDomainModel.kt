package com.waynebloom.scorekeeper.room.domain.model

import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput


data class GameDomainModel(
    val id: Long = 0,
    val categories: List<CategoryDomainModel> = listOf(),
    val color: String,
    val matches: List<MatchDomainModel> = listOf(),
    val name: TextFieldInput, // TODO: make this a string again, do the same for any other ui models
    val scoringMode: ScoringMode
)
