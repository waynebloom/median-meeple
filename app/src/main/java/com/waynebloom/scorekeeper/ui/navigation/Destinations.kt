package com.waynebloom.scorekeeper.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.waynebloom.scorekeeper.R

object Destinations {
    const val EditGame = "editGame"
    const val EditPlayerScore = "editPlayerScore"
    const val DetailPlayerScores = "detailedPlayerScores"
    const val Library = "library"
    const val Overview = "overview"
    const val SingleGame = "singleGame"
    const val SingleMatch = "singleMatch"
}

sealed class Destination(val route: String) {

    // TODO: adopt this as the standard for all destinations

    open class BottomNavDestination(
        route: String,
        @StringRes val labelResource: Int,
        @DrawableRes val unselectedIconResource: Int,
        @DrawableRes val selectedIconResource: Int
    ): Destination(route)

    data object Overview : BottomNavDestination(
        route = "overview",
        labelResource = R.string.top_bar_header_overview,
        selectedIconResource = R.drawable.ic_dashboard_filled,
        unselectedIconResource = R.drawable.ic_dashboard
    )

    data object Library : BottomNavDestination(
        route = "library",
        labelResource = R.string.text_library,
        selectedIconResource = R.drawable.ic_games_filled,
        unselectedIconResource = R.drawable.ic_games
    )

    data object SingleGame : Destination("singleGame")

    // region SingleGame children

    data object EditGame : Destination("editGame")

    data object MatchesForGame: BottomNavDestination(
        route = "matchesForGame",
        labelResource = R.string.text_matches,
        selectedIconResource = R.drawable.ic_table_filled,
        unselectedIconResource = R.drawable.ic_table
    )
    data object StatisticsForGame: BottomNavDestination(
        route = "statisticsForGame",
        labelResource = R.string.text_statistics,
        selectedIconResource = R.drawable.ic_leaderboard_filled,
        unselectedIconResource = R.drawable.ic_leaderboard
    )

    // endregion

    data object SingleMatch : Destination("singleMatch")

    data object EditPlayerScore : Destination("editPlayerScore")

    data object DetailPlayerScores : Destination("detailedPlayerScores")
}