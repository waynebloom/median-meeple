package com.waynebloom.scorekeeper.room.domain.mapper

import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import javax.inject.Inject

class PlayerDataMapper @Inject constructor(
    private val categoryScoreDataMapper: CategoryScoreDataMapper
) {

    fun map(playerData: PlayerDataRelationModel) = PlayerDomainModel(
        id = playerData.entity.id,
        categoryScores = listOf(),
        name = playerData.entity.name.toTextFieldInput(),
        position = playerData.entity.position,
        showDetailedScore = playerData.entity.showDetailedScore,
        totalScore = playerData.entity.totalScore.toBigDecimal()
    )

    fun mapWithRelations(
        playerData: PlayerDataRelationModel,
        categories: Map<Long, CategoryDomainModel>
    ) = PlayerDomainModel(
        id = playerData.entity.id,
        categoryScores = playerData.score.map {
            categoryScoreDataMapper.mapWithRelations(it, categories)
        },
        name = playerData.entity.name.toTextFieldInput(),
        position = playerData.entity.position,
        showDetailedScore = playerData.entity.showDetailedScore,
        totalScore = playerData.entity.totalScore.toBigDecimal()
    )
}
