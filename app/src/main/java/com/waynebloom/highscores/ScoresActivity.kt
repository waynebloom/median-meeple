package com.waynebloom.highscores

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.waynebloom.highscores.data.EMPTY_SCORE
import com.waynebloom.highscores.data.Score
import com.waynebloom.highscores.data.EMPTY_GAME
import com.waynebloom.highscores.data.Game
import com.waynebloom.highscores.screens.*
import com.waynebloom.highscores.ui.theme.HighScoresTheme

class ScoresActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gamesViewModel = ViewModelProvider(this)[GamesViewModel::class.java]
        setContent { HighScoresApp(gamesViewModel = gamesViewModel) }
    }
}

@Composable
fun HighScoresApp(gamesViewModel: GamesViewModel) {
    HighScoresTheme {
        val navController = rememberNavController()
        Scaffold() { innerPadding ->
            ScoresNavHost(
                navController = navController,
                gamesViewModel = gamesViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun ScoresNavHost(
    navController: NavHostController,
    gamesViewModel: GamesViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = HighScoresScreen.Overview.name,
        modifier = modifier
    ) {

        // OverviewScreen
        composable(HighScoresScreen.Overview.name) {
            OverviewScreen(
                games = gamesViewModel
                    .games
                    .collectAsState(listOf())
                    .value
                    .take(6),
                scores = gamesViewModel
                    .scores
                    .collectAsState(listOf())
                    .value
                    .toList()
                    .sortedByDescending { it.timeModified }
                    .take(5),
                onSeeAllGamesTap = { navController.navigate(HighScoresScreen.Games.name) },
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
                onSingleScoreTap = { score -> navigateToSingleScore(navController, score.gameOwnerId, score.id) }
            )
        }

        // GamesScreen
        composable(HighScoresScreen.Games.name) {
            GamesScreen(
                games = gamesViewModel.games.collectAsState(listOf()).value,
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
            )
        }

        // GamesScreen w/ delete intent
        composable(
            route = "${HighScoresScreen.Games.name}/delete/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val gameId = entry.arguments?.getString("id") ?: ""
            gamesViewModel.deleteGameById(gameId)
            GamesScreen(
                games = gamesViewModel.games.collectAsState(listOf()).value,
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
            )
        }

        // SingleGameScreen
        composable(
            route = "${HighScoresScreen.SingleGame.name}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getString("id") ?: ""
            val game = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            SingleGameScreen(
                game = game,
                scores = gamesViewModel.getScoresByGameId(game.id).collectAsState(initial = listOf()).value,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewScoreTap = { gameId -> navigateToNewScore(navController, gameId) },
                onSingleScoreTap = { score -> navigateToSingleScore(navController, score.gameOwnerId, score.id) }
            )
        }

        // SingleGameScreen w/ delete intent
        composable(
            route = "${HighScoresScreen.SingleGame.name}/delete/{gameId}/{scoreId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                },
                navArgument("scoreId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getString("gameId") ?: ""
            val scoreId = entry.arguments?.getString("scoreId") ?: ""
            val game = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            gamesViewModel.deleteScoreById(scoreId)
            SingleGameScreen(
                game = game,
                scores = gamesViewModel.getScoresByGameId(game.id).collectAsState(initial = listOf()).value,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewScoreTap = { gameId -> navigateToNewScore(navController, gameId) },
                onSingleScoreTap = { score -> navigateToSingleScore(navController, score.gameOwnerId, score.id) }
            )
        }

        // EditGameScreen
        composable(
            route = "${HighScoresScreen.EditGame.name}/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getString("id") ?: ""
            if (targetGameId == "new") {
                EditGameScreen(
                    game = Game(),
                    onSaveTap = { newGame ->
                        val popSuccess = navController.popBackStack(
                            route = HighScoresScreen.Games.name,
                            inclusive = false
                        )
                        if (!popSuccess) {
                            navController.popBackStack(
                                route = HighScoresScreen.Overview.name,
                                inclusive = false
                            )
                        }
                        navigateToSingleGame(navController, newGame.id)
                        gamesViewModel.addGame(newGame)
                        Toast.makeText(context, R.string.toast_game_created, Toast.LENGTH_SHORT).show()
                    },
                    isNewGame = true
                )
            } else {
                EditGameScreen(
                    game = gamesViewModel
                        .getGameById(targetGameId)
                        .collectAsState(EMPTY_GAME)
                        .value ?: EMPTY_GAME,
                    onDeleteTap = { gameId ->
                        Toast.makeText(context, R.string.toast_game_deleted, Toast.LENGTH_SHORT).show()
                        navController.popBackStack(
                            route = HighScoresScreen.Overview.name,
                            inclusive = false
                        )
                        navigateToGamesWithDeletedGame(navController, gameId)
                    },
                    onSaveTap = { updatedGame ->
                        Toast.makeText(context, R.string.toast_game_updated, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        gamesViewModel.updateGame(updatedGame)
                    }
                )
            }
        }

        // SingleScoreScreen
        composable(
            route = "${HighScoresScreen.SingleScore.name}/{gameId}/{scoreId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                },
                navArgument("scoreId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetScoreId = entry.arguments?.getString("scoreId") ?: ""
            val targetGameId = entry.arguments?.getString("gameId") ?: ""
            val game = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            if (targetScoreId == "new") {
                SingleScoreScreen(
                    game = game,
                    score = Score(
                        gameOwnerId = targetGameId
                    ),
                    openInEditMode = true,
                    isNewScore = true,
                    onSaveTap = { newScore ->
                        Toast.makeText(context, R.string.toast_score_created, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        gamesViewModel.addScore(newScore)
                    }
                )
            } else {
                SingleScoreScreen(
                    game = game,
                    score = gamesViewModel
                        .getScoreById(targetScoreId)
                        .collectAsState(EMPTY_SCORE)
                        .value ?: EMPTY_SCORE,
                    onSaveTap = { score ->
                        Toast.makeText(context, R.string.toast_score_updated, Toast.LENGTH_SHORT).show()
                        gamesViewModel.updateScore(score)
                    },
                    onDeleteTap = { gameId, scoreId ->
                        val popSuccess = navController.popBackStack(
                            route = "${HighScoresScreen.SingleGame.name}/{id}",
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()

                        navigateToSingleGameWithDeletedScore(navController, gameId, scoreId)
                        Toast.makeText(context, R.string.toast_score_deleted, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

private fun backStackIncludesDestinationWithRoute(navController: NavController, route: String): Boolean {
    return navController.backQueue.find { it.destination.route == route } != null
}

private fun navigateToGamesWithDeletedGame(navController: NavController, gameId: String) {
    navController.navigate("${HighScoresScreen.Games.name}/delete/$gameId")
}

private fun navigateToSingleGame(navController: NavController, gameId: String) {
    navController.navigate("${HighScoresScreen.SingleGame.name}/$gameId")
}

private fun navigateToEditGame(navController: NavController, gameId: String) {
    navController.navigate("${HighScoresScreen.EditGame.name}/$gameId")
}

private fun navigateToNewGame(navController: NavController) {
    navController.navigate("${HighScoresScreen.EditGame.name}/new")
}

private fun navigateToSingleGameWithDeletedScore(navController: NavController, gameId: String, scoreId: String) {
    navController.navigate("${HighScoresScreen.SingleGame.name}/delete/$gameId/$scoreId")
}

private fun navigateToSingleScore(navController: NavController, gameId: String, scoreId: String) {
    navController.navigate("${HighScoresScreen.SingleScore.name}/$gameId/$scoreId")
}

private fun navigateToNewScore(navController: NavController, gameId: String) {
    navController.navigate("${HighScoresScreen.SingleScore.name}/$gameId/new")
}