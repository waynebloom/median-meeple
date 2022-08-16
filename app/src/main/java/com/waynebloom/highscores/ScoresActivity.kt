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
import com.waynebloom.highscores.data.*
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
            val games by gamesViewModel.games.collectAsState(listOf())
            val allMatches by gamesViewModel.matches.collectAsState(listOf())
            val allScores by gamesViewModel.scores.collectAsState(listOf())
            OverviewScreen(
                games = games.take(6),
                matches = allMatches
                    .sortedByDescending { it.timeModified }
                    .take(5)
                    .onEach { match ->
                        match.scores = allScores.filter { score -> score.matchId == match.id }
                    },
                onSeeAllGamesTap = { navController.navigate(HighScoresScreen.Games.name) },
                onAddNewGameTap = { navigateToNewGame(navController) },
                onSingleGameTap = { game -> navigateToSingleGame(navController, game.id) },
                onSingleMatchTap = { match -> navigateToSingleMatch(navController, match.gameOwnerId, match.id) }
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
            val targetGameId = entry.arguments?.getString("id") ?: ""
            LaunchedEffect(targetGameId) { gamesViewModel.deleteGameById(targetGameId) }
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
            val targetGame = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            val targetGameMatchList = gamesViewModel
                .getMatchesByGameId(targetGame.id)
                .collectAsState(initial = listOf())
                .value
            targetGameMatchList.forEach { match ->
                match.scores = gamesViewModel
                    .getScoresByMatchId(match.id)
                    .collectAsState(initial = listOf())
                    .value
            }

            SingleGameScreen(
                game = targetGame,
                matches = targetGameMatchList,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewMatchTap = { gameId -> navigateToNewMatch(navController, gameId) },
                onSingleMatchTap = { match -> navigateToSingleMatch(navController, match.gameOwnerId, match.id) }
            )
        }

        // SingleGameScreen w/ delete intent
        composable(
            route = "${HighScoresScreen.SingleGame.name}/delete/{gameId}/{matchId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                },
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            gamesViewModel.deleteMatchById(entry.arguments?.getString("matchId") ?: "")

            val targetGameId = entry.arguments?.getString("gameId") ?: ""
            val targetGame = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            val targetGameMatchList = gamesViewModel
                .getMatchesByGameId(targetGame.id)
                .collectAsState(initial = listOf())
                .value
            targetGameMatchList.forEach { match ->
                match.scores = gamesViewModel
                    .getScoresByMatchId(match.id)
                    .collectAsState(initial = listOf())
                    .value
            }
            SingleGameScreen(
                game = targetGame,
                matches = targetGameMatchList,
                onEditGameTap = { navigateToEditGame(navController, targetGameId) },
                onNewMatchTap = { gameId -> navigateToNewMatch(navController, gameId) },
                onSingleMatchTap = { match -> navigateToSingleMatch(navController, match.gameOwnerId, match.id) }
            )
        }

        // EditGameScreen
        composable(
            route = "${HighScoresScreen.EditGame.name}/{gameId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetGameId = entry.arguments?.getString("gameId") ?: ""
            if (targetGameId == "new") {
                EditGameScreen(
                    game = GameEntity(),
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

        // SingleMatchScreen
        composable(
            route = "${HighScoresScreen.SingleMatch.name}/{gameId}/{matchId}",
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.StringType
                },
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val targetMatchId = entry.arguments?.getString("matchId") ?: ""
            val targetGameId = entry.arguments?.getString("gameId") ?: ""
            val game = gamesViewModel
                .getGameById(targetGameId)
                .collectAsState(EMPTY_GAME)
                .value ?: EMPTY_GAME
            if (targetMatchId == "new") {
                SingleMatchScreen(
                    game = game,
                    match = MatchEntity(
                        gameOwnerId = targetGameId
                    ),
                    openInEditMode = true,
                    isNewMatch = true,
                    onSaveTap = { newMatch, newScores ->
                        Toast.makeText(context, R.string.toast_score_created, Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        gamesViewModel.addMatch(newMatch)
                        newScores.forEach { score ->
                            gamesViewModel.addScore(score)
                        }
                    }
                )
            } else {
                val targetMatch = gamesViewModel
                    .getMatchById(targetMatchId)
                    .collectAsState(EMPTY_MATCH)
                    .value ?: EMPTY_MATCH
                targetMatch.scores = gamesViewModel
                    .getScoresByMatchId(targetMatchId)
                    .collectAsState(listOf())
                    .value
                SingleMatchScreen(
                    game = game,
                    match = targetMatch,
                    onSaveTap = { match, newScores ->
                        Toast.makeText(context, R.string.toast_score_updated, Toast.LENGTH_SHORT).show()
                        gamesViewModel.updateMatch(match)
                        newScores.forEach { score ->
                            if (targetMatch.scores.find { it.id == score.id } != null) {
                                gamesViewModel.updateScore(score)
                            } else {
                                gamesViewModel.addScore(score)
                            }
                        }
                    },
                    onDeleteTap = { gameId, matchId ->
                        val popSuccess = navController.popBackStack(
                            route = "${HighScoresScreen.SingleGame.name}/{id}",
                            inclusive = true
                        )
                        if (!popSuccess) navController.popBackStack()

                        navigateToSingleGameWithDeletedMatch(navController, gameId, matchId)
                        Toast.makeText(context, R.string.toast_score_deleted, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

/*private suspend fun deleteGame(gamesViewModel: GamesViewModel, gameId: String) {
    gamesViewModel
        .getMatchesByGameId(gameId)
        .onEach { matchList ->
            matchList.forEach { match ->
                gamesViewModel.deleteScoresByMatchId(match.id)
            }
        }
        .onCompletion {
            gamesViewModel.deleteMatchesByGameId(gameId)
            gamesViewModel.deleteGameById(gameId)
        }
        .collect()
}*/

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

private fun navigateToSingleGameWithDeletedMatch(navController: NavController, gameId: String, matchId: String) {
    navController.navigate("${HighScoresScreen.SingleGame.name}/delete/$gameId/$matchId")
}

private fun navigateToSingleMatch(navController: NavController, gameId: String, matchId: String) {
    navController.navigate("${HighScoresScreen.SingleMatch.name}/$gameId/$matchId")
}

private fun navigateToNewMatch(navController: NavController, gameId: String) {
    navController.navigate("${HighScoresScreen.SingleMatch.name}/$gameId/new")
}