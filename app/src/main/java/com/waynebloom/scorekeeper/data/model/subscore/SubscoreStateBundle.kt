package com.waynebloom.scorekeeper.data.model.subscore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.DatabaseAction

class SubscoreStateBundle(
    var entity: SubscoreEntity,
    databaseAction: DatabaseAction = DatabaseAction.NO_ACTION
) {
    var databaseAction by mutableStateOf(databaseAction)
    var scoreStringIsValidLong by mutableStateOf(true)
    var textFieldValue by mutableStateOf(
        value = TextFieldValue(text = entity.value.toString()))

    fun copy(
        entity: SubscoreEntity = this.entity,
        databaseAction: DatabaseAction = this.databaseAction
    ) = SubscoreStateBundle(entity, databaseAction)

    fun updateDatabaseAction(databaseAction: DatabaseAction) {
        if (this.databaseAction != DatabaseAction.NO_ACTION) return
        this.databaseAction = databaseAction
    }

    fun setScoreFromLong(scoreLong: Long) {
        textFieldValue = textFieldValue.copy(text = scoreLong.toString())
        entity.value = scoreLong
    }

    fun setScoreFromTextValue(textFieldValue: TextFieldValue) {
        this.textFieldValue = textFieldValue
        val scoreLong = textFieldValue.text.toLongOrNull()
        if (scoreLong == null) {
            scoreStringIsValidLong = false
        } else {
            this.entity.value = scoreLong
            scoreStringIsValidLong = true
        }
    }
}