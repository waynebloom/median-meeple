package com.waynebloom.scorekeeper.database.room.domain.model

import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal

data class ScoreDomainModel(
	val id: Long = -1,
	val playerID: Long = -1,
	val categoryID: Long = -1,
	val category: CategoryDomainModel? = null,
	val scoreAsBigDecimal: BigDecimal? = null,
	val scoreAsTextFieldValue: TextFieldValue = TextFieldValue(),
)
