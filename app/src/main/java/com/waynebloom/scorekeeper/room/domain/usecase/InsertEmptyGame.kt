package com.waynebloom.scorekeeper.room.domain.usecase

import android.content.res.Resources
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.domain.repository.GameRepository
import javax.inject.Inject

class InsertEmptyGame @Inject constructor(
    private val gameRepository: GameRepository,
    private val resources: Resources
) {

    suspend operator fun invoke(): Long {
        val defaultName = resources.getString(R.string.default_new_game_name)
        return gameRepository.insert(game = GameDataModel(name = defaultName))
    }
}