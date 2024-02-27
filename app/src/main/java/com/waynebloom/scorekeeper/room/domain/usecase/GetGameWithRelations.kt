package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.ui.model.CategoryScoreUiModel
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.GameDomainModel
import com.waynebloom.scorekeeper.ui.model.MatchUiModel
import com.waynebloom.scorekeeper.ui.model.PlayerUiModel
import javax.inject.Inject

class GetGameWithRelations @Inject constructor(
    private val gameRepository: GameRepository
) {

    suspend operator fun invoke(id: Long) = gameRepository.getWithRelations(id).toUiModel()

    private fun GameDataRelationModel.toUiModel(): GameDomainModel {

        val categoryUiModels = categories
            .map {
                CategoryUiModel(
                    id = it.id,
                    name = it.name.toTextFieldInput(),
                    position = it.position
                )
            }
            .associateBy { it.id }

        return GameDomainModel(
            id = entity.id,
            categories = categoryUiModels.values.toList(),
            color = entity.color,
            matches = matches.map { it.toUiModel(categoryUiModels) },
            name = entity.name.toTextFieldInput(),
            scoringMode = entity.scoringMode.toScoringMode()
        )
    }

    private fun MatchDataRelationModel.toUiModel(
        categories: Map<Long, CategoryUiModel>
    ) = MatchUiModel(
        id = entity.id,
        notes = entity.notes.toTextFieldInput(),
        players = players.map { it.toUiModel(categories) }
    )

    private fun PlayerDataRelationModel.toUiModel(
        categories: Map<Long, CategoryUiModel>
    ) = PlayerUiModel(
        id = entity.id,
        categoryScores = score.map { it.toUiModel(categories) },
        name = entity.name.toTextFieldInput(),
        position = entity.position,
        showDetailedScore = entity.showDetailedScore,
        totalScore = entity.totalScore.toBigDecimal()
    )

    private fun CategoryScoreDataModel.toUiModel(
        categories: Map<Long, CategoryUiModel>
    ) = CategoryScoreUiModel(
        category = categories.getValue(categoryId),
        score = value.toBigDecimal()
    )
}
