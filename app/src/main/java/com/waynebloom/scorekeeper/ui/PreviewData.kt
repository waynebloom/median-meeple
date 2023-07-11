package com.waynebloom.scorekeeper.ui

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import java.math.BigDecimal

object PreviewData {

    val Categories = listOf(
        CategoryDomainModel(
            name = TextFieldValue("Eggs"),
            position = 0
        ),
        CategoryDomainModel(
            name = TextFieldValue("Cached Food"),
            position = 1
        ),
        CategoryDomainModel(
            name = TextFieldValue("Tucked Cards"),
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

    val Players = listOf(
        PlayerDomainModel(
            name = TextFieldValue("Alice"),
            totalScore = BigDecimal.TEN,
            position = 0,
            useCategorizedScore = false,
            categoryScores = listOf()
        ),
        PlayerDomainModel(
            name = TextFieldValue("Bob"),
            totalScore = BigDecimal("22"),
            position = 1,
            useCategorizedScore = false,
            categoryScores = listOf()
        ),
        PlayerDomainModel(
            name = TextFieldValue("Charlie"),
            totalScore = BigDecimal("15"),
            position = 2,
            useCategorizedScore = false,
            categoryScores = listOf()
        )
    )
}
