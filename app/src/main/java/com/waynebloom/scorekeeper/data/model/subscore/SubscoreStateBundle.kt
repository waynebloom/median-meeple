package com.waynebloom.scorekeeper.data.model.subscore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.ext.getScoreValidityState
import java.math.BigDecimal
import java.math.RoundingMode

class SubscoreStateBundle(
    var entity: CategoryScoreEntity,
    databaseAction: DatabaseAction = DatabaseAction.NO_ACTION
) {
    var databaseAction by mutableStateOf(databaseAction)
    var validityState by mutableStateOf(ScoreStringValidityState.Valid)
    var textFieldValue by mutableStateOf(
        value = TextFieldValue(text = entity.value))
    var bigDecimal by mutableStateOf(
        entity.value.toBigDecimal())

    fun updateDatabaseAction(databaseAction: DatabaseAction) {
        if (this.databaseAction != DatabaseAction.NO_ACTION) return
        this.databaseAction = databaseAction
    }

    fun setScoreFromBigDecimal(scoreBigDecimal: BigDecimal) {
        validityState = ScoreStringValidityState.Valid
        this.bigDecimal = beautifyBigDecimal(scoreBigDecimal)
        textFieldValue = textFieldValue.copy(text = this.bigDecimal.toPlainString())
    }

    fun setScoreFromTextValue(textFieldValue: TextFieldValue) {
        validityState = textFieldValue.text.getScoreValidityState()
        this.textFieldValue = textFieldValue
        if (validityState == ScoreStringValidityState.Valid)
            bigDecimal = beautifyBigDecimal(textFieldValue.text.toBigDecimal())
    }

    private fun beautifyBigDecimal(original: BigDecimal) = original
        .setScale(3, RoundingMode.HALF_UP)
        .stripTrailingZeros()
}