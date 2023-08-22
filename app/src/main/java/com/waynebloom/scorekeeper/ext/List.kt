package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.AdSpacing.firstAdMaximumIndex
import com.waynebloom.scorekeeper.ext.AdSpacing.itemsBetweenAds

private object AdSpacing {
    const val firstAdMaximumIndex = 5
    const val itemsBetweenAds = 10
}

fun <T> List<T>.statefulUpdateElement(predicate: (T) -> Boolean, update: (T) -> Unit): List<T> {
    return map {
        if (predicate(it)) {
            update(it)
            it
        } else it
    }
}

fun <T> List<T>.toAdSeparatedSubLists(): List<List<T>> {
    val result = mutableListOf<List<T>>()

    if (size <= firstAdMaximumIndex) {
        return listOf(this)
    }

    result.add(subList(0, firstAdMaximumIndex))
    for (i in firstAdMaximumIndex..size step itemsBetweenAds) {
        if (i + itemsBetweenAds > lastIndex) {
            result.add(subList(i, size))
        } else {
            result.add(subList(i, i + itemsBetweenAds))
        }
    }

    return result
}

fun List<PlayerObject>.getWinningPlayer(scoringMode: ScoringMode) =
    when(scoringMode) {
        ScoringMode.Ascending -> minBy { it.entity.score.toBigDecimal() }
        ScoringMode.Descending -> maxBy { it.entity.score.toBigDecimal() }
        ScoringMode.Manual -> minBy { it.entity.position }
    }
