package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class GetGameWithRelations @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(id: Long) = gameRepository.getOneWithRelations(id).toDomainModel()

    private fun GameDataRelationModel.toDomainModel(): GameDomainModel {

        val categoryDomainModels = categories
            .map {
                CategoryDomainModel(
                    id = it.id,
                    name = it.name.toTextFieldInput(),
                    position = it.position
                )
            }
            .associateBy { it.id }

        return GameDomainModel(
            id = entity.id,
            categories = categoryDomainModels.values.toList(),
            color = entity.color,
            matches = matches.map { it.toDomainModel(categoryDomainModels) },
            name = entity.name.toTextFieldInput(),
            scoringMode = entity.scoringMode.toScoringMode()
        )
    }

    private fun MatchDataRelationModel.toDomainModel(
        categories: Map<Long, CategoryDomainModel>
    ) = MatchDomainModel(
        id = entity.id,
        notes = entity.notes.toTextFieldInput(),
        players = players.map { it.toDomainModel(categories) }
    )

    private fun PlayerDataRelationModel.toDomainModel(
        categories: Map<Long, CategoryDomainModel>
    ) = PlayerDomainModel(
        id = entity.id,
        categoryScores = score.map { it.toDomainModel(categories) },
        name = entity.name.toTextFieldInput(),
        position = entity.position,
        showDetailedScore = entity.showDetailedScore,
        totalScore = entity.totalScore.toBigDecimal()
    )

    private fun CategoryScoreDataModel.toDomainModel(
        categories: Map<Long, CategoryDomainModel>
    ) = CategoryScoreDomainModel(
        category = categories.getValue(categoryId),
        score = value.toBigDecimal()
    )
}
