package com.waynebloom.scorekeeper.ext

import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import java.math.RoundingMode

fun String.getScoreValidityState(): ScoreStringValidityState {
    val scoreBigDecimal = toBigDecimalOrNull()

    return if (scoreBigDecimal == null) {
        ScoreStringValidityState.InvalidNumber
    } else if (scoreBigDecimal.scale() > Constants.maximumDecimalPlaces) {
        ScoreStringValidityState.ExcessiveDecimals
    } else ScoreStringValidityState.Valid
}

fun String.sentenceCase(): String {
    val result: StringBuilder = StringBuilder(this[0].uppercase())
    for (i in 1 until this.length) {
        if (this[i-1] == ' ') {
            result.append(this[i].uppercase())
        } else result.append(this[i].lowercase())
    }
    return result.toString()
}

fun String.convertToShortFormatScore(): String {
    val maximumAcceptablePrecisionForDisplay = 6
    val billionScale = -9
    val billionMark = "B"
    val millionScale = -6
    val millionMark = "M"
    val tenThousandScale = -3
    val thousandMark = "K"

    return with(toBigDecimal()) {
        when {

            // The precision is low enough to display without conversion
            precision() <= maximumAcceptablePrecisionForDisplay -> toPlainString()

            greaterThanOrEqualTo(BigDecimalValues.Trillion) -> "1000B+"

            greaterThanOrEqualTo(BigDecimalValues.Billion) -> this
                .scaleByPowerOfTen(billionScale)
                .toStringForDisplay() + billionMark

            greaterThanOrEqualTo(BigDecimalValues.Million) -> this
                .scaleByPowerOfTen(millionScale)
                .toStringForDisplay() + millionMark

            greaterThanOrEqualTo(BigDecimalValues.TenThousand) -> this
                .scaleByPowerOfTen(tenThousandScale)
                .toStringForDisplay() + thousandMark

            else -> setScale(0, RoundingMode.HALF_UP).toPlainString() + "*"
        }
    }
}

fun String.toTextFieldValue() = TextFieldValue(this)

fun String.toTextFieldInput() = TextFieldInput(value = this.toTextFieldValue())
