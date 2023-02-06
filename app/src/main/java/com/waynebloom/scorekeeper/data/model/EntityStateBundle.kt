package com.waynebloom.scorekeeper.data.model

import com.waynebloom.scorekeeper.enums.DatabaseAction

open class EntityStateBundle<T>(
    var entity: T,
    databaseAction: DatabaseAction = DatabaseAction.NO_ACTION
) {
    var databaseAction = databaseAction
        set(value) {
            if (field == DatabaseAction.NO_ACTION) field = value
        }

    open fun copy(
        entity: T = this.entity,
        databaseAction: DatabaseAction = this.databaseAction
    ) = EntityStateBundle(entity, databaseAction)
}