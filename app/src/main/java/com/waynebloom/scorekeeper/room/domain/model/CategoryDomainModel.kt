package com.waynebloom.scorekeeper.room.domain.model

import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput

data class CategoryDomainModel(
    val id: Long = 0,
    val name: TextFieldInput,
    val position: Int
)
