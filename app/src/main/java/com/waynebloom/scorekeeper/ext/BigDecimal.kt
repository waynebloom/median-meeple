package com.waynebloom.scorekeeper.ext

import com.waynebloom.scorekeeper.constants.Constants
import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.toShortFormatString(): String {
    val maximumAcceptablePrecisionForDisplay = 6
    val billionScale = -9
    val billionMark = "B"
    val millionScale = -6
    val millionMark = "M"
    val tenThousandScale = -3
    val thousandMark = "K"

    return when {

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

        else -> "~" + setScale(0, RoundingMode.HALF_UP).toPlainString()
    }
}

fun BigDecimal.toStringForDisplay(): String =
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
