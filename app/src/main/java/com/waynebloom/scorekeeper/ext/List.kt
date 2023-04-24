package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.enums.ScoringMode

fun <T> List<T>.statefulUpdateElement(predicate: (T) -> Boolean, update: (T) -> Unit): List<T> {
    return map {
        if (predicate(it)) {
            update(it)
            it
        } else it
    }
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

fun List<PlayerObject>.getWinningPlayer(
    scoringModeID: Int
) = when(ScoringMode.getModeByOrdinal(scoringModeID)) {
    ScoringMode.Ascending -> minBy { it.entity.score.toBigDecimal() }
    ScoringMode.Descending -> maxBy { it.entity.score.toBigDecimal() }
    ScoringMode.Manual -> minBy { it.entity.position }
}