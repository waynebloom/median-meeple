package com.waynebloom.scorekeeper.enums

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class MatchSortMode(@StringRes override val label: Int) : MenuOption {
	ByMatchAge(R.string.sort_option_age),
	ByWinningPlayer(R.string.sort_option_name),
	ByWinningScore(R.string.sort_option_score),
	ByPlayerCount(R.string.sort_option_players)
}
