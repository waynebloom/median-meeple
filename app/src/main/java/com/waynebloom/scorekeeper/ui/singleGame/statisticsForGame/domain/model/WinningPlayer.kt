package com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.domain.model

import com.waynebloom.scorekeeper.ui.singleGame.statisticsForGame.ui.model.WinningPlayerUiModel

data class WinningPlayer(
    val name: String,
    val numberOfWins: Int
) {

    fun toUiModel() = WinningPlayerUiModel(
        name = name,
        numberOfWins = numberOfWins.toString()
    )
}