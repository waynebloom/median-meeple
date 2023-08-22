package com.waynebloom.scorekeeper.data.model

import com.waynebloom.scorekeeper.enums.DatabaseAction

class EntityStateBundle<T>(
    var entity: T,
    databaseAction: DatabaseAction = DatabaseAction.NO_ACTION
) {

    var databaseAction = databaseAction
        set(value) {
            if (databaseAction == DatabaseAction.NO_ACTION || value == DatabaseAction.DELETE) {
                field = value
            }
        }

    fun copy(
        entity: T = this.entity,
        databaseAction: DatabaseAction = this.databaseAction
    ) = EntityStateBundle(entity, databaseAction)
}
