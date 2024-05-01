package com.waynebloom.scorekeeper.room.domain.model

import java.math.BigDecimal

data class CategoryScoreDomainModel(
    val id: Long = -1,
    val playerId: Long = -1,
    val categoryId: Long = -1,
    val category: CategoryDomainModel? = null,
    val score: BigDecimal,
)
