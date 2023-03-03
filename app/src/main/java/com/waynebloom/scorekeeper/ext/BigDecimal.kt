package com.waynebloom.scorekeeper.ext

import java.math.BigDecimal
import java.math.RoundingMode

object BigDecimalValues {
    val Trillion = BigDecimal(1000000000000)
    val Billion = BigDecimal(1000000000)
    val Million = BigDecimal(1000000)
    val TenThousand = BigDecimal(10000)
}

fun BigDecimal.toTrimmedScoreString(): String = setScale(3, RoundingMode.HALF_UP)
    .stripTrailingZeros()
    .toPlainString()

fun BigDecimal.greaterThanOrEqualTo(target: BigDecimal): Boolean {
    val compareResult = compareTo(target)
    return compareResult == 1 || compareResult == 0
}

fun BigDecimal.isEqualTo(target: BigDecimal) = compareTo(target) == 0