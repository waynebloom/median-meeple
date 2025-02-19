package com.waynebloom.scorekeeper.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import javax.inject.Inject

class GameDataMapper @Inject constructor(
    private val matchDataMapper: MatchDataMapper
) {

    fun map(gameData: GameDataModel): GameDomainModel {
        return GameDomainModel(
            id = gameData.id,
            displayColorIndex = gameData.color,
            name = TextFieldValue(gameData.name),
            scoringMode = gameData.scoringMode.toScoringMode()
        )
    }

    fun mapWithRelations(gameData: GameDataRelationModel?): GameDomainModel? {
        if (gameData == null) return null
        val categoryDomainModels = gameData.categories
            .map {
                CategoryDomainModel(
                    id = it.id,
                    name = TextFieldValue(it.name),
                    position = it.position
                )
            }
            .associateBy {
                it.id
            }

        return GameDomainModel(
            id = gameData.entity.id,
            categories = categoryDomainModels.values.toList(),
            displayColorIndex = gameData.entity.color,
            matches = gameData.matches.map {
                matchDataMapper.mapWithRelations(it, categoryDomainModels)
            },
            name = TextFieldValue(gameData.entity.name),
            scoringMode = gameData.entity.scoringMode.toScoringMode()
        )
    }
}
