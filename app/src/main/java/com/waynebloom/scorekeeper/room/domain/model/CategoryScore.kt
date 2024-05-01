package com.waynebloom.scorekeeper.room.domain.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.enums.DatabaseAction
import com.waynebloom.scorekeeper.enums.ValidityState
import com.waynebloom.scorekeeper.ext.isValidBigDecimal
import com.waynebloom.scorekeeper.room.data.model.CategoryScoreDataModel
import java.math.BigDecimal
import java.math.RoundingMode

// TODO move this somewhere more relevant
class CategoryScoreEntityState(
    var entity: CategoryScoreDataModel,
    databaseAction: DatabaseAction = DatabaseAction.NO_ACTION
) {
    var databaseAction by mutableStateOf(databaseAction)
    var validityState by mutableStateOf(ValidityState.Valid)
    var textFieldValue by mutableStateOf(
        value = TextFieldValue(text = entity.value)
    )
    var bigDecimal by mutableStateOf(
        entity.value.toBigDecimal()
    )

    fun updateDatabaseAction(databaseAction: DatabaseAction) {
        if (this.databaseAction != DatabaseAction.NO_ACTION) return
        this.databaseAction = databaseAction
    }

    fun setScoreFromBigDecimal(scoreBigDecimal: BigDecimal) {
        validityState = ValidityState.Valid
        this.bigDecimal = beautifyBigDecimal(scoreBigDecimal)
        textFieldValue = textFieldValue.copy(text = this.bigDecimal.toPlainString())
    }

    fun setScoreFromTextValue(textFieldValue: TextFieldValue) {
        validityState = textFieldValue.text.isValidBigDecimal()
        this.textFieldValue = textFieldValue
        if (validityState == ValidityState.Valid)
            bigDecimal = beautifyBigDecimal(textFieldValue.text.toBigDecimal())
    }

    private fun beautifyBigDecimal(original: BigDecimal) = original
        .setScale(Constants.maximumDecimalPlaces, RoundingMode.HALF_UP)
        .stripTrailingZeros()
}
