package com.waynebloom.scorekeeper.ext

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.TextFieldValue
import com.waynebloom.scorekeeper.constants.Constants
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.shared.domain.model.TextFieldInput
import com.waynebloom.scorekeeper.ui.LocalCustomThemeColors
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

fun String.toShortScoreFormat(): String {
    val maximumRawNumberLength = 6
    val billionScale = -9
    val billionMark = "B"
    val millionScale = -6
    val millionMark = "M"
    val tenThousandScale = -3
    val thousandMark = "K"

    if (count { it != '.' } <= maximumRawNumberLength) return this
    val bigDecimal = toBigDecimal()
    return if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Trillion)) {
        "1000B+"
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Billion)) {
        bigDecimal
            .scaleByPowerOfTen(billionScale)
            .toTrimmedScoreString() + billionMark
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Million)) {
        bigDecimal
            .scaleByPowerOfTen(millionScale)
            .toTrimmedScoreString() + millionMark
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.TenThousand)) {
        bigDecimal
            .scaleByPowerOfTen(tenThousandScale)
            .toTrimmedScoreString() + thousandMark
    } else {
        bigDecimal
            .setScale(0, RoundingMode.HALF_UP)
            .toPlainString() + "*"
    }
}

fun String.toTextFieldValue() = TextFieldValue(this)

fun String.toTextFieldInput() = TextFieldInput(value = this.toTextFieldValue())

@Composable
fun String.toColor() = LocalCustomThemeColors.current.getColorByKey(this)
