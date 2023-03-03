package com.waynebloom.scorekeeper.enums

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class ScoreStringValidityState(@StringRes val descriptionResource: Int) {
    InvalidNumber(R.string.validity_state_invalid_number),
    ExcessiveDecimals(R.string.validity_state_excessive_decimals),
    Valid(R.string.validity_state_no_error)
}