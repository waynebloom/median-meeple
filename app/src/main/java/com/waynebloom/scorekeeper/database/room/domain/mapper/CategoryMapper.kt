package com.waynebloom.scorekeeper.database.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.database.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

class CategoryMapper @Inject constructor() {

	fun toData(json: JsonObject): CategoryDataModel {
		return Json.decodeFromJsonElement(json)
	}

	fun toData(category: CategoryDomainModel, gameID: Long) = category.let {
		CategoryDataModel(
			id = it.id.let { id -> if (id == -1L) 0 else id },
			gameID = gameID,
			name = it.name.text,
			position = it.position,
		)
	}

	fun toDomain(category: CategoryDataModel) = category.let {
		CategoryDomainModel(
			id = it.id,
			name = TextFieldValue(it.name),
			position = it.position,
		)
	}

	fun toDomain(categories: List<CategoryDataModel>) = categories.map { this.toDomain(it) }
}