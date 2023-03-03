package com.waynebloom.scorekeeper

import com.waynebloom.scorekeeper.data.model.game.GameEntity
import com.waynebloom.scorekeeper.data.model.game.GameObject
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreEntity
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity

val PreviewGameEntities: List<GameEntity> = listOf(
    GameEntity(name = "Carcassonne"),
    GameEntity(name = "Wingspan"),
    GameEntity(name = "Century: Golem Edition"),
    GameEntity(name = "Ticket to Ride: Rails & Sails Test Test Test"),
    GameEntity(name = "Mystic Vale"),
    GameEntity(name = "Mariposas"),
    GameEntity(name = "Azul: Queen's Garden"),
    GameEntity(name = "Catan"),
)

val PreviewGameObjects: List<GameObject> = PreviewGameEntities.map { GameObject(entity = it) }

val PreviewPlayerEntities: List<PlayerEntity> = listOf(
    PlayerEntity(
        name = "Wayne",
        score = "32"
    ),
    PlayerEntity(
        name = "Conor",
        score = "37"
    ),
    PlayerEntity(
        name = "Alyssa",
        score = "45"
    ),
    PlayerEntity(
        name = "",
        score = "22"
    )
)

val PreviewPlayerObjects: List<PlayerObject> = PreviewPlayerEntities.map {
    PlayerObject(
        entity = it,
        score = listOf(SubscoreEntity(value = it.score))
    )
}

val PreviewMatchEntities: List<MatchEntity> = listOf(
    MatchEntity(
        matchNotes = "Example notes 1"
    ),
    MatchEntity(
        matchNotes = "Example notes 2"
    ),
    MatchEntity(
        matchNotes = "Example notes 3"
    ),
)

val PreviewMatchObjects = PreviewMatchEntities.map { MatchObject(entity = it) }

val PreviewSubscoreTitleEntities: List<SubscoreTitleEntity> = listOf(
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

val PreviewSubscoreEntities: List<SubscoreEntity> = listOf(
    SubscoreEntity(
        subscoreTitleId = 1,
        value = "1"
    ),
    SubscoreEntity(
        subscoreTitleId = 2,
        value = "1"
    ),
    SubscoreEntity(
        subscoreTitleId = 3,
        value = "2"
    ),
    SubscoreEntity(
        subscoreTitleId = 4,
        value = "4"
    ),
    SubscoreEntity(
        subscoreTitleId = 5,
        value = "2"
    ),
    SubscoreEntity(
        subscoreTitleId = 6,
        value = "1"
    ),
    SubscoreEntity(
        subscoreTitleId = 7,
        value = "5"
    ),
    SubscoreEntity(
        subscoreTitleId = 8,
        value = "3"
    )
)
