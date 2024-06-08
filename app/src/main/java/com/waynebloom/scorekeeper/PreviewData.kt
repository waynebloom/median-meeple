package com.waynebloom.scorekeeper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.ext.toScoringMode
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataModel
import com.waynebloom.scorekeeper.room.data.model.GameDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataModel
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.MatchDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import java.math.BigDecimal

val CategoryEntitiesDefaultPreview: List<CategoryDataModel> = listOf(
    CategoryDataModel(
        id = 1,
        name = "Goals"
    ),
    CategoryDataModel(
        id = 2,
        name = "Personal Goals"
    ),
    CategoryDataModel(
        id = 3,
        name = "Nectar"
    ),
    CategoryDataModel(
        id = 4,
        name = "Eggs"
    ),
    CategoryDataModel(
        id = 5,
        name = "Tucked Cards"
    ),
    CategoryDataModel(
        id = 6,
        name = "Cached Food"
    ),
    CategoryDataModel(
        id = 7,
        name = "Birds"
    ),
    CategoryDataModel(
        id = 8,
        name = "Duet Map"
    )
)

val CategoryScoreEntitiesDefaultPreview: List<CategoryScoreDataModel> = listOf(
    CategoryScoreDataModel(
        id = 0,
        categoryId = 1,
        value = "1"
    ),
    CategoryScoreDataModel(
        id = 1,
        categoryId = 2,
        value = "1"
    ),
    CategoryScoreDataModel(
        id = 2,
        categoryId = 3,
        value = "2"
    ),
    CategoryScoreDataModel(
        id = 3,
        categoryId = 4,
        value = "4"
    ),
    CategoryScoreDataModel(
        id = 4,
        categoryId = 5,
        value = "2"
    ),
    CategoryScoreDataModel(
        id = 5,
        categoryId = 6,
        value = "1"
    ),
    CategoryScoreDataModel(
        id = 6,
        categoryId = 7,
        value = "5"
    ),
    CategoryScoreDataModel(
        id = 7,
        categoryId = 8,
        value = "3"
    )
)

val PlayerEntitiesDefaultPreview: List<PlayerDataModel> = listOf(
    PlayerDataModel(id = 0, name = "Wayne"),
    PlayerDataModel(id = 1, name = "Conor"),
    PlayerDataModel(id = 2, name = "Alyssa"),
    PlayerDataModel(id = 3, name = "Joseph"),
)

val PlayerObjectsDefaultPreview: List<PlayerDataRelationModel> = PlayerEntitiesDefaultPreview.map {
    PlayerDataRelationModel(
        entity = it,
        score = CategoryScoreEntitiesDefaultPreview,
    )
}

val MatchEntitiesDefaultPreview: List<MatchDataModel> = listOf(
    MatchDataModel(id = 0, notes = "Example notes 1"),
    MatchDataModel(id = 1, notes = "Example notes 2"),
    MatchDataModel(id = 2, notes = "Example notes 3"),
)

val MatchObjectsDefaultPreview = MatchEntitiesDefaultPreview.map {
    MatchDataRelationModel(
        entity = it,
        players = PlayerObjectsDefaultPreview
    )
}

val GameEntitiesDefaultPreview: List<GameDataModel> = listOf(
    GameDataModel(
        id = 0,
        name = "Carcassonne",
        color = "TEAL",
        scoringMode = 0
    ),
    GameDataModel(
        id = 1,
        name = "Wingspan",
        color = "BLUE",
        scoringMode = 0
    ),
    GameDataModel(
        id = 2,
        name = "Century: Golem Edition",
        color = "DEEP_ORANGE",
        scoringMode = 0
    ),
    GameDataModel(
        id = 3,
        name = "Ticket to Ride: Rails & Sails Test Test Test",
        color = "GREEN",
        scoringMode = 0
    ),
    GameDataModel(
        id = 4,
        name = "Mystic Vale",
        color = "PURPLE",
        scoringMode = 0
    ),
    GameDataModel(
        id = 5,
        name = "Agricola",
        color = "TEAL",
        scoringMode = 0
    ),
    GameDataModel(
        id = 6,
        name = "Azul: Queen's Garden",
        color = "YELLOW",
        scoringMode = 0
    ),
    GameDataModel(
        id = 7,
        name = "Catan",
        color = "TEAL",
        scoringMode = 0
    ),
)

val SampleGames: List<GameDomainModel> = GameEntitiesDefaultPreview.map {
    GameDomainModel(
        id = it.id,
        name = TextFieldValue(it.name),
        color = it.color,
        scoringMode = it.scoringMode.toScoringMode()
    )
}

val GameObjectsDefaultPreview: List<GameDataRelationModel> = GameEntitiesDefaultPreview.map {
    GameDataRelationModel(
        entity = it,
        categories = CategoryEntitiesDefaultPreview,
        matches = MatchObjectsDefaultPreview,
    )
}

