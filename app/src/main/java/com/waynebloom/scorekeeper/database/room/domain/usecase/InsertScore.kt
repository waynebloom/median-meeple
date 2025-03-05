package com.waynebloom.scorekeeper.database.room.domain.usecase

import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import com.waynebloom.scorekeeper.database.repository.ScoreRepository
import com.waynebloom.scorekeeper.database.room.data.datasource.ScoreDao
import javax.inject.Inject

class InsertScore @Inject constructor(
    private val scoreRepository: ScoreDao
) {
    suspend operator fun invoke(categoryScore: ScoreDomainModel): Long {
        return scoreRepository.insert(
            ScoreDataModel(
                categoryId = categoryScore.categoryId,
                playerId = categoryScore.playerId,
                value = categoryScore.scoreAsTextFieldValue.text
            )
        )
    }
}
