package com.waynebloom.scorekeeper.room.domain.model

import java.math.BigDecimal

data class CategoryScoreDomainModel(
    val category: CategoryDomainModel,
    val score: BigDecimal
)
