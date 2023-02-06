package com.waynebloom.scorekeeper.screens

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.components.ScreenHeader
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.ScorekeeperScreen
import com.waynebloom.scorekeeper.ext.onFocusSelectAll
import com.waynebloom.scorekeeper.ui.theme.deepOrange500
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModel
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModelFactory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditPlayerScoreScreen(
    initialPlayer: PlayerObject,
    subscoreTitles: List<SubscoreTitleEntity>,
    themeColor: Color,
    onSaveTap: (EntityStateBundle<PlayerEntity>, List<SubscoreStateBundle>) -> Unit,
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
    val focusManager = LocalFocusManager.current
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
                            label = { Text(text = stringResource(id = R.string.field_name)) },
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            colors = textFieldColors,
                            maxLines = 1,
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
                            uncategorizedScoreBundle = viewModel.uncategorizedScoreBundle,
                            focusManager = focusManager,
                            onDoneTap = { viewModel.onSaveTap(keyboardController) },
                            onSubscoreTextFieldValueChange = { id, textFieldValue -> viewModel.updateSubscoreStateById(id, textFieldValue) },
                            onUncategorizedTextFieldValueChange = { viewModel.updateUncategorizedScoreRemainder(it) }
                        )
                    } else {
                        TotalScoreField(
                            totalScoreBundle = viewModel.totalScoreBundle,
                            textFieldColors = textFieldColors,
                            onChange = { viewModel.updateTotalScore(it) },
                            onDoneTap = { viewModel.onSaveTap(keyboardController) }
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
                    enabled = viewModel.scoreValuesAreValid,
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
    totalScoreBundle: SubscoreStateBundle,
    textFieldColors: TextFieldColors,
    onChange: (TextFieldValue) -> Unit,
    onDoneTap: () -> Unit
) {
    OutlinedTextFieldWithErrorDescription(
        textFieldValue = totalScoreBundle.textFieldValue,
        onValueChange = { onChange(it) },
        groupModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        label = { Text(text = stringResource(id = R.string.field_total_score)) },
        isError = !totalScoreBundle.scoreStringIsValidLong,
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneTap() }
        ),
        errorDescription = R.string.error_invalid_score,
        selectAllOnFocus = true
    )
}

@Composable
@SuppressLint("ModifierParameter")
fun OutlinedTextFieldWithErrorDescription(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    groupModifier: Modifier = Modifier,
    label: @Composable (() -> Unit)?,
    isError: Boolean,
    colors: TextFieldColors,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    maxLines: Int = 1,
    @StringRes errorDescription: Int,
    selectAllOnFocus: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = groupModifier
    ) {
        val textFieldModifier = if (selectAllOnFocus) {
            Modifier.onFocusSelectAll(
                textFieldValueState = textFieldValue,
                onTextFieldValueChanged = { onValueChange(it) }
            )
        } else {
            Modifier
        }

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            colors = colors,
            label = label,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            maxLines = maxLines,
            modifier = textFieldModifier
        )

        if (isError) {
            Text(
                text = stringResource(id = errorDescription),
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
private fun SubscoreFields(
    subscoreStateBundles: List<SubscoreStateBundle>,
    subscoreTitles: List<SubscoreTitleEntity>,
    textFieldColors: TextFieldColors,
    uncategorizedScoreBundle: SubscoreStateBundle,
    focusManager: FocusManager,
    onDoneTap: () -> Unit,
    onSubscoreTextFieldValueChange: (Long, TextFieldValue) -> Unit,
    onUncategorizedTextFieldValueChange: (TextFieldValue) -> Unit
) {
    subscoreStateBundles.forEachIndexed { index, subscore ->
        OutlinedTextFieldWithErrorDescription(
            textFieldValue = subscore.textFieldValue,
            onValueChange = { onSubscoreTextFieldValueChange(subscore.entity.subscoreTitleId, it) },
            groupModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text(text = subscoreTitles[index].title) },
            isError = !subscore.scoreStringIsValidLong,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescription = R.string.error_invalid_score,
            selectAllOnFocus = true
        )
    }

    OutlinedTextFieldWithErrorDescription(
        textFieldValue = uncategorizedScoreBundle.textFieldValue,
        groupModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        onValueChange = { onUncategorizedTextFieldValueChange(it) },
        label = { Text(text = stringResource(id = R.string.field_uncategorized)) },
        isError = !uncategorizedScoreBundle.scoreStringIsValidLong,
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneTap() }
        ),
        errorDescription = R.string.error_invalid_score,
        selectAllOnFocus = true
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