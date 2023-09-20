package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.waynebloom.scorekeeper.enums.ScoringMode
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel

@Entity(tableName = "Game")
data class GameDataModel(

    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0,

    override var color: String = "ORANGE",

    override var name: String = "",

    @ColumnInfo(name = "scoring_mode", defaultValue = "1")
    override var scoringMode: Int = ScoringMode.Descending.ordinal
): GameDomainModel

data class GameDataRelationModel(
    @Embedded
    var entity: GameDataModel = GameDataModel(),

    @Relation(parentColumn = "id", entityColumn = "game_owner_id", entity = MatchDataModel::class)
    var matches: List<MatchDataRelationModel> = listOf(),

    @Relation(parentColumn = "id", entityColumn = "game_id", entity = CategoryDataModel::class)
    var categories: List<CategoryDataModel> = listOf()
) {

    // TODO remove this
    @Ignore
    fun getScoringMode() = ScoringMode.getModeByOrdinal(entity.scoringMode)
}