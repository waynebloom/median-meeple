package com.waynebloom.scorekeeper.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import javax.inject.Inject

class PlayerDataMapper @Inject constructor(
    private val categoryScoreDataMapper: CategoryScoreDataMapper
) {

    fun map(playerData: PlayerDataModel) = PlayerDomainModel(
        id = playerData.id,
        categoryScores = listOf(),
        name = TextFieldValue(playerData.name),
        position = playerData.position,
        useCategorizedScore = playerData.showDetailedScore,
        totalScore = playerData.totalScore.toBigDecimal()
    )

    /**
     * Maps all relations recursively (players, player scores, etc).
     *
     * @param playerData The match data from db.
     * @param categories The scoring categories of the game for which this match is recorded. This
     * is used to map the category scores for each player.
     */
    fun mapWithRelations(
        playerData: PlayerDataRelationModel,
        categories: Map<Long, CategoryDomainModel>
    ) = PlayerDomainModel(
        id = playerData.entity.id,
        categoryScores = playerData.score.map {
            categoryScoreDataMapper.mapWithRelations(it, categories)
        },
        name = TextFieldValue(playerData.entity.name),
        position = playerData.entity.position,
        useCategorizedScore = playerData.entity.showDetailedScore,
        totalScore = playerData.entity.totalScore.toBigDecimal()
    )

    /**
     * Only map the first-level relations.
     *
     * @param playerData The match data from db.
     */
    fun mapWithRelations(playerData: PlayerDataRelationModel) = PlayerDomainModel(
        id = playerData.entity.id,
        categoryScores = playerData.score.map {
            categoryScoreDataMapper.map(it)
        },
        name = TextFieldValue(playerData.entity.name),
        position = playerData.entity.position,
        useCategorizedScore = playerData.entity.showDetailedScore,
        totalScore = playerData.entity.totalScore.toBigDecimal()
    )
}
