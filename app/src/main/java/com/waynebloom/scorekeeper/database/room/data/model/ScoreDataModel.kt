package com.waynebloom.scorekeeper.database.room.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(
	tableName = "CategoryScore",
	foreignKeys = [
		ForeignKey(
			entity = CategoryDataModel::class,
			parentColumns = arrayOf("id"),
			childColumns = arrayOf("subscore_title_id"),
			onDelete = ForeignKey.CASCADE,
			onUpdate = ForeignKey.CASCADE
		),
		ForeignKey(
			entity = PlayerDataModel::class,
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
data class ScoreDataModel(

	@PrimaryKey(autoGenerate = true)
	val id: Long = 0,

	@SerialName("category_id")
	@ColumnInfo(name = "subscore_title_id")
	var categoryID: Long = 0,

	@SerialName("player_id")
	@ColumnInfo(name = "player_id")
	var playerID: Long = 0,

	var value: String = "0"
)
