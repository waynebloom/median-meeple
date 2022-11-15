package com.waynebloom.scorekeeper.exceptions

abstract class NullCache(message: String): Exception(message)

class NullGameCache(message: String = "Attempted to access null game cache"): NullCache(message)

class NullMatchCache(message: String = "Attempted to access null match cache"): NullCache(message)