package com.waynebloom.scorekeeper.ui

import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.ui.model.CategoryUiModel
import com.waynebloom.scorekeeper.ui.model.GameDomainModel

object PreviewData {

    val Categories = listOf(
        CategoryUiModel(
            name = "Eggs".toTextFieldInput(),
            position = 0
        ),
        CategoryUiModel(
            name = "Cached Food".toTextFieldInput(),
            position = 1
        ),
        CategoryUiModel(
            name = "Tucked Cards".toTextFieldInput(),
            position = 2
        )
    )

    val Games = listOf(
        GameDomainModel(
            name = "Wingspan".toTextFieldInput(),
            color = "LIGHT_BLUE",
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = "Splendor".toTextFieldInput(),
            color = "YELLOW",
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = "Catan".toTextFieldInput(),
            color = "DEEP_ORANGE",
            scoringMode = ScoringMode.Descending
        )
    )
}
