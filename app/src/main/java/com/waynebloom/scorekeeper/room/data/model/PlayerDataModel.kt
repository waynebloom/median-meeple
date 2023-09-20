package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import java.math.BigDecimal

@Entity(
    tableName = "Player",
    foreignKeys = [ForeignKey(
        entity = MatchDataModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("match_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["match_id"])]
)
data class PlayerDataModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(defaultValue = "0")
    var id: Long = 0,

    @ColumnInfo(name = "match_id")
    var matchId: Long = 0,

    override var name: String = "",

    override var position: Int = 0,

    @ColumnInfo(name = "score")
    override var totalScore: String = "0",

    @ColumnInfo(name = "show_detailed_score")
    override var showDetailedScore: Boolean = false
): PlayerDomainModel

class PlayerDataRelationModel(
    @Embedded
    var entity: PlayerDataModel = PlayerDataModel(),

    @Relation(parentColumn = "id", entityColumn = "player_id", entity = CategoryScoreDataModel::class)
    var score: List<CategoryScoreDataModel> = listOf()
) {


    // TODO remove this, put in ui model
    @Ignore
    fun getUncategorizedScore(): BigDecimal {
        val subscoreSum = score.sumOf { it.value.toBigDecimal() }
        val totalScoreBigDecimal = entity.totalScore.toBigDecimal()
        return totalScoreBigDecimal - subscoreSum
    }
}