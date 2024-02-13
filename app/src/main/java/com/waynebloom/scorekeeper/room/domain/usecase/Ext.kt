package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel


internal fun CategoryUiModel.toDataModel(gameId: Long) = CategoryDataModel(
    id = id,
    gameId = gameId,
    name = name.value.text,
    position = position
)