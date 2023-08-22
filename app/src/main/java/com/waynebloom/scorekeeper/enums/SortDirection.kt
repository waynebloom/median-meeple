package com.waynebloom.scorekeeper.enums

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class SortDirection(@StringRes override val label: Int): MenuOption {
    Ascending(R.string.sort_direction_ascending),
    Descending(R.string.sort_direction_descending)
}
