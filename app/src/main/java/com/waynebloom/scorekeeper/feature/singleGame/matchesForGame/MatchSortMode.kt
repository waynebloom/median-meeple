package com.waynebloom.scorekeeper.feature.singleGame.matchesForGame

import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.common.MenuOption

enum class MatchSortMode(@StringRes override val label: Int) : MenuOption {
	ByMatchAge(R.string.sort_option_age),
	ByWinningPlayer(R.string.sort_option_name),
	ByWinningScore(R.string.sort_option_score),
	ByPlayerCount(R.string.sort_option_players)
}