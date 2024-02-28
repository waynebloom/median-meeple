package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetGameWithRelationsAsFlow @Inject constructor(
    private val gameRepository: GameRepository
) {

    operator fun invoke(id: Long) = gameRepository
        .getOneWithRelationsAsFlow(id)
        .map(::mapGameDataToDomainModel)

    private fun mapGameDataToDomainModel(gameData: GameDataRelationModel): GameDomainModel {

        val categoryDomainModels = gameData.categories
            .map {
                CategoryDomainModel(
                    id = it.id,
                    name = it.name.toTextFieldInput(),
                    position = it.position
                )
            }
            .associateBy {
                it.id
            }

        return GameDomainModel(
            id = gameData.entity.id,
            categories = categoryDomainModels.values.toList(),
            color = gameData.entity.color,
            matches = gameData.matches.map {
                mapMatchDataToDomainModel(it, categoryDomainModels)
            },
            name = gameData.entity.name.toTextFieldInput(),
            scoringMode = gameData.entity.scoringMode.toScoringMode()
        )
    }

    private fun mapMatchDataToDomainModel(
        matchData: MatchDataRelationModel,
        categories: Map<Long, CategoryDomainModel>
    ) = MatchDomainModel(
        id = matchData.entity.id,
        notes = matchData.entity.notes.toTextFieldInput(),
        players = matchData.players.map {
            mapPlayerDataToDomainModel(it, categories)
        }
    )

    private fun mapPlayerDataToDomainModel(
        playerData: PlayerDataRelationModel,
        categories: Map<Long, CategoryDomainModel>
    ) = PlayerDomainModel(
        id = playerData.entity.id,
        categoryScores = playerData.score.map {
            mapCategoryScoreDataToDomainModel(it, categories)
        },
        name = playerData.entity.name.toTextFieldInput(),
        position = playerData.entity.position,
        showDetailedScore = playerData.entity.showDetailedScore,
        totalScore = playerData.entity.totalScore.toBigDecimal()
    )

    private fun mapCategoryScoreDataToDomainModel(
        categoryScoreData: CategoryScoreDataModel,
        categories: Map<Long, CategoryDomainModel>
    ) = CategoryScoreDomainModel(
        category = categories.getValue(categoryScoreData.categoryId),
        score = categoryScoreData.value.toBigDecimal()
    )
}
