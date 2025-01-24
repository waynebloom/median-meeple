package com.waynebloom.scorekeeper.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Expansion::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("expansion_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MatchDataModel::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("match_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["expansion_id"]),
        Index(value = ["match_id"]),
    ]
)
data class ExpansionMatch(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "expansion_id")
    val expansionId: Long,

    @ColumnInfo(name = "match_id")
    val matchId: Long,
)
