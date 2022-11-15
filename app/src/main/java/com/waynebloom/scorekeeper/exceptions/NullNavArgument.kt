package com.waynebloom.scorekeeper.exceptions

class NullNavArgument(
    private val route: String?,
    private val argument: String
): Exception() {
    override val message: String
        get() = "Argument '$argument' is null in route '$route'."
}