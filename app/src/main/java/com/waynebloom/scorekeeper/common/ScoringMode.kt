package com.waynebloom.scorekeeper.common

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.common.MenuOption

enum class ScoringMode(@StringRes override val label: Int) : MenuOption {
	Ascending(R.string.scoring_option_ascending),
	Descending(R.string.scoring_option_descending),
	Manual(R.string.scoring_option_manual);

	companion object {
		fun getModeByOrdinal(ordinal: Int) = entries[ordinal]
	}
}