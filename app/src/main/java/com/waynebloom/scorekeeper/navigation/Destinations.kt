package com.waynebloom.scorekeeper.navigation

sealed class Destination(val route: String) {

    data object Library: Destination(route = "library")
    data object EditGame: Destination("editGame")
    data object MatchesForGame: Destination(route = "matchesForGame")
    data object StatisticsForGame: Destination(route = "statisticsForGame")
    data object ScoreCard : Destination("singleMatch")
}
