package com.waynebloom.scorekeeper.data.model.game

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.waynebloom.scorekeeper.data.model.match.MatchEntity
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.ScoringMode

data class GameObject(
    @Embedded
    var entity: GameEntity = GameEntity(),

    @Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchEntity::class)
    var matches: List<MatchObject> = listOf(),

    @Relation(parentColumn = "id", entityColumn = "game_id", entity = CategoryTitleEntity::class)
    var subscoreTitles: List<CategoryTitleEntity> = listOf()
) {
    @Ignore
    fun getScoringMode() = ScoringMode.getModeByOrdinal(entity.scoringMode)
}
