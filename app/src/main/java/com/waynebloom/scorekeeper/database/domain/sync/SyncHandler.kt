package com.waynebloom.scorekeeper.database.domain.sync

import com.waynebloom.scorekeeper.database.domain.model.Action
import kotlinx.serialization.json.JsonObject

internal interface SyncHandler {
	fun sync(change: Pair<Action, JsonObject>)
}