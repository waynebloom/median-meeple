package com.waynebloom.scorekeeper.navigation

sealed class Destination(val route: String) {

	data object Hub : Destination(route = "hub")
	data object Settings : Destination(route = "settings")
	data object Login : Destination(route = "login")
	data object Library : Destination(route = "library")
	data object EditGame : Destination("editGame")
	data object MatchesForGame : Destination(route = "matchesForGame")
	data object StatisticsForGame : Destination(route = "statisticsForGame")
	data object ScoreCard : Destination("singleMatch")
}
