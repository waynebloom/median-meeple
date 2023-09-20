package com.waynebloom.scorekeeper.ui.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput

data class CategoryUiModel(
    val id: Long = 0,
    val name: TextFieldInput,
    val position: Int
)