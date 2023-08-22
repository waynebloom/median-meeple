package com.waynebloom.scorekeeper.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

enum class SingleGameScreen(
    @StringRes val titleResource: Int,
    @DrawableRes val iconResource: Int,
    ) {
    MatchesForSingleGame(
        titleResource = R.string.text_matches,
        iconResource = R.drawable.ic_list,
    ),
    GameStatistics(
        titleResource = R.string.text_statistics,
        iconResource = R.drawable.ic_bar_chart,
    );
}
