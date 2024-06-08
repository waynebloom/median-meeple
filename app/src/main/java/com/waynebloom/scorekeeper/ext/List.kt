package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel

/**
 * Perform a transformation on the element at the specified index.
 *
 * @param index the index of the element being transformed
 * @param transformation the transformation to perform
 */
fun <T> MutableList<T>.transformElement(index: Int, transformation: (T) -> T) {
    this[index] = transformation(this[index])
}

fun <T> List<T>.toAdSeparatedSubLists(
    firstAdMaximumIndex: Int = 2,
    itemsBetweenAds: Int = 5
): List<List<T>> {
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

fun List<PlayerDomainModel>.getWinningPlayer(scoringMode: ScoringMode) =
    when(scoringMode) {
        ScoringMode.Ascending -> minBy { it.totalScore }
        ScoringMode.Descending -> maxBy { it.totalScore }
        ScoringMode.Manual -> minBy { it.rank }
    }
