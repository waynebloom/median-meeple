package com.waynebloom.scorekeeper.data

import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.CategoryScoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity

val SubscoreEntitiesDefaultPreview: List<CategoryScoreEntity> = listOf(
    CategoryScoreEntity(
        id = 0,
        categoryTitleId = 1,
        value = "1"
    ),
    CategoryScoreEntity(
        id = 1,
        categoryTitleId = 2,
        value = "1"
    ),
    CategoryScoreEntity(
        id = 2,
        categoryTitleId = 3,
        value = "2"
    ),
    CategoryScoreEntity(
        id = 3,
        categoryTitleId = 4,
        value = "4"
    ),
    CategoryScoreEntity(
        id = 4,
        categoryTitleId = 5,
        value = "2"
    ),
    CategoryScoreEntity(
        id = 5,
        categoryTitleId = 6,
        value = "1"
    ),
    CategoryScoreEntity(
        id = 6,
        categoryTitleId = 7,
        value = "5"
    ),
    CategoryScoreEntity(
        id = 7,
        categoryTitleId = 8,
        value = "3"
    )
)

val PlayerEntitiesDefaultPreview: List<PlayerEntity> = listOf(
    PlayerEntity(
        id = 0,
        name = "Wayne",
        score = "32"
    ),
    PlayerEntity(
        id = 1,
        name = "Conor",
        score = "37"
    ),
    PlayerEntity(
        id = 2,
        name = "Alyssa",
        score = "45"
    ),
    PlayerEntity(
        id = 3,
        name = "Joseph",
        score = "22"
    )
)

val PlayerObjectsDefaultPreview: List<PlayerObject> = PlayerEntitiesDefaultPreview.map {
    PlayerObject(
        entity = it,
        score = SubscoreEntitiesDefaultPreview,
    )
}

val MatchEntitiesDefaultPreview: List<MatchEntity> = listOf(
    MatchEntity(
        id = 0,
        matchNotes = "Example notes 1"
    ),
    MatchEntity(
        id = 1,
        matchNotes = "Example notes 2"
    ),
    MatchEntity(
        id = 2,
        matchNotes = "Example notes 3"
    ),
)

val MatchObjectsDefaultPreview = MatchEntitiesDefaultPreview.map {
    MatchObject(
        entity = it,
        players = PlayerObjectsDefaultPreview
    )
}

val SubscoreTitleEntitiesDefaultPreview: List<CategoryTitleEntity> = listOf(
    CategoryTitleEntity(
        id = 1,
        title = "Goals"
    ),
    CategoryTitleEntity(
        id = 2,
        title = "Personal Goals"
    ),
    CategoryTitleEntity(
        id = 3,
        title = "Nectar"
    ),
    CategoryTitleEntity(
        id = 4,
        title = "Eggs"
    ),
    CategoryTitleEntity(
        id = 5,
        title = "Tucked Cards"
    ),
    CategoryTitleEntity(
        id = 6,
        title = "Cached Food"
    ),
    CategoryTitleEntity(
        id = 7,
        title = "Birds"
    ),
    CategoryTitleEntity(
        id = 8,
        title = "Duet Map"
    )
)

val GameEntitiesDefaultPreview: List<GameEntity> = listOf(
    GameEntity(
        id = 0,
        name = "Carcassonne",
        color = "TEAL",
        scoringMode = 0
    ),
    GameEntity(
        id = 1,
        name = "Wingspan",
        color = "BLUE",
        scoringMode = 0
    ),
    GameEntity(
        id = 2,
        name = "Century: Golem Edition",
        color = "DEEP_ORANGE",
        scoringMode = 0
    ),
    GameEntity(
        id = 3,
        name = "Ticket to Ride: Rails & Sails Test Test Test",
        color = "GREEN",
        scoringMode = 0
    ),
    GameEntity(
        id = 4,
        name = "Mystic Vale",
        color = "PURPLE",
        scoringMode = 0
    ),
    GameEntity(
        id = 5,
        name = "Agricola",
        color = "TEAL",
        scoringMode = 0
    ),
    GameEntity(
        id = 6,
        name = "Azul: Queen's Garden",
        color = "YELLOW",
        scoringMode = 0
    ),
    GameEntity(
        id = 7,
        name = "Catan",
        color = "TEAL",
        scoringMode = 0
    ),
)

val GameObjectsDefaultPreview: List<GameObject> = GameEntitiesDefaultPreview.map {
    GameObject(
        entity = it,
        subscoreTitles = SubscoreTitleEntitiesDefaultPreview,
        matches = MatchObjectsDefaultPreview,
    )
}

