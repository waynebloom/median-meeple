package com.waynebloom.highscores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.GamesViewModel
import com.waynebloom.highscores.screens.EditScoreScreen
import com.waynebloom.highscores.screens.HighScoresScreen
import com.waynebloom.highscores.screens.OverviewScreen
import com.waynebloom.highscores.screens.SingleGameScreen
import com.waynebloom.highscores.ui.theme.HighScoresTheme

class ScoresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HighScoresApp() }
    }
}

@Composable
fun HighScoresApp(
) {
    HighScoresTheme {
        val navController = rememberNavController()
        Scaffold() { innerPadding ->
            ScoresNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ScoresNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    gamesViewModel: GamesViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = HighScoresScreen.Overview.name,
        modifier = modifier
    ) {
        composable(HighScoresScreen.Overview.name) {
            OverviewScreen(
                games = gamesViewModel.games,
                onSeeAllGamesTap = { navController.navigate(HighScoresScreen.Games.name) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.name) },
                onSingleScoreTap = {}
            )
        }
        composable(HighScoresScreen.Games.name) {

        }
        composable(
            route = "${HighScoresScreen.Games.name}/{name}",
            arguments = listOf(
                navArgument("name") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val gameName = entry.arguments?.getString("name")
            val game = gamesViewModel.getGame(gameName)
            SingleGameScreen(
                game = game,
                onAddScoreTap = {}
            )
        }
        composable(
            route = "${HighScoresScreen.EditGame.name}/{gameName}/{scoreId}",
            arguments = listOf(
                navArgument("gameName") {
                    type = NavType.StringType
                },
                navArgument("scoreId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val gameName = entry.arguments?.getString("gameName")
            val scoreId = entry.arguments?.getString("scoreId")
            EditScoreScreen(
                score = gamesViewModel.getScore(gameName, scoreId),
                onSubmitTap = {}
            )
        }
        composable(HighScoresScreen.EditScore.name) {

        }
    }
}

private fun navigateToSingleGame(navController: NavController, gameName: String) {
    navController.navigate("${HighScoresScreen.Games.name}/$gameName")
}

private fun navigateToSingleScore(navController: NavController, gameName: String, scoreId: String) {
    navController.navigate("${HighScoresScreen.EditScore.name}/$gameName/$scoreId")
}

@Preview
@Composable
fun HighScoresAppPreview() {
    HighScoresTheme {
        HighScoresApp()
    }
}