package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal

data class CategoryScoreDomainModel(
    val id: Long = -1,
    val playerId: Long = -1,
    val categoryId: Long = -1,
    val category: CategoryDomainModel? = null,
    val scoreAsBigDecimal: BigDecimal? = null,
    val scoreAsTextFieldValue: TextFieldValue = TextFieldValue(),
)
