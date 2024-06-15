package com.waynebloom.scorekeeper.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import javax.inject.Inject

class CategoryScoreDataMapper @Inject constructor() {

    fun mapWithRelations(
        categoryScoreData: CategoryScoreDataModel,
        categories: Map<Long, CategoryDomainModel>
    ) = CategoryScoreDomainModel(
        id = categoryScoreData.id,
        playerId = categoryScoreData.playerId,
        categoryId = categoryScoreData.categoryId,
        category = categories.getValue(categoryScoreData.categoryId),
        scoreAsBigDecimal = categoryScoreData.value.toBigDecimalOrNull(),
        scoreAsTextFieldValue = TextFieldValue(categoryScoreData.value)
    )

    fun map(categoryScore: CategoryScoreDataModel) = CategoryScoreDomainModel(
        id = categoryScore.id,
        playerId = categoryScore.playerId,
        categoryId = categoryScore.categoryId,
        scoreAsBigDecimal = categoryScore.value.toBigDecimalOrNull(),
        scoreAsTextFieldValue = TextFieldValue(categoryScore.value)
    )
}
