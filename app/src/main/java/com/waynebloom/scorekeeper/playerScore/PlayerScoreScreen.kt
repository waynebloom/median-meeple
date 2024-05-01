package com.waynebloom.scorekeeper.playerScore

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
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.components.IconButton
import com.waynebloom.scorekeeper.components.HelperBox
import com.waynebloom.scorekeeper.components.HelperBoxType
import com.waynebloom.scorekeeper.components.OutlinedTextFieldWithErrorDescription
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions.Size
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.MatchObjectsDefaultPreview
import com.waynebloom.scorekeeper.room.data.model.MatchDataRelationModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataModel
import com.waynebloom.scorekeeper.room.data.model.PlayerDataRelationModel
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreEntityState
import com.waynebloom.scorekeeper.room.data.model.CategoryDataModel
import com.waynebloom.scorekeeper.enums.ValidityState
import com.waynebloom.scorekeeper.enums.TopLevelScreen
import com.waynebloom.scorekeeper.room.domain.model.EntityStateBundle
import com.waynebloom.scorekeeper.theme.Animation.delayedFadeInWithFadeOut
import com.waynebloom.scorekeeper.theme.Animation.sizeTransformWithDelay
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import com.waynebloom.scorekeeper.theme.color.deepOrange500

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlayerScoreScreen(
    initialPlayer: PlayerDataRelationModel,
    matchObject: MatchDataRelationModel,
    categories: List<CategoryDataModel>,
    isGameManualRanked: Boolean,
    themeColor: Color,
    onSaveClick: (EntityStateBundle<PlayerDataModel>, List<CategoryScoreEntityState>) -> Unit,
    onDeleteClick: (Long) -> Unit,
) {
    val viewModel = viewModel<EditPlayerScoreViewModel>(
        key = TopLevelScreen.EditPlayerScore.name,
        factory = EditPlayerScoreViewModelFactory(
            playerObject = initialPlayer,
            matchObject = matchObject,
            playerSubscores = initialPlayer.score,
            subscoreTitles = categories,
            isGameManualRanked = isGameManualRanked,
            saveCallback = onSaveClick
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
                onDoneClick = { viewModel.onSaveClick(keyboardController) },
                onDeleteClick = { onDeleteClick(initialPlayer.entity.id) }
            )
        }
    ) { contentPadding ->

        CompositionLocalProvider(
            LocalTextSelectionColors.provides(
                TextSelectionColors(
                    handleColor = themeColor,
                    backgroundColor = themeColor.copy(Alpha.textSelectionBackground)
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
                        onDoneClick = { viewModel.onSaveClick(keyboardController) },
                        onFieldChanged = { id, value -> viewModel.onCategoryFieldChanged(id, value) },
                        onNextClick = { focusManager.moveFocus(FocusDirection.Next) },
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
    onDoneClick: () -> Unit,
    onDeleteClick: () -> Unit,
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

                IconButton(
                    imageVector = Icons.Rounded.Done,
                    backgroundColor = Color.Transparent,
                    foregroundColor = themeColor,
                    enabled = submitButtonEnabled,
                    onClick = onDoneClick
                )

                IconButton(
                    imageVector = Icons.Rounded.Delete,
                    backgroundColor = Color.Transparent,
                    foregroundColor = MaterialTheme.colors.error,
                    onClick = onDeleteClick
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
            value = name,
            onValueChange = { onNameChanged(it) },
            label = { Text(text = stringResource(id = R.string.field_name)) },
            isError = isError,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescriptionResource = R.string.error_empty_name,
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
            value = playerRankTextFieldValue,
            onValueChange = { onPlayerRankUpdate(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            label = { Text(text = stringResource(id = R.string.field_rank)) },
            isError = showPlayerRankError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.moveFocus(FocusDirection.Next) }
            ),
            errorDescriptionResource = R.string.error_invalid_rank,
            selectAllOnFocus = true
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ScoreSection(
    categoryData: List<CategoryScoreEntityState>,
    categoryTitles: List<CategoryDataModel>,
    totalScoreData: CategoryScoreEntityState,
    uncategorizedScoreData: CategoryScoreEntityState,
    isDetailedMode: Boolean,
    themeColor: Color,
    textFieldColors: TextFieldColors,
    onDetailedModeChanged: (Boolean) -> Unit,
    onDoneClick: () -> Unit,
    onFieldChanged: (Long, TextFieldValue) -> Unit,
    onNextClick: () -> Unit,
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
                        onDoneClick = onDoneClick,
                        onNextClick = onNextClick,
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
                    onDoneClick = onDoneClick,
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
    totalScoreData: CategoryScoreEntityState,
    textFieldColors: TextFieldColors,
    onChanged: (TextFieldValue) -> Unit,
    onDoneClick: () -> Unit
) {
    OutlinedTextFieldWithErrorDescription(
        value = totalScoreData.textFieldValue,
        onValueChange = { onChanged(it) },
        modifier = Modifier.padding(bottom = 8.dp),
        label = { Text(text = stringResource(id = R.string.field_total_score)) },
        isError = totalScoreData.validityState != ValidityState.Valid,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onDoneClick() }
        ),
        errorDescriptionResource = totalScoreData.validityState.descriptionResource,
        selectAllOnFocus = true
    )
}

@Composable
private fun CategoryFields(
    categoryData: List<CategoryScoreEntityState>,
    categoryTitles: List<CategoryDataModel>,
    uncategorizedScoreData: CategoryScoreEntityState,
    textFieldColors: TextFieldColors,
    onDoneClick: () -> Unit,
    onFieldChanged: (Long, TextFieldValue) -> Unit,
    onNextClick: () -> Unit,
    onUncategorizedFieldChanged: (TextFieldValue) -> Unit,
) {

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sectionContent)) {

        categoryData.forEachIndexed { index, data ->

            OutlinedTextFieldWithErrorDescription(
                value = data.textFieldValue,
                onValueChange = { onFieldChanged(data.entity.categoryId, it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = categoryTitles[index].name) },
                isError = data.validityState != ValidityState.Valid,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { onNextClick() }
                ),
                errorDescriptionResource = data.validityState.descriptionResource,
                selectAllOnFocus = true
            )
        }

        OutlinedTextFieldWithErrorDescription(
            value = uncategorizedScoreData.textFieldValue,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { onUncategorizedFieldChanged(it) },
            label = { Text(text = stringResource(id = R.string.field_uncategorized)) },
            isError = uncategorizedScoreData.validityState != ValidityState.Valid,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDoneClick() }
            ),
            errorDescriptionResource = uncategorizedScoreData.validityState.descriptionResource,
            selectAllOnFocus = true
        )
    }
}

@Preview(backgroundColor = 0xFFF0EAE2, showBackground = true)
@Composable
fun EditPlayerScoreScreenPreview() {
    MedianMeepleTheme {
        PlayerScoreScreen(
            initialPlayer = PlayerDataRelationModel(
                PlayerDataModel(
                    name = "Wayne",
                    showDetailedScore = true
                )
            ),
            matchObject = MatchObjectsDefaultPreview[0],
            categories = listOf(
                CategoryDataModel(name = "Eggs"),
                CategoryDataModel(name = "Tucked Cards"),
                CategoryDataModel(name = "Cached Food"),
                CategoryDataModel(name = "Birds")
            ),
            isGameManualRanked = true,
            themeColor = deepOrange500,
            onSaveClick = { _, _ -> },
            onDeleteClick = {}
        )
    }
}