val GameObjectStatisticsPreview: GameObject = GameObject(
    entity = GameEntity(
        id = 0,
        name = "Wingspan",
        color = "BLUE",
        scoringMode = 1
    ),
    subscoreTitles = listOf(
        CategoryTitleEntity(id = 0, title = "Round Goals"),
        CategoryTitleEntity(id = 1, title = "Duet Map"),
        CategoryTitleEntity(id = 2, title = "Nectar"),
        CategoryTitleEntity(id = 3, title = "Personal Goals"),
        CategoryTitleEntity(id = 4, title = "Eggs"),
        CategoryTitleEntity(id = 5, title = "Cached Food"),
        CategoryTitleEntity(id = 6, title = "Tucked Cards"),
        CategoryTitleEntity(id = 7, title = "Birds"),
    ),
    matches = listOf(
        MatchObject(
            entity = MatchEntity(
                id = 0,
                matchNotes = "Example notes 1"
            ),
            players = listOf(
                PlayerObject(
                    entity = PlayerEntity(
                        id = 0,
                        name = "Wayne",
                        score = "32"
                    ),
                    score = listOf(
                        CategoryScoreEntity(
                            id = 0,
                            categoryTitleId = 0,
                            value = "5"
                        ),
                        CategoryScoreEntity(
                            id = 1,
                            categoryTitleId = 1,
                            value = "6"
                        ),
                        CategoryScoreEntity(
                            id = 2,
                            categoryTitleId = 2,
                            value = "7"
                        ),
                        CategoryScoreEntity(
                            id = 3,
                            categoryTitleId = 3,
                            value = "8"
                        ),
                        CategoryScoreEntity(
                            id = 4,
                            categoryTitleId = 4,
                            value = "9"
                        ),
                        CategoryScoreEntity(
                            id = 5,
                            categoryTitleId = 5,
                            value = "10"
                        ),
                        CategoryScoreEntity(
                            id = 6,
                            categoryTitleId = 6,
                            value = "11"
                        ),
                        CategoryScoreEntity(
                            id = 7,
                            categoryTitleId = 7,
                            value = "12"
                        )
                    )
                ),
                PlayerObject(
                    entity = PlayerEntity(
                        id = 0,
                        name = "Alyssa",
                        score = "72"
                    ),
                    score = listOf(
                        CategoryScoreEntity(
                            id = 0,
                            categoryTitleId = 0,
                            value = "10"
                        ),
                        CategoryScoreEntity(
                            id = 1,
                            categoryTitleId = 1,
                            value = "11"
                        ),
                        CategoryScoreEntity(
                            id = 2,
                            categoryTitleId = 2,
                            value = "12"
                        ),
                        CategoryScoreEntity(
                            id = 3,
                            categoryTitleId = 3,
                            value = "13"
                        ),
                        CategoryScoreEntity(
                            id = 4,
                            categoryTitleId = 4,
                            value = "14"
                        ),
                        CategoryScoreEntity(
                            id = 5,
                            categoryTitleId = 5,
                            value = "15"
                        ),
                        CategoryScoreEntity(
                            id = 6,
                            categoryTitleId = 6,
                            value = "16"
                        ),
                        CategoryScoreEntity(
                            id = 7,
                            categoryTitleId = 7,
                            value = "17"
                        )
                    )
                ),
            )
        ),
        MatchObject(
            entity = MatchEntity(
                id = 0,
                matchNotes = "Example notes 1"
            ),
            players = listOf(
                PlayerObject(
                    entity = PlayerEntity(
                        id = 0,
                        name = "Alyssa",
                        score = "88"
                    ),
                    score = listOf(
                        CategoryScoreEntity(
                            id = 0,
                            categoryTitleId = 0,
                            value = "14"
                        ),
                        CategoryScoreEntity(
                            id = 1,
                            categoryTitleId = 1,
                            value = "13"
                        ),
                        CategoryScoreEntity(
                            id = 2,
                            categoryTitleId = 2,
                            value = "12"
                        ),
                        CategoryScoreEntity(
                            id = 3,
                            categoryTitleId = 3,
                            value = "11"
                        ),
                        CategoryScoreEntity(
                            id = 4,
                            categoryTitleId = 4,
                            value = "10"
                        ),
                        CategoryScoreEntity(
                            id = 5,
                            categoryTitleId = 5,
                            value = "9"
                        ),
                        CategoryScoreEntity(
                            id = 6,
                            categoryTitleId = 6,
                            value = "8"
                        ),
                        CategoryScoreEntity(
                            id = 7,
                            categoryTitleId = 7,
                            value = "7"
                        )
                    )
                ),
                PlayerObject(
                    entity = PlayerEntity(
                        id = 0,
                        name = "Cassie",
                        score = "89"
                    ),
                    score = listOf(
                        CategoryScoreEntity(
                            id = 0,
                            categoryTitleId = 0,
                            value = "5"
                        ),
                        CategoryScoreEntity(
                            id = 1,
                            categoryTitleId = 1,
                            value = "6"
                        ),
                        CategoryScoreEntity(
                            id = 2,
                            categoryTitleId = 2,
                            value = "7"
                        ),
                        CategoryScoreEntity(
                            id = 3,
                            categoryTitleId = 3,
                            value = "8"
                        ),
                        CategoryScoreEntity(
                            id = 4,
                            categoryTitleId = 4,
                            value = "9"
                        ),
                        CategoryScoreEntity(
                            id = 5,
                            categoryTitleId = 5,
                            value = "10"
                        ),
                        CategoryScoreEntity(
                            id = 6,
                            categoryTitleId = 6,
                            value = "11"
                        ),
                        CategoryScoreEntity(
                            id = 7,
                            categoryTitleId = 7,
                            value = "12"
                        )
                    )
                ),
            )
        ),
    )
)
