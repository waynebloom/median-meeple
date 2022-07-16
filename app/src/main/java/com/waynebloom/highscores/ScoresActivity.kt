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
import com.waynebloom.highscores.model.GamesViewModel
import com.waynebloom.highscores.model.Score
import com.waynebloom.highscores.screens.SingleScoreScreen
import com.waynebloom.highscores.screens.HighScoresScreen
import com.waynebloom.highscores.screens.OverviewScreen
import com.waynebloom.highscores.screens.SingleGameScreen
import com.waynebloom.highscores.ui.theme.HighScoresTheme
import java.util.*

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
                onSingleScoreTap = {  }
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
            val targetGameName = entry.arguments?.getString("name")
            val game = gamesViewModel.getGame(targetGameName)
            SingleGameScreen(
                game = game,
                onNewScoreTap = { gameName -> navigateToNewScore(navController, gameName) },
                onSingleScoreTap = { score -> navigateToSingleScore(navController, score.forGame, score.id) }
            )
        }
        composable(
            route = "${HighScoresScreen.EditScore.name}/{gameName}/{scoreId}",
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
            val game = gamesViewModel.getGame(gameName)
            val scoreId = entry.arguments?.getString("scoreId")
            if (scoreId == "new") {
                SingleScoreScreen(
                    game = game,
                    score = Score(
                        forGame = game.name
                    ),
                    openInEditMode = true,
                    onSaveTap = { score -> gamesViewModel.addScore(game, score) }
                )
            } else {
                SingleScoreScreen(
                    game = game,
                    score = gamesViewModel.getScore(gameName, UUID.fromString(scoreId)),
                    onSaveTap = { score -> gamesViewModel.updateScore(game, score) }
                )
            }
        }
    }
}

private fun navigateToSingleGame(navController: NavController, gameName: String) {
    navController.navigate("${HighScoresScreen.Games.name}/$gameName")
}

private fun navigateToSingleScore(navController: NavController, gameName: String, scoreId: UUID) {
    navController.navigate("${HighScoresScreen.EditScore.name}/$gameName/$scoreId")
}

private fun navigateToNewScore(navController: NavController, gameName: String) {
    navController.navigate("${HighScoresScreen.EditScore.name}/$gameName/new")
}

@Preview
@Composable
fun HighScoresAppPreview() {
    HighScoresTheme {
        HighScoresApp()
    }
}