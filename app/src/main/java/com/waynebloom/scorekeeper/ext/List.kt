package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.database.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.enums.ScoringMode
import java.math.BigDecimal

/**
 * Perform a transformation on the element at the specified index.
 *
 * @param index the index of the element being transformed
 * @param transformation the transformation to perform
 */
fun <T> MutableList<T>.transformElement(index: Int, transformation: (T) -> T) {
	this[index] = transformation(this[index])
}

fun List<PlayerDomainModel>.getWinningPlayer(scoringMode: ScoringMode) =
	when (scoringMode) {
		ScoringMode.Ascending -> minBy { player ->
			player.categoryScores.sumOf {
				it.scoreAsBigDecimal ?: BigDecimal.ZERO
			}
		}

		ScoringMode.Descending -> maxBy { player ->
			player.categoryScores.sumOf {
				it.scoreAsBigDecimal ?: BigDecimal.ZERO
			}
		}

		ScoringMode.Manual -> minBy {
			it.position
		}
	}
