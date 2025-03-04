package com.waynebloom.scorekeeper.database.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import javax.inject.Inject

class ScoreDataMapper @Inject constructor() {

    fun mapWithRelations(
        categoryScoreData: ScoreDataModel,
        categories: Map<Long, CategoryDomainModel>
    ) = ScoreDomainModel(
        id = categoryScoreData.id,
        playerId = categoryScoreData.playerId,
        categoryId = categoryScoreData.categoryId,
        category = categories.getValue(categoryScoreData.categoryId),
        scoreAsBigDecimal = categoryScoreData.value.toBigDecimalOrNull(),
        scoreAsTextFieldValue = TextFieldValue(categoryScoreData.value)
    )

    fun map(categoryScore: ScoreDataModel) = ScoreDomainModel(
        id = categoryScore.id,
        playerId = categoryScore.playerId,
        categoryId = categoryScore.categoryId,
        scoreAsBigDecimal = categoryScore.value.toBigDecimalOrNull(),
        scoreAsTextFieldValue = TextFieldValue(categoryScore.value)
    )
}
