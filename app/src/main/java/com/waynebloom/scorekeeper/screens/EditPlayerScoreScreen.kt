package com.waynebloom.scorekeeper.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.ui.theme.MedianMeepleTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.data.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.CustomIconButton
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.data.model.*
import com.waynebloom.scorekeeper.data.model.match.MatchObject
import com.waynebloom.scorekeeper.data.model.player.PlayerEntity
import com.waynebloom.scorekeeper.data.model.player.PlayerObject
import com.waynebloom.scorekeeper.data.model.subscore.SubscoreStateBundle
import com.waynebloom.scorekeeper.data.model.subscoretitle.CategoryTitleEntity
import com.waynebloom.scorekeeper.enums.ScoreStringValidityState
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.ui.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.ui.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.ui.theme.color.deepOrange500
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModel
import com.waynebloom.scorekeeper.viewmodel.EditPlayerScoreViewModelFactory

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditPlayerScoreScreen(
    initialPlayer: PlayerObject,
    matchObject: MatchObject,
    subscoreTitles: List<CategoryTitleEntity>,
    isGameManualRanked: Boolean,
    themeColor: Color,
    onSaveTap: (EntityStateBundle<PlayerEntity>, List<SubscoreStateBundle>) -> Unit,
    onDeleteTap: (Long) -> Unit,
) {
    val viewModel = viewModel<EditPlayerScoreViewModel>(
        key = TopLevelScreen.EditPlayerScore.name,
        factory = EditPlayerScoreViewModelFactory(
            playerObject = initialPlayer,
            matchObject = matchObject,
            playerSubscores = initialPlayer.score,
            subscoreTitles = subscoreTitles,
            isGameManualRanked = isGameManualRanked,
            saveCallback = onSaveTap
        )
    ).also { it.initialPlayerEntity.id = initialPlayer.entity.id }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = themeColor,
        focusedLabelColor = themeColor,
        cursorColor = themeColor,
    )

    Scaffold(
        topBar = {
            EditPlayerScoreScreenTopBar(
                title = initialPlayer.entity.name,
                themeColor = themeColor,
                submitButtonEnabled = viewModel.isSubmitButtonEnabled(),
                onDoneTap = { viewModel.onSaveTap(keyboardController) },
                onDeleteTap = { onDeleteTap(initialPlayer.entity.id) }
            )
        }
    ) { contentPadding ->

        CompositionLocalProvider(
            LocalTextSelectionColors.provides(
                TextSelectionColors(
                    handleColor = themeColor,
                    backgroundColor = themeColor.copy(0.3f)
                )
            )
        ) {

            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = Spacing.screenEdge,
                    vertical = Spacing.betweenSections
                ),
                verticalArrangement = Arrangement.spacedBy(Spacing.betweenSections),
                modifier = Modifier.padding(contentPadding),
            ) {

                item {

                    InformationSection(
                        name = viewModel.name,
                        isError = !viewModel.isNameValid,
                        focusManager = focusManager,
                        textFieldColors = textFieldColors,
                        onNameChanged = { viewModel.onNameChanged(it) },
                    )
                }

                if (isGameManualRanked) {
                    item {

                        RankingSection(
                            playerRankTextFieldValue = viewModel.playerRank,
                            showPlayerRankError = !viewModel.playerRankIsValid,
                            textFieldColors = textFieldColors,
                            focusManager = focusManager,
                            onPlayerRankUpdate = { viewModel.onPlayerRankChanged(it) },
                        )
                    }
                }

                item {

                    ScoreSection(
                        categoryData = viewModel.categoryData,
                        categoryTitles = viewModel.categoryTitles,
                        totalScoreData = viewModel.totalScoreData,
                        uncategorizedScoreData = viewModel.uncategorizedScoreData,
                        isDetailedMode = viewModel.isDetailedMode,
                        themeColor = themeColor,
                        textFieldColors = textFieldColors,
                        onDetailedModeChanged = { viewModel.onDetailedModeChanged(it) },
                        onDoneTap = { viewModel.onSaveTap(keyboardController) },
                        onFieldChanged = { id, value -> viewModel.onCategoryFieldChanged(id, value) },
                        onNextTap = { focusManager.moveFocus(FocusDirection.Next) },
                        onTotalFieldChanged = { viewModel.onTotalFieldChanged(it) },
                        onUncategorizedFieldChanged = { viewModel.onUncategorizedFieldChanged(it) }
                    )
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

    Column {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .defaultMinSize(minHeight = Size.topBarHeight)
                .fillMaxWidth(),
        ) {

            Text(
                text = title,
                color = themeColor,
                style = MaterialTheme.typography.h5,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Row(verticalAlignment = Alignment.CenterVertically) {

                CustomIconButton(
                    imageVector = Icons.Rounded.Done,
                    backgroundColor = Color.Transparent,
                    foregroundColor = themeColor,
                    enabled = submitButtonEnabled,
                    onTap = onDoneTap
                )

                CustomIconButton(
                    imageVector = Icons.Rounded.Delete,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    onTap = onDeleteTap
                )
            }
        }

        Divider()
    }
}

@Composable
private fun InformationSection(
    name: TextFieldValue,
    isError: Boolean,
    focusManager: FocusManager,
    textFieldColors: TextFieldColors,
    onNameChanged: (TextFieldValue) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_info),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        OutlinedTextFieldWithErrorDescription(
            textFieldValue = name,
            onValueChange = { onNameChanged(it) },
            label = { Text(text = stringResource(id = R.string.field_name)) },
            isError = isError,
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

@Composable
private fun RankingSection(
    playerRankTextFieldValue: TextFieldValue,
    showPlayerRankError: Boolean,
    textFieldColors: TextFieldColors,
    focusManager: FocusManager,
    onPlayerRankUpdate: (TextFieldValue) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_ranking),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        HelperBox(
            message = stringResource(R.string.helper_player_score_screen_rank),
            type = HelperBoxType.Info,
        )

        OutlinedTextFieldWithErrorDescription(
            textFieldValue = playerRankTextFieldValue,
            onValueChange = { onPlayerRankUpdate(it) },
            groupModifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text(text = stringResource(id = R.string.field_rank)) },
            isError = showPlayerRankError,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescription = R.string.error_invalid_rank,
            selectAllOnFocus = true
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoreSection(
    categoryData: List<SubscoreStateBundle>,
    categoryTitles: List<CategoryTitleEntity>,
    totalScoreData: SubscoreStateBundle,
    uncategorizedScoreData: SubscoreStateBundle,
    isDetailedMode: Boolean,
    themeColor: Color,
    textFieldColors: TextFieldColors,
    onDetailedModeChanged: (Boolean) -> Unit,
    onDoneTap: () -> Unit,
    onFieldChanged: (Long, TextFieldValue) -> Unit,
    onNextTap: () -> Unit,
    onTotalFieldChanged: (TextFieldValue) -> Unit,
    onUncategorizedFieldChanged: (TextFieldValue) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        Text(
            text = stringResource(id = R.string.header_player_scores),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.SemiBold,
        )

        DetailedScoreSwitchRow(
            checked = isDetailedMode,
            onCheckedChange = { onDetailedModeChanged(it) },
            themeColor = themeColor,
        )

        AnimatedContent(
            targetState = isDetailedMode,
            transitionSpec = { delayedFadeInWithFadeOut using sizeTransformWithDelay }
        ) {

            if (it) {

                if (categoryTitles.isNotEmpty()) {

                    CategoryFields(
                        categoryData = categoryData,
                        categoryTitles = categoryTitles,
                        textFieldColors = textFieldColors,
                        uncategorizedScoreData = uncategorizedScoreData,
                        onDoneTap = onDoneTap,
                        onNextTap = onNextTap,
                        onFieldChanged = onFieldChanged,
                        onUncategorizedFieldChanged = onUncategorizedFieldChanged,
                    )
                } else {

                    HelperBox(
                        message = stringResource(R.string.helper_player_score_screen_detailed_mode),
                        type = HelperBoxType.Error,
                    )
                }
            } else {
                TotalScoreField(
                    totalScoreData = totalScoreData,
                    textFieldColors = textFieldColors,
                    onChanged = onTotalFieldChanged,
                    onDoneTap = onDoneTap,
                )
            }
        }
    }
}

@Composable
private fun DetailedScoreSwitchRow(
    checked: Boolean,
    themeColor: Color,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
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
private fun TotalScoreField(
    totalScoreData: SubscoreStateBundle,
    textFieldColors: TextFieldColors,
    onChanged: (TextFieldValue) -> Unit,
    onDoneTap: () -> Unit
) {
    OutlinedTextFieldWithErrorDescription(
        textFieldValue = totalScoreData.textFieldValue,
        onValueChange = { onChanged(it) },
        groupModifier = Modifier.padding(bottom = 8.dp),
        label = { Text(text = stringResource(id = R.string.field_total_score)) },
        isError = totalScoreData.validityState != ScoreStringValidityState.Valid,
        colors = textFieldColors,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneTap() }
        ),
        errorDescription = totalScoreData.validityState.descriptionResource,
        selectAllOnFocus = true
    )
}

