package com.waynebloom.scorekeeper.database.domain.sync

import com.waynebloom.scorekeeper.database.domain.model.Action

internal interface SyncHandler {
	fun sync(change: Pair<Action, String>)
}