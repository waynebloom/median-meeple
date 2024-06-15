package com.waynebloom.scorekeeper.singleGame

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import java.math.BigDecimal

object SingleGameSampleData {

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

    val CategoryScores = listOf(
        CategoryScoreDomainModel(
            category = Categories[0],
            scoreAsBigDecimal = BigDecimal(20),
            scoreAsTextFieldValue = TextFieldValue("20")
        ),
        CategoryScoreDomainModel(
            category = Categories[1],
            scoreAsBigDecimal = BigDecimal(30),
            scoreAsTextFieldValue = TextFieldValue("30")
        ),
        CategoryScoreDomainModel(
            category = Categories[2],
            scoreAsBigDecimal = BigDecimal(40),
            scoreAsTextFieldValue = TextFieldValue("40")
        )
    )

    val Players = listOf(
        PlayerDomainModel(
            name = "Alice",
            rank = 0,
            categoryScores = CategoryScores
        ),
        PlayerDomainModel(
            name = "Bob",
            rank = 1,
            categoryScores = CategoryScores
        ),
        PlayerDomainModel(
            name = "Charlie",
            rank = 2,
            categoryScores = CategoryScores
        )
    )

    val Matches = listOf(
        MatchDomainModel(
            id = 0,
            players = Players,
            notes = "Sample notes",
            location = "Home",
            dateMillis = 1630000000000,
        ),
        MatchDomainModel(
            id = 1,
            players = Players,
            notes = "Sample notes",
            location = "Conor's",
            dateMillis = 1630000099999,
        ),
        MatchDomainModel(
            id = 2,
            players = Players,
            notes = "Sample notes",
            location = "Tim's",
            dateMillis = 1630000999999,
        )
    )

    val Normal = SingleGameViewModelState(
        loading = false,
        nameOfGame = "Wingspan",
        matches = Matches,
    )
    val Loading = SingleGameViewModelState()
    val LongGameName = SingleGameViewModelState(
        loading = false,
        nameOfGame = "Ticket to Ride: Rails & Sails",
        matches = Matches,
    )
    val NoMatches = SingleGameViewModelState(
        loading = false,
        nameOfGame = "Catan",
        matches = emptyList(),
    )
    val EmptySearch = SingleGameViewModelState(
        loading = false,
        nameOfGame = "Catan",
        matches = Matches,
        searchValue = TextFieldValue("searching"),
    )
}