@Composable
private fun CategoryFields(
    categoryData: List<SubscoreStateBundle>,
    categoryTitles: List<CategoryTitleEntity>,
    uncategorizedScoreData: SubscoreStateBundle,
    textFieldColors: TextFieldColors,
    onDoneTap: () -> Unit,
    onFieldChanged: (Long, TextFieldValue) -> Unit,
    onNextTap: () -> Unit,
    onUncategorizedFieldChanged: (TextFieldValue) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        categoryData.forEachIndexed { index, data ->

            OutlinedTextFieldWithErrorDescription(
                textFieldValue = data.textFieldValue,
                onValueChange = { onFieldChanged(data.entity.categoryTitleId, it) },
                groupModifier = Modifier.fillMaxWidth(),
                label = { Text(text = categoryTitles[index].title) },
                isError = data.validityState != ScoreStringValidityState.Valid,
                colors = textFieldColors,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNextTap() }
                ),
                errorDescription = data.validityState.descriptionResource,
                selectAllOnFocus = true
            )
        }

        OutlinedTextFieldWithErrorDescription(
            textFieldValue = uncategorizedScoreData.textFieldValue,
            groupModifier = Modifier.fillMaxWidth(),
            onValueChange = { onUncategorizedFieldChanged(it) },
            label = { Text(text = stringResource(id = R.string.field_uncategorized)) },
            isError = uncategorizedScoreData.validityState != ScoreStringValidityState.Valid,
            colors = textFieldColors,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDoneTap() }
            ),
            errorDescription = uncategorizedScoreData.validityState.descriptionResource,
            selectAllOnFocus = true
        )
    }
}

@Preview(backgroundColor = 0xFFF0EAE2, showBackground = true)
@Composable
fun EditPlayerScoreScreenPreview() {
    MedianMeepleTheme {
        EditPlayerScoreScreen(
            initialPlayer = PlayerObject(
                PlayerEntity(
                    name = "Wayne",
                    showDetailedScore = true
                )
            ),
            matchObject = MatchObjectsDefaultPreview[0],
            subscoreTitles = listOf(
                CategoryTitleEntity(title = "Eggs"),
                CategoryTitleEntity(title = "Tucked Cards"),
                CategoryTitleEntity(title = "Cached Food"),
                CategoryTitleEntity(title = "Birds")
            ),
            isGameManualRanked = true,
            themeColor = deepOrange500,
            onSaveTap = { _, _ -> },
            onDeleteTap = {}
        )
    }
}