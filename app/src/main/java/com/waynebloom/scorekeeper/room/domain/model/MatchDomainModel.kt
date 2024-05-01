package com.waynebloom.scorekeeper.room.domain.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput

data class MatchDomainModel(
//    val timeModified: Date,
    val id: Long = 0,
    val gameId: Long = -1,
    val notes: TextFieldInput = TextFieldInput(),
    val players: List<PlayerDomainModel> = listOf()
)
