package com.waynebloom.scorekeeper

import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity

val SubscoreEntitiesDefaultPreview: List<SubscoreEntity> = listOf(
    SubscoreEntity(
        id = 0,
        subscoreTitleId = 1,
        value = "1"
    ),
    SubscoreEntity(
        id = 1,
        subscoreTitleId = 2,
        value = "1"
    ),
    SubscoreEntity(
        id = 2,
        subscoreTitleId = 3,
        value = "2"
    ),
    SubscoreEntity(
        id = 3,
        subscoreTitleId = 4,
        value = "4"
    ),
    SubscoreEntity(
        id = 4,
        subscoreTitleId = 5,
        value = "2"
    ),
    SubscoreEntity(
        id = 5,
        subscoreTitleId = 6,
        value = "1"
    ),
    SubscoreEntity(
        id = 6,
        subscoreTitleId = 7,
        value = "5"
    ),
    SubscoreEntity(
        id = 7,
        subscoreTitleId = 8,
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

val SubscoreTitleEntitiesDefaultPreview: List<SubscoreTitleEntity> = listOf(
    SubscoreTitleEntity(
        id = 1,
        title = "Goals"
    ),
    SubscoreTitleEntity(
        id = 2,
        title = "Personal Goals"
    ),
    SubscoreTitleEntity(
        id = 3,
        title = "Nectar"
    ),
    SubscoreTitleEntity(
        id = 4,
        title = "Eggs"
    ),
    SubscoreTitleEntity(
        id = 5,
        title = "Tucked Cards"
    ),
    SubscoreTitleEntity(
        id = 6,
        title = "Cached Food"
    ),
    SubscoreTitleEntity(
        id = 7,
        title = "Birds"
    ),
    SubscoreTitleEntity(
        id = 8,
        title = "Duet Map"
    )
)

val GameEntitiesDefaultPreview: List<GameEntity> = listOf(
    GameEntity(
        id = 0,
        name = "Carcassonne",
        color = "RED",
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
        name = "PINK",
        color = "RED",
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
        SubscoreTitleEntity(id = 0, title = "Round Goals"),
        SubscoreTitleEntity(id = 1, title = "Duet Map"),
        SubscoreTitleEntity(id = 2, title = "Nectar"),
        SubscoreTitleEntity(id = 3, title = "Personal Goals"),
        SubscoreTitleEntity(id = 4, title = "Eggs"),
        SubscoreTitleEntity(id = 5, title = "Cached Food"),
        SubscoreTitleEntity(id = 6, title = "Tucked Cards"),
        SubscoreTitleEntity(id = 7, title = "Birds"),
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
                        SubscoreEntity(
                            id = 0,
                            subscoreTitleId = 0,
                            value = "5"
                        ),
                        SubscoreEntity(
                            id = 1,
                            subscoreTitleId = 1,
                            value = "6"
                        ),
                        SubscoreEntity(
                            id = 2,
                            subscoreTitleId = 2,
                            value = "7"
                        ),
                        SubscoreEntity(
                            id = 3,
                            subscoreTitleId = 3,
                            value = "8"
                        ),
                        SubscoreEntity(
                            id = 4,
                            subscoreTitleId = 4,
                            value = "9"
                        ),
                        SubscoreEntity(
                            id = 5,
                            subscoreTitleId = 5,
                            value = "10"
                        ),
                        SubscoreEntity(
                            id = 6,
                            subscoreTitleId = 6,
                            value = "11"
                        ),
                        SubscoreEntity(
                            id = 7,
                            subscoreTitleId = 7,
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
                        SubscoreEntity(
                            id = 0,
                            subscoreTitleId = 0,
                            value = "10"
                        ),
                        SubscoreEntity(
                            id = 1,
                            subscoreTitleId = 1,
                            value = "11"
                        ),
                        SubscoreEntity(
                            id = 2,
                            subscoreTitleId = 2,
                            value = "12"
                        ),
                        SubscoreEntity(
                            id = 3,
                            subscoreTitleId = 3,
                            value = "13"
                        ),
                        SubscoreEntity(
                            id = 4,
                            subscoreTitleId = 4,
                            value = "14"
                        ),
                        SubscoreEntity(
                            id = 5,
                            subscoreTitleId = 5,
                            value = "15"
                        ),
                        SubscoreEntity(
                            id = 6,
                            subscoreTitleId = 6,
                            value = "16"
                        ),
                        SubscoreEntity(
                            id = 7,
                            subscoreTitleId = 7,
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
                        SubscoreEntity(
                            id = 0,
                            subscoreTitleId = 0,
                            value = "14"
                        ),
                        SubscoreEntity(
                            id = 1,
                            subscoreTitleId = 1,
                            value = "13"
                        ),
                        SubscoreEntity(
                            id = 2,
                            subscoreTitleId = 2,
                            value = "12"
                        ),
                        SubscoreEntity(
                            id = 3,
                            subscoreTitleId = 3,
                            value = "11"
                        ),
                        SubscoreEntity(
                            id = 4,
                            subscoreTitleId = 4,
                            value = "10"
                        ),
                        SubscoreEntity(
                            id = 5,
                            subscoreTitleId = 5,
                            value = "9"
                        ),
                        SubscoreEntity(
                            id = 6,
                            subscoreTitleId = 6,
                            value = "8"
                        ),
                        SubscoreEntity(
                            id = 7,
                            subscoreTitleId = 7,
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
                        SubscoreEntity(
                            id = 0,
                            subscoreTitleId = 0,
                            value = "5"
                        ),
                        SubscoreEntity(
                            id = 1,
                            subscoreTitleId = 1,
                            value = "6"
                        ),
                        SubscoreEntity(
                            id = 2,
                            subscoreTitleId = 2,
                            value = "7"
                        ),
                        SubscoreEntity(
                            id = 3,
                            subscoreTitleId = 3,
                            value = "8"
                        ),
                        SubscoreEntity(
                            id = 4,
                            subscoreTitleId = 4,
                            value = "9"
                        ),
                        SubscoreEntity(
                            id = 5,
                            subscoreTitleId = 5,
                            value = "10"
                        ),
                        SubscoreEntity(
                            id = 6,
                            subscoreTitleId = 6,
                            value = "11"
                        ),
                        SubscoreEntity(
                            id = 7,
                            subscoreTitleId = 7,
                            value = "12"
                        )
                    )
                ),
            )
        ),
    )
)
