package com.waynebloom.scorekeeper.room.domain.mapper

import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import javax.inject.Inject

class CategoryScoreDataMapper @Inject constructor() {

    fun mapWithRelations(
        categoryScoreData: CategoryScoreDataModel,
        categories: Map<Long, CategoryDomainModel>
    ) = CategoryScoreDomainModel(
        category = categories.getValue(categoryScoreData.categoryId),
        score = categoryScoreData.value.toBigDecimal()
    )
}
