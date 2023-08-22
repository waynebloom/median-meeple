package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.constants.Constants
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toTrimmedScoreString(): String =
    setScale(Constants.maximumDecimalPlaces, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()

fun BigDecimal.greaterThanOrEqualTo(target: BigDecimal): Boolean {
    val compareResult = compareTo(target)
    return compareResult == 1 || compareResult == 0
}

fun BigDecimal.isEqualTo(target: BigDecimal) = compareTo(target) == 0

object BigDecimalValues {
    val Trillion = BigDecimal(1000000000000)
    val Billion = BigDecimal(1000000000)
    val Million = BigDecimal(1000000)
    val TenThousand = BigDecimal(10000)
}
