package com.waynebloom.scorekeeper.data.model

import com.waynebloom.scorekeeper.ext.toTrimmedScoreString
import java.math.BigDecimal
import java.math.RoundingMode

class ScoringStatisticsForCategory(
    var categoryTitle: String,
    data: List<Pair<String, BigDecimal>>
) {

    companion object {
        const val TopScoreSelectionSize = 5
    }

    private var sortedData: List<Pair<String, BigDecimal>>
    private var needsUpdate: Boolean
    private lateinit var topSelection: List<Pair<String, String>>
    private lateinit var low: String
    private lateinit var mean: String
    private lateinit var range: String

    init {
        sortedData = data.sortedByDescending { it.second }
        needsUpdate = true
    }

    private fun calculate() {
        if (!needsUpdate) return
        topSelection = sortedData
            .take(TopScoreSelectionSize)
            .map { Pair(it.first, it.second.toTrimmedScoreString()) }
        low = sortedData.last().second.toTrimmedScoreString()
        mean = calculateMean().toTrimmedScoreString()
        range = (sortedData.first().second - sortedData.last().second).toTrimmedScoreString()
        needsUpdate = false
    }

    fun getHigh(): String {
        calculate()
        return topSelection.first().second
    }

    fun getHighScorers() = getTopSelection().takeWhile { it.second == getHigh() }

    fun getTopSelection(): List<Pair<String, String>> {
        calculate()
        return topSelection
    }

    fun getLow(): String {
        calculate()
        return low
    }

    fun getMean(): String {
        calculate()
        return mean
    }

    fun getRange(): String {
        calculate()
        return range
    }

    private fun calculateMean() = sortedData
        .sumOf { it.second }
        .divide(sortedData.size.toBigDecimal(), 3, RoundingMode.HALF_UP)
}
