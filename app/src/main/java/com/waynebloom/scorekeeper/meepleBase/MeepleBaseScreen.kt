package com.waynebloom.scorekeeper.meepleBase

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.components.Loading
import com.waynebloom.scorekeeper.components.NewGameCard
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun MeepleBaseScreen(
    uiState: MeepleBaseUiState,
    modifier: Modifier = Modifier,
    onEmailChange: (TextFieldValue) -> Unit,
    onPwChange: (TextFieldValue) -> Unit,
    onLoginClick: () -> Unit,
    onRequestGames: () -> Unit,
) {

    when(uiState) {
        is MeepleBaseUiState.Loading -> {
            Loading()
        }
        is MeepleBaseUiState.Content -> {
            MeepleBaseScreen(
                email = uiState.email,
                pw = uiState.pw,
                gameCards = uiState.gameCards,
                modifier = modifier,
                onEmailChange,
                onPwChange,
                onLoginClick,
                onRequestGames,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MeepleBaseScreen(
    email: TextFieldValue,
    pw: TextFieldValue,
    gameCards: List<LibraryGameCard>,
    modifier: Modifier = Modifier,
    onEmailChange: (TextFieldValue) -> Unit,
    onPwChange: (TextFieldValue) -> Unit,
    onLoginClick: () -> Unit,
    onRequestGames: () -> Unit,
) {

    Scaffold(
        topBar = {
            // TODO: top bar
        },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = modifier,
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(160.dp),
                verticalItemSpacing = Dimensions.Spacing.sectionContent,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent),
                contentPadding = PaddingValues(Dimensions.Spacing.screenEdge),
            ) {
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.sectionContent)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = pw,
                            onValueChange = onPwChange,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Login")
                        }
                        Button(
                            onClick = onRequestGames,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Request Games")
                        }
                    }
                }

                gameCards.forEach { card ->
                    item(key = card.id) {
                        NewGameCard(
                            name = card.name,
                            color = card.color
                                .copy(alpha = 0.2f)
                                .compositeOver(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)),
                            highScore = card.highScore,
                            noOfMatches = card.noOfMatches,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .clickable {
                                    // TODO: onClick for game cards
                                }
                                .animateItemPlacement()
                        )
                    }
                }

                item {
                    Spacer(
                        Modifier
                            .windowInsetsBottomHeight(WindowInsets.navigationBars)
                            .consumeWindowInsets(WindowInsets.navigationBars)
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun Normal() {
    MedianMeepleTheme {
        // TODO: previews
    }
}
