package com.waynebloom.scorekeeper.navigation

sealed class Destination(val route: String) {

    data object Library: Destination(route = "library")
    data object EditGame: Destination("editGame")
    data object MatchesForGame: Destination(route = "matchesForGame")
    data object StatisticsForGame: Destination(route = "statisticsForGame")
    data object ScoreCard : Destination("singleMatch")
    data object NewSingleMatch : Destination("newSingleMatch")
    data object EditPlayer : Destination("editPlayerScore")
    data object DetailPlayerScores : Destination("detailedPlayerScores")
}
