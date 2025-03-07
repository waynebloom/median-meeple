package com.waynebloom.scorekeeper.database.domain.model

internal data class ChangeDataModel<T>(
	val action: Action,
	val entity: T?,
)
