package com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model

import com.waynebloom.scorekeeper.ext.toStringForDisplay
import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.ScoringPlayerUiModel
import java.math.BigDecimal

data class ScoringPlayer(
    val name: String,
    val score: BigDecimal
) {

    fun toUiModel() = ScoringPlayerUiModel(
        name = name,
        score = score.toStringForDisplay()
    )
}