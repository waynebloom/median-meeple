package com.waynebloom.scorekeeper.database.supabase.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ChangeNetworkModel(
	val timestamp: String,
	val action: String,

	@SerialName("table_name")
	val tableName: String,

	@SerialName("old_data")
	val oldData: JsonObject?,

	@SerialName("new_data")
	val newData: JsonObject?,
)