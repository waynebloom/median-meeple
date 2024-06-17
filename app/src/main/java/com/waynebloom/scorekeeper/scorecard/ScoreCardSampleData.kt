package com.waynebloom.scorekeeper.scorecard

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import java.math.BigDecimal

object ScoreCardSampleData {
    private val shortTotals = listOf(
        BigDecimal(67),
        BigDecimal(78),
        BigDecimal(42),
        BigDecimal(67),
        BigDecimal(70),
        BigDecimal(59),
    )
    private val longTotals = listOf(
        BigDecimal(3000000),
        BigDecimal(2345678),
        BigDecimal(1234567),
        BigDecimal(23456789),
        BigDecimal(12345678),
        BigDecimal(345678),
    )
    private val shortScoreCard = listOf(
        1 until 10 step 2,
        0 until 10 step 2,
        6..10,
        10 until 20 step 2,
        11 until 20 step 2,
        1 until 10 step 2,
    ).map { row ->
        row.map { col ->
            CategoryScoreDomainModel(
                scoreAsTextFieldValue = TextFieldValue(col.toString()),
                scoreAsBigDecimal = BigDecimal(col)
            )
        }
    }
    private val longScoreCard = listOf(
        10000 until 100000 step 20000,
        0 until 100000 step 20000,
        60000..100000 step 10000,
        100000 until 200000 step 20000,
    ).map { row ->
        row.map { col ->
            CategoryScoreDomainModel(
                scoreAsTextFieldValue = TextFieldValue(col.toString()),
                scoreAsBigDecimal = BigDecimal(col)
            )
        }
    }
    val Default = ScoreCardUiState.Content(
        totals = shortTotals,
        game = GameDomainModel(name = TextFieldValue("Wingspan")),
        indexOfMatch = 43,
        dateMillis = 1620000000000L,
        location = "Wayne's House",
        notes = TextFieldValue("This was a fun game."),
        players = listOf("Wayne", "Conor", "Alyssa", "Brock", "Tim", "Benjamin").mapIndexed { index, it ->
            PlayerDomainModel(name = it, rank = index)
        },
        categoryNames = listOf("Red", "Orange", "Yellow", "Green", "Blue"),
        hiddenCategories = listOf(),
        scoreCard = shortScoreCard,
        playerIndexToChange = 0,
        manualRanks = false,
        dialogTextFieldValue = TextFieldValue(""),
    )
    val LongValues = Default.copy(
        totals = longTotals,
        game = GameDomainModel(name = TextFieldValue("Ticket to Ride: Rails and Sails")),
        categoryNames = listOf("Very Long Name", "Even Longer Name Somehow", "Tickets", "Trains", "Ships"),
        players = listOf("Benjamin", "Mr. Long Name Person", "Jaina Proudmoore", "Anduin Wrynn").mapIndexed { index, it ->
            PlayerDomainModel(name = it, rank = index)
        },
        scoreCard = longScoreCard,
    )
    val NoPlayers = Default.copy(players = emptyList(), scoreCard = emptyList())
    val OneCategory = Default.copy(scoreCard = shortScoreCard.map { it.take(1) }, categoryNames = listOf("Red"))
}