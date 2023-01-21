package com.waynebloom.scorekeeper.data.model

import androidx.room.*

val EMPTY_SUBSCORE_ENTITY = SubscoreEntity()

@Entity(
    tableName = "Subscore",
    foreignKeys = [
        ForeignKey(
            entity = SubscoreTitleEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("subscore_title_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("player_id"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["player_id"]),
        Index(value = ["subscore_title_id"])
    ]
)
data class SubscoreEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "subscore_title_id")
    var subscoreTitleId: Long = 0,

    @ColumnInfo(name = "player_id")
    var playerId: Long = 0,

    // TODO make this non-nullable, default 0
    var value: Long? = null
)
