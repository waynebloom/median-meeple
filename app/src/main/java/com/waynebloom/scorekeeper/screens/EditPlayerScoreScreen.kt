package com.waynebloom.scorekeeper.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.components.ScreenHeader
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.ui.theme.deepOrange500
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModel
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModelFactory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditPlayerScoreScreen(
    initialPlayer: PlayerObject,
    subscoreTitles: List<SubscoreTitleEntity>,
    themeColor: Color,
    onSaveTap: (EntityStateBundle<PlayerEntity>, List<EntityStateBundle<SubscoreEntity>>) -> Unit,
    onDeleteTap: (Long) -> Unit
) {
    val viewModel = viewModel<EditPlayerScoreViewModel>(
        key = ScorekeeperScreen.EditPlayerScore.name,
        factory = EditPlayerScoreViewModelFactory(
            playerObject = initialPlayer,
            playerSubscores = initialPlayer.score,
            subscoreTitles = subscoreTitles,
            saveCallback = onSaveTap
        )
    ).also { it.initialPlayerEntity.id = initialPlayer.entity.id }
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = themeColor,
        focusedLabelColor = themeColor,
        cursorColor = themeColor,
        disabledBorderColor = themeColor.copy(0.75f)
    )

    Column {

        ScreenHeader(
            title = viewModel.nameState,
            color = themeColor
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            CompositionLocalProvider(
                LocalTextSelectionColors.provides(
                    TextSelectionColors(
                        handleColor = themeColor,
                        backgroundColor = themeColor.copy(0.3f)
                    )
                )
            ) {
                HeadedSection(title = R.string.header_player_info) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = viewModel.nameState,
                            onValueChange = { viewModel.setName(it) },
                            colors = textFieldColors,
                            label = { Text(text = stringResource(id = R.string.field_name)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                HeadedSection(title = R.string.header_player_scores) {
                    DetailedScoreSwitchRow(
                        checked = viewModel.showDetailedScoreState,
                        onCheckedChange = { viewModel.setShowDetailedScore(it) },
                        themeColor = themeColor
                    )

                    if (viewModel.showDetailedScoreState) {
                        SubscoreFields(
                            subscoreStateBundles = viewModel.subscoreStateBundles,
                            subscoreTitles = viewModel.subscoreTitles,
                            textFieldColors = textFieldColors,
                            uncategorizedScore = viewModel.uncategorizedScoreRemainderState,
                            onSubscoreChange = { id, value -> viewModel.updateSubscore(id, value) },
                            onUncategorizedScoreChange = { viewModel.updateUncategorizedScoreRemainder(it) }
                        )
                    } else {
                        TotalScoreField(
                            score = viewModel.totalScoreState,
                            textFieldColors = textFieldColors,
                            onChange = { viewModel.updateTotalScore(it) }
                        )
                    }
                }
            }

            Row(modifier = Modifier.padding(top = 16.dp)) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = themeColor,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    onClick = { viewModel.onSaveTap(keyboardController) },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f),
                    content = {
                        Icon(imageVector = Icons.Rounded.Done, contentDescription = null)
                    }
                )

                Button(
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colors.error),
                    onClick = { onDeleteTap(initialPlayer.entity.id) },
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .height(48.dp)
                        .weight(1f),
                    content = {
                        Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TotalScoreField(
    score: Long?,
    textFieldColors: TextFieldColors,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = score?.toString() ?: "",
        onValueChange = { onChange(it) },
        colors = textFieldColors,
        label = { Text(text = stringResource(id = R.string.field_total_score)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun SubscoreFields(
    subscoreStateBundles: List<EntityStateBundle<SubscoreEntity>>,
    subscoreTitles: List<SubscoreTitleEntity>,
    textFieldColors: TextFieldColors,
    uncategorizedScore: Long,
    onSubscoreChange: (Long, String) -> Unit,
    onUncategorizedScoreChange: (String) -> Unit
) {
    subscoreStateBundles
        .map { it.entity }
        .forEachIndexed { index, subscore ->
            OutlinedTextField(
                value = subscore.value?.toString() ?: "",
                onValueChange = { onSubscoreChange(subscore.subscoreTitleId, it) },
                colors = textFieldColors,
                label = { Text(text = subscoreTitles[index].title) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
    
    OutlinedTextField(
        value = uncategorizedScore.toString(),
        onValueChange = { onUncategorizedScoreChange(it) },
        colors = textFieldColors,
        label = { Text(text = stringResource(id = R.string.field_uncategorized)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@Composable
private fun DetailedScoreSwitchRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    themeColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(text = stringResource(id = R.string.field_detailed_view))

        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = themeColor,
                checkedTrackColor = themeColor
            )
        )
    }
}

@Preview(backgroundColor = 0xFFF0EAE2, showBackground = true)
@Composable
fun EditPlayerScoreScreenPreview() {
    ScoreKeeperTheme {
        EditPlayerScoreScreen(
            initialPlayer = PlayerObject(
                PlayerEntity(
                    name = "Wayne",
                    showDetailedScore = true
                )
            ),
            subscoreTitles = listOf(
                SubscoreTitleEntity(title = "Eggs"),
                SubscoreTitleEntity(title = "Tucked Cards"),
                SubscoreTitleEntity(title = "Cached Food"),
                SubscoreTitleEntity(title = "Birds")
            ),
            themeColor = deepOrange500,
            onSaveTap = { _, _ -> },
            onDeleteTap = {}
        )
    }
}