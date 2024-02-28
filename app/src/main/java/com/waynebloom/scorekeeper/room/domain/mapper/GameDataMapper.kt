package com.waynebloom.scorekeeper.room.domain.mapper

import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import javax.inject.Inject

class GameDataMapper @Inject constructor(
    private val matchDataMapper: MatchDataMapper
) {

    fun mapWithRelations(gameData: GameDataRelationModel): GameDomainModel {
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
                matchDataMapper.mapWithRelations(it, categoryDomainModels)
            },
            name = gameData.entity.name.toTextFieldInput(),
            scoringMode = gameData.entity.scoringMode.toScoringMode()
        )
    }
}
