package com.waynebloom.scorekeeper.enums

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class ScoringMode(
    @StringRes val label: Int
) {
    Ascending(R.string.scoring_option_ascending),
    Descending(R.string.scoring_option_descending);

    companion object {
        fun getModeByOrdinal(ordinal: Int) = values()[ordinal]
    }
}