package com.waynebloom.scorekeeper.ui.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput

data class MatchUiModel(
//    val timeModified: Date,
    val notes: TextFieldInput,
    val players: List<PlayerUiModel>
)