val GameObjectStatisticsPreview: GameDataRelationModel = GameDataRelationModel(
    entity = GameDataModel(
        id = 0,
        name = "Wingspan",
        color = "BLUE",
        scoringMode = 1
    ),
    categories = listOf(
        CategoryDataModel(id = 0, name = "Round Goals"),
        CategoryDataModel(id = 1, name = "Duet Map"),
        CategoryDataModel(id = 2, name = "Nectar"),
        CategoryDataModel(id = 3, name = "Personal Goals"),
        CategoryDataModel(id = 4, name = "Eggs"),
        CategoryDataModel(id = 5, name = "Cached Food"),
        CategoryDataModel(id = 6, name = "Tucked Cards"),
        CategoryDataModel(id = 7, name = "Birds"),
    ),
    matches = listOf(
        MatchDataRelationModel(
            entity = MatchDataModel(
                id = 0,
                notes = "Example notes 1"
            ),
            players = listOf(
                PlayerDataRelationModel(
                    entity = PlayerDataModel(id = 0, name = "Wayne"),
                    score = listOf(
                        CategoryScoreDataModel(
                            id = 0,
                            categoryId = 0,
                            value = "5"
                        ),
                        CategoryScoreDataModel(
                            id = 1,
                            categoryId = 1,
                            value = "6"
                        ),
                        CategoryScoreDataModel(
                            id = 2,
                            categoryId = 2,
                            value = "7"
                        ),
                        CategoryScoreDataModel(
                            id = 3,
                            categoryId = 3,
                            value = "8"
                        ),
                        CategoryScoreDataModel(
                            id = 4,
                            categoryId = 4,
                            value = "9"
                        ),
                        CategoryScoreDataModel(
                            id = 5,
                            categoryId = 5,
                            value = "10"
                        ),
                        CategoryScoreDataModel(
                            id = 6,
                            categoryId = 6,
                            value = "11"
                        ),
                        CategoryScoreDataModel(
                            id = 7,
                            categoryId = 7,
                            value = "12"
                        )
                    )
                ),
                PlayerDataRelationModel(
                    entity = PlayerDataModel(id = 0, name = "Alyssa"),
                    score = listOf(
                        CategoryScoreDataModel(
                            id = 0,
                            categoryId = 0,
                            value = "10"
                        ),
                        CategoryScoreDataModel(
                            id = 1,
                            categoryId = 1,
                            value = "11"
                        ),
                        CategoryScoreDataModel(
                            id = 2,
                            categoryId = 2,
                            value = "12"
                        ),
                        CategoryScoreDataModel(
                            id = 3,
                            categoryId = 3,
                            value = "13"
                        ),
                        CategoryScoreDataModel(
                            id = 4,
                            categoryId = 4,
                            value = "14"
                        ),
                        CategoryScoreDataModel(
                            id = 5,
                            categoryId = 5,
                            value = "15"
                        ),
                        CategoryScoreDataModel(
                            id = 6,
                            categoryId = 6,
                            value = "16"
                        ),
                        CategoryScoreDataModel(
                            id = 7,
                            categoryId = 7,
                            value = "17"
                        )
                    )
                ),
            )
        ),
        MatchDataRelationModel(
            entity = MatchDataModel(
                id = 0,
                notes = "Example notes 1"
            ),
            players = listOf(
                PlayerDataRelationModel(
                    entity = PlayerDataModel(id = 0, name = "Alyssa"),
                    score = listOf(
                        CategoryScoreDataModel(
                            id = 0,
                            categoryId = 0,
                            value = "14"
                        ),
                        CategoryScoreDataModel(
                            id = 1,
                            categoryId = 1,
                            value = "13"
                        ),
                        CategoryScoreDataModel(
                            id = 2,
                            categoryId = 2,
                            value = "12"
                        ),
                        CategoryScoreDataModel(
                            id = 3,
                            categoryId = 3,
                            value = "11"
                        ),
                        CategoryScoreDataModel(
                            id = 4,
                            categoryId = 4,
                            value = "10"
                        ),
                        CategoryScoreDataModel(
                            id = 5,
                            categoryId = 5,
                            value = "9"
                        ),
                        CategoryScoreDataModel(
                            id = 6,
                            categoryId = 6,
                            value = "8"
                        ),
                        CategoryScoreDataModel(
                            id = 7,
                            categoryId = 7,
                            value = "7"
                        )
                    )
                ),
                PlayerDataRelationModel(
                    entity = PlayerDataModel(id = 0, name = "Cassie"),
                    score = listOf(
                        CategoryScoreDataModel(
                            id = 0,
                            categoryId = 0,
                            value = "5"
                        ),
                        CategoryScoreDataModel(
                            id = 1,
                            categoryId = 1,
                            value = "6"
                        ),
                        CategoryScoreDataModel(
                            id = 2,
                            categoryId = 2,
                            value = "7"
                        ),
                        CategoryScoreDataModel(
                            id = 3,
                            categoryId = 3,
                            value = "8"
                        ),
                        CategoryScoreDataModel(
                            id = 4,
                            categoryId = 4,
                            value = "9"
                        ),
                        CategoryScoreDataModel(
                            id = 5,
                            categoryId = 5,
                            value = "10"
                        ),
                        CategoryScoreDataModel(
                            id = 6,
                            categoryId = 6,
                            value = "11"
                        ),
                        CategoryScoreDataModel(
                            id = 7,
                            categoryId = 7,
                            value = "12"
                        )
                    )
                ),
            )
        ),
    )
)

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
            name = TextFieldValue("Wingspan"),
            color = "LIGHT_BLUE",
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = TextFieldValue("Splendor"),
            color = "YELLOW",
            scoringMode = ScoringMode.Descending
        ),
        GameDomainModel(
            name = TextFieldValue("Catan"),
            color = "DEEP_ORANGE",
            scoringMode = ScoringMode.Descending
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
}