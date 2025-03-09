package com.waynebloom.scorekeeper.database.room.domain.mapper

import androidx.compose.ui.text.input.TextFieldValue
import com.squareup.moshi.JsonAdapter
import com.waynebloom.scorekeeper.database.room.data.model.ScoreDataModel
import com.waynebloom.scorekeeper.database.room.domain.model.CategoryDomainModel
import com.waynebloom.scorekeeper.database.room.domain.model.ScoreDomainModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import javax.inject.Inject

class ScoreMapper @Inject constructor() {

	fun toData(json: JsonObject): ScoreDataModel {
		return Json.decodeFromJsonElement(json)
	}

	fun toData(score: ScoreDomainModel) = score.let {
		ScoreDataModel(
			id = it.id,
			playerID = it.playerID,
			categoryID = it.categoryID,
			value = it.scoreAsTextFieldValue.text
		)
	}

	fun toDomain(score: ScoreDataModel) = score.let {
		ScoreDomainModel(
			id = it.id,
			playerID = it.playerID,
			categoryID = it.categoryID,
			scoreAsBigDecimal = it.value.toBigDecimalOrNull(),
			scoreAsTextFieldValue = TextFieldValue(it.value)
		)
	}

	fun toDomain(scores: List<ScoreDataModel>) = scores.map { this.toDomain(it) }

	fun toDomainWithRelations(
		score: ScoreDataModel,
		categories: Map<Long, CategoryDomainModel>
	) = score.let {
		ScoreDomainModel(
			id = it.id,
			playerID = it.playerID,
			categoryID = it.categoryID,
			category = categories.getValue(it.categoryID),
			scoreAsBigDecimal = it.value.toBigDecimalOrNull(),
			scoreAsTextFieldValue = TextFieldValue(it.value)
		)
	}
}
