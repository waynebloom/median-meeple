package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.waynebloom.scorekeeper.room.domain.model.CategoryDomainModel

@Entity(
    tableName = "SubscoreTitle",
    foreignKeys = [ForeignKey(
        entity = GameDataModel::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("game_id"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["game_id"])]
)
data class CategoryDataModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "game_id")
    var gameId: Long = 0,

    @ColumnInfo(name = "title")
    override var name: String = "",

    override var position: Int = 0
): CategoryDomainModel