package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DataObjectCache<T>(
    dataObject: T,
    needsUpdate: Boolean = false,
    databaseEntityId: Long = -1
) {
    var dataObject: T by mutableStateOf(dataObject)
    var needsUpdate: Boolean by mutableStateOf(needsUpdate)
    var databaseEntityId: Long by mutableStateOf(databaseEntityId)
}
