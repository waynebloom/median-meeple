package com.waynebloom.highscores

import android.os.Bundle
import android.service.autofill.UserData
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.highscores.model.Game
import com.waynebloom.highscores.model.GamesViewModel
import com.waynebloom.highscores.model.Score
import com.waynebloom.highscores.ui.theme.HighScoresTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HighScoresApp() }
    }
}

@Composable
fun HighScoresApp(
    gamesViewModel: GamesViewModel = viewModel()
) {
    HighScoresTheme {
        val navController = rememberNavController()
        Scaffold() { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HighScoresScreen.Overview.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(HighScoresScreen.Overview.name) {
                    MainScreen(
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
                    val game = UserData.getAccount(accountName)
                    SingleAccountBody(account)
                }
                composable(HighScoresScreen.NewGame.name) {

                }
                composable(HighScoresScreen.NewScore.name) {

                }
            }
        }
    }
}

private fun navigateToSingleGame(navController: NavController, gameName: String) {
    navController.navigate("${HighScoresScreen.GameScoresList}/$gameName")
}

private fun navigateToSingleScore() {

}

@Preview
@Composable
fun HighScoresAppPreview() {
    HighScoresTheme {
        HighScoresApp()
    }
}