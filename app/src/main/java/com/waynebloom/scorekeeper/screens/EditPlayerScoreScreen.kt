package com.waynebloom.scorekeeper.screens

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.ui.theme.ScoreKeeperTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.HeadedSection
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.SubscoreTitleEntity
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
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
    onDeleteTap: (Long) -> Unit,
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

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {

        EditPlayerScoreScreenTopBar(
            title = initialPlayer.entity.name,
            themeColor = themeColor,
            submitButtonEnabled = viewModel.isSubmitButtonEnabled(),
            onDoneTap = { viewModel.onSaveTap(keyboardController) },
            onDeleteTap = { onDeleteTap(initialPlayer.entity.id) },
        )

        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            EditPlayerScoreScreenInfoBox(
                showDetailedModeWarning = viewModel.shouldShowDetailedModeWarning()
            )

            CompositionLocalProvider(
                LocalTextSelectionColors.provides(
                    TextSelectionColors(
                        handleColor = themeColor,
                        backgroundColor = themeColor.copy(0.3f)
                    )
                )
            ) {
                HeadedSection(
                    title = R.string.header_player_info,
                    topPadding = 32
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextFieldWithErrorDescription(
                            textFieldValue = viewModel.nameTextFieldValue,
                            onValueChange = { viewModel.setName(it) },
                            label = { Text(text = stringResource(id = R.string.field_name)) },
                            isError = !viewModel.nameIsValid,
                            colors = textFieldColors,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Next) }
                            ),
                            errorDescription = R.string.error_empty_name,
                            selectAllOnFocus = true
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
                            onSubscoreTextFieldValueChange = { id, textFieldValue -> viewModel.onSubscoreFieldUpdate(id, textFieldValue) },
                            onUncategorizedTextFieldValueChange = { viewModel.onUncategorizedFieldUpdate(it) }
                        )
                    } else {
                        TotalScoreField(
                            totalScoreBundle = viewModel.totalScoreBundle,
                            textFieldColors = textFieldColors,
                            onChange = { viewModel.onTotalScoreFieldUpdate(it) },
                            onDoneTap = { viewModel.onSaveTap(keyboardController) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditPlayerScoreScreenTopBar(
    title: String,
    themeColor: Color,
    submitButtonEnabled: Boolean,
    onDoneTap: () -> Unit,
    onDeleteTap: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.h5,
            color = themeColor
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.background(MaterialTheme.colors.surface, MaterialTheme.shapes.small)
        ) {

            CustomIconButton(
                onTap = onDoneTap,
                enabled = submitButtonEnabled,
                foregroundColor = themeColor,
                imageVector = Icons.Rounded.Done
            )

            CustomIconButton(
                onTap = onDeleteTap,
                foregroundColor = MaterialTheme.colors.error,
                imageVector = Icons.Rounded.Delete
            )
        }
    }
}

@Composable
private fun EditPlayerScoreScreenInfoBox(
    showDetailedModeWarning: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(vertical = 16.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = MaterialTheme.shapes.small
            )
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 2.dp),
                )

                Text(
                    text = stringResource(id = R.string.info_player_score_screen_helper),
                    modifier = Modifier.weight(1f, fill = false),
                )
            }

            if (showDetailedModeWarning) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        tint = MaterialTheme.colors.error,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 2.dp),
                    )

                    Text(
                        text = stringResource(id = R.string.info_player_score_screen_warning),
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
            }
        }
    }
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
    singleLine: Boolean = true,
    @StringRes errorDescription: Int,
    selectAllOnFocus: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = groupModifier.fillMaxWidth()
    ) {
        val textFieldModifier = if (selectAllOnFocus) {
            Modifier
                .onFocusSelectAll(
                    textFieldValueState = textFieldValue,
                    onTextFieldValueChanged = { onValueChange(it) }
                )
                .fillMaxWidth()
        } else Modifier

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = onValueChange,
            colors = colors,
            label = label,
            isError = isError,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
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
fun TotalScoreField(
    totalScoreBundle: SubscoreStateBundle,
    textFieldColors: TextFieldColors,
    onChange: (TextFieldValue) -> Unit,
    onDoneTap: () -> Unit
) {
    OutlinedTextFieldWithErrorDescription(
        textFieldValue = totalScoreBundle.textFieldValue,
        onValueChange = { onChange(it) },
        groupModifier = Modifier.padding(bottom = 8.dp),
        label = { Text(text = stringResource(id = R.string.field_total_score)) },
        isError = totalScoreBundle.validityState != ScoreStringValidityState.Valid,
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneTap() }
        ),
        errorDescription = totalScoreBundle.validityState.descriptionResource,
        selectAllOnFocus = true
    )
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
            isError = subscore.validityState != ScoreStringValidityState.Valid,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescription = subscore.validityState.descriptionResource,
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
        isError = uncategorizedScoreBundle.validityState != ScoreStringValidityState.Valid,
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneTap() }
        ),
        errorDescription = uncategorizedScoreBundle.validityState.descriptionResource,
        selectAllOnFocus = true
    )
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