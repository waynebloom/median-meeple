package com.waynebloom.scorekeeper.database.domain.model

import kotlinx.io.IOException

enum class Action {
	DELETE,
	INSERT,
	UPDATE;

	companion object {
		fun fromString(str: String): Action {
			return when (str.uppercase()) {
				DELETE.name -> DELETE
				INSERT.name -> INSERT
				UPDATE.name -> UPDATE
				else -> {
					throw IOException("Encountered an invalid database operation identifier \"$str\" while performing sync.")
				}
			}
		}
	}
}