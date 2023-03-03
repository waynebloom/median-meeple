package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import java.math.BigDecimal
import java.math.RoundingMode

fun String.getScoreValidityState(): ScoreStringValidityState {
    val scoreBigDecimal = toBigDecimalOrNull()

    return if (scoreBigDecimal == null) {
        ScoreStringValidityState.InvalidNumber
    } else if (scoreBigDecimal.scale() > 3) {
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
    if (count { it != '.' } <= 6) return this
    val bigDecimal = toBigDecimal()
    return if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Trillion)) {
        "1000B+"
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Billion)) {
        bigDecimal
            .scaleByPowerOfTen(-9)
            .toTrimmedScoreString() + "B"
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.Million)) {
        bigDecimal
            .scaleByPowerOfTen(-6)
            .toTrimmedScoreString() + "M"
    } else if (bigDecimal.greaterThanOrEqualTo(BigDecimalValues.TenThousand)) {
        bigDecimal
            .scaleByPowerOfTen(-3)
            .toTrimmedScoreString() + "K"
    } else {
        bigDecimal
            .setScale(0, RoundingMode.HALF_UP)
            .toPlainString() + "*"
    }
}
