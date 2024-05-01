package com.waynebloom.scorekeeper.room.domain.usecase

import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel


internal fun CategoryDomainModel.toDataModel(gameId: Long) = CategoryDataModel(
    id = id,
    gameId = gameId,
    name = name.text,
    position = position
)
