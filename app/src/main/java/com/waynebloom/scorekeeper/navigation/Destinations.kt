package com.waynebloom.scorekeeper.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

sealed class Destination(val route: String) {

    data object Overview: Destination(route = "overview")
    data object Library: Destination(route = "library")
    data object SingleGame: Destination(route = "singleGame")
    data object EditGame: Destination("editGame")
    data object MatchesForGame: Destination(route = "matchesForGame")
    data object StatisticsForGame: Destination(route = "statisticsForGame")
    data object SingleMatch : Destination("singleMatch")
    data object EditPlayerScore : Destination("editPlayerScore")
    data object DetailPlayerScores : Destination("detailedPlayerScores")
}
