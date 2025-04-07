package com.waynebloom.scorekeeper.feature.singleGame.matchesForGame

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.common.MenuOption

enum class SortDirection(@StringRes override val label: Int) : MenuOption {
	Ascending(R.string.sort_direction_ascending),
	Descending(R.string.sort_direction_descending)
}