package com.waynebloom.scorekeeper.ext

fun <T> List<T>.updateElement(predicate: (T) -> Boolean, transform: (T) -> T): List<T> {
    return map { if (predicate(it)) transform(it) else it }
}

fun <T> List<T>.toAdSeparatedListlets(): List<List<T>> {
    val result = mutableListOf<List<T>>()

    if (size <= 5) {
        return listOf(this)
    }

    result.add(subList(0, 5))
    for (i in 5..size step 10) {
        if (i + 10 > lastIndex) {
            result.add(subList(i, size))
        } else {
            result.add(subList(i, i + 10))
        }
    }

    return result
}