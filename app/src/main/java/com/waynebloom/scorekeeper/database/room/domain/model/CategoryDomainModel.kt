package com.waynebloom.scorekeeper.database.room.domain.model

import androidx.compose.ui.text.input.TextFieldValue

data class CategoryDomainModel(
	val id: Long = -1,
	val name: TextFieldValue,
	val position: Int
)
