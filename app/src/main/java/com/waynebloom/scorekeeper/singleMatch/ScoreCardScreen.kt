package com.waynebloom.scorekeeper.singleMatch

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions
import com.waynebloom.scorekeeper.ext.onFocusSelectAll
import com.waynebloom.scorekeeper.ext.toRank
import com.waynebloom.scorekeeper.ext.toShortFormatString
import com.waynebloom.scorekeeper.ext.toTextFieldInput
import com.waynebloom.scorekeeper.room.domain.model.CategoryScoreDomainModel
import com.waynebloom.scorekeeper.room.domain.model.GameDomainModel
import com.waynebloom.scorekeeper.room.domain.model.PlayerDomainModel
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun ScoreCardScreen(
    uiState: NewSingleMatchUiState,
    onPlayerClick: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddPlayer: (String) -> Unit,
    onDeletePlayerClick: (Int) -> Unit,
    onCellChange: (TextFieldValue, col: Int, row: Int) -> Unit,
    onDialogTextFieldChange: (TextFieldValue) -> Unit,
    onDateChange: (Long) -> Unit,
    onLocationChange: (String) -> Unit,
    onNotesChange: (TextFieldValue) -> Unit,
    onPlayerChange: (String, Int) -> Unit,
) {

    ScoreCardScreen(
        gameName = uiState.game.name.value.text,
        matchNumber = uiState.indexOfMatch,
        categoryNames = uiState.categoryNames,
        players = uiState.players,
        scoreCard = uiState.scoreCard,
        totals = uiState.totals,
        dateMillis = uiState.dateMillis,
        location = uiState.location,
        notes = uiState.notes,
        playerIndexToChange = uiState.playerIndexToChange,
        manualRanks = uiState.manualRanks,
        dialogTextFieldValue = uiState.dialogTextFieldValue,
        onPlayerClick,
        onSaveClick,
        onDeleteClick,
        onAddPlayer,
        onDeletePlayerClick,
        onCellChange,
        onDialogTextFieldChange,
        onDateChange,
        onLocationChange,
        onNotesChange,
        onPlayerChange,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class
)
@Composable
private fun ScoreCardScreen(
    gameName: String,
    matchNumber: Int,
    categoryNames: List<String>,
    players: List<PlayerDomainModel>,
    scoreCard: List<List<CategoryScoreDomainModel>>,
    totals: List<BigDecimal>,
    dateMillis: Long,
    location: String,
    notes: TextFieldValue,
    playerIndexToChange: Int,
    manualRanks: Boolean,
    dialogTextFieldValue: TextFieldValue,
    onPlayerClick: (Int) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAddPlayer: (String) -> Unit,
    onDeletePlayerClick: (Int) -> Unit,
    onCellChange: (TextFieldValue, col: Int, row: Int) -> Unit,
    onDialogTextFieldChange: (TextFieldValue) -> Unit,
    onDateChange: (Long) -> Unit,
    onLocationChange: (String) -> Unit,
    onNotesChange: (TextFieldValue) -> Unit,
    onPlayerChange: (String, Int) -> Unit,
) {
    Scaffold { paddingValues ->

        var showDatePickerDialog by remember { mutableStateOf(false) }
        var showLocationDialog by remember { mutableStateOf(false) }
        var showMoreDialog by remember { mutableStateOf(false) }
        var showEditPlayerDialog by remember { mutableStateOf(false) }
        var showNewPlayerDialog by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dateMillis,
            initialDisplayedMonthMillis = dateMillis,
        )

        if (showDatePickerDialog) {
            DatePickerDialog(
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colors.surface
                ),
                onDismissRequest = {
                    showDatePickerDialog = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePickerDialog = false
                            onDateChange(datePickerState.selectedDateMillis ?: 0)
                        },
                    ) {
                        Text(stringResource(R.string.text_ok), style = MaterialTheme.typography.button)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePickerDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.text_cancel), style = MaterialTheme.typography.button)
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        if (showMoreDialog) {
            Dialog(
                onDismissRequest = {
                    showMoreDialog = false
                },
                properties = DialogProperties(
                    decorFitsSystemWindows = false,
                    usePlatformDefaultWidth = false,
                )
            ) {
                Box(
                    contentAlignment = Alignment.BottomStart,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable { showMoreDialog = false })
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(
                                shape = MaterialTheme.shapes.medium.copy(
                                    bottomEnd = CornerSize(0.dp),
                                    bottomStart = CornerSize(0.dp)
                                )
                            )
                            .background(MaterialTheme.colors.background)
                            .padding(vertical = Dimensions.Spacing.screenEdge)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable(onClick = onDeleteClick)
                                .padding(
                                    horizontal = Dimensions.Spacing.screenEdge,
                                    vertical = Dimensions.Spacing.sectionContent
                                )
                                .fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Rounded.Delete, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(text = stringResource(R.string.text_delete))
                        }
                    }
                }
            }
        }
        if (showLocationDialog) {
            Dialog(onDismissRequest = { showLocationDialog = false }) {
                Surface(shape = MaterialTheme.shapes.large) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(true) {
                            focusRequester.requestFocus()
                        }

                        Text(
                            text = stringResource(R.string.field_location),
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                start = Dimensions.Spacing.screenEdge,
                                top = Dimensions.Spacing.screenEdge
                            )
                        )
                        OutlinedTextField(
                            value = dialogTextFieldValue,
                            onValueChange = onDialogTextFieldChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.Spacing.screenEdge)
                                .focusRequester(focusRequester)
                                .onFocusSelectAll(dialogTextFieldValue, onDialogTextFieldChange),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    showLocationDialog = false
                                    onLocationChange(dialogTextFieldValue.text)
                                }
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    end = Dimensions.Spacing.screenEdge,
                                    bottom = Dimensions.Spacing.screenEdge
                                )
                        ) {
                            TextButton(
                                onClick = {
                                    showLocationDialog = false
                                }
                            ) {
                                Text(text = stringResource(R.string.text_cancel), style = MaterialTheme.typography.button)
                            }
                            TextButton(
                                onClick = {
                                    showLocationDialog = false
                                    onLocationChange(dialogTextFieldValue.text)
                                }
                            ) {
                                Text(text = stringResource(R.string.text_ok), style = MaterialTheme.typography.button)
                            }
                        }
                    }
                }
            }
        }
        if (showEditPlayerDialog) {

            Dialog(onDismissRequest = { showEditPlayerDialog = false }) {
                var nameValue by remember {
                    mutableStateOf(TextFieldValue(players[playerIndexToChange].name))
                }
                var rank by remember {
                    if (manualRanks) {
                        players[playerIndexToChange].rank.let {
                            val validRanks = (1..players.size)
                            if (validRanks.contains(it)) {
                                mutableIntStateOf(it)
                            } else {
                                mutableIntStateOf(playerIndexToChange)
                            }
                        }
                    } else {
                        mutableIntStateOf(-1)
                    }
                }
                val onDismiss = {
                    showEditPlayerDialog = false
                }
                val onPositiveAction = {
                    showEditPlayerDialog = false
                    onPlayerChange(nameValue.text, rank)
                }
                Surface(shape = MaterialTheme.shapes.large) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(Dimensions.Spacing.screenEdge)
                    ) {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(true) {
                            focusRequester.requestFocus()
                        }

                        Text(
                            text = stringResource(R.string.text_edit_player),
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.SemiBold,
                        )
                        OutlinedTextField(
                            value = nameValue,
                            onValueChange = { nameValue = it},
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusSelectAll(
                                    textFieldValueState = nameValue,
                                    onTextFieldValueChanged = { nameValue = it }
                                ),
                            label = { Text(stringResource(R.string.field_name)) },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { onPositiveAction() }
                            )
                        )
                        if (manualRanks) {
                            FlowRow {
                                repeat(players.size) {
                                    val style = if (it == rank) {
                                        MaterialTheme.typography.body1.copy(
                                            color = MaterialTheme.colors.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    } else {
                                        MaterialTheme.typography.body1
                                    }
                                    val backgroundColor = if (it == rank) {
                                        MaterialTheme.colors.primary.copy(alpha = 0.2f)
                                    } else {
                                        Color.Transparent
                                    }
                                    Text(
                                        text = (it + 1).toRank(),
                                        style = style,
                                        modifier = Modifier
                                            .background(
                                                backgroundColor,
                                                MaterialTheme.shapes.medium
                                            )
                                            .clip(MaterialTheme.shapes.medium)
                                            .clickable { rank = it }
                                            .minimumInteractiveComponentSize()
                                            .padding(Dimensions.Spacing.sectionContent / 2)
                                    )
                                }
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = {
                                    onDeletePlayerClick(playerIndexToChange)
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.text_delete),
                                    style = MaterialTheme.typography.button,
                                    color = MaterialTheme.colors.error
                                )
                            }
                            Row {
                                TextButton(onDismiss) {
                                    Text(text = stringResource(R.string.text_cancel), style = MaterialTheme.typography.button)
                                }
                                TextButton(
                                    onClick = onPositiveAction,
                                    enabled = nameValue.text.isNotBlank(),
                                ) {
                                    Text(text = stringResource(R.string.text_ok), style = MaterialTheme.typography.button)
                                }
                            }
                        }
                    }
                }
            }
        }
        if (showNewPlayerDialog) {
            TextFieldDialog(
                title = stringResource(R.string.text_new_player),
                textFieldValue = dialogTextFieldValue,
                onTextFieldChange = onDialogTextFieldChange,
                positiveButtonEnabled = dialogTextFieldValue.text.isNotBlank(),
                onDismiss = {
                    showNewPlayerDialog = false
                },
                onPositiveButtonClick = {
                    showNewPlayerDialog = false
                    onAddPlayer(dialogTextFieldValue.text)
                }
            )
        }

        Column(Modifier.padding(paddingValues)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(start = Dimensions.Spacing.screenEdge)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "$gameName #$matchNumber",
                        style = MaterialTheme.typography.h5,
                        modifier =  Modifier.padding(top = Dimensions.Spacing.screenEdge)
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Rounded.Done,
                            contentDescription = null,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .padding(top = Dimensions.Spacing.screenEdge)
                                .clip(CircleShape)
                                .clickable(onClick = onSaveClick)
                                .padding(4.dp),
                        )
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = null,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .padding(
                                    top = Dimensions.Spacing.screenEdge,
                                    end = Dimensions.Spacing.screenEdge
                                )
                                .clip(CircleShape)
                                .clickable { showMoreDialog = true }
                                .padding(4.dp),
                        )
                    }
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.subSectionContent),
                ) {
                    InputChip(
                        selected = false,
                        onClick = {
                            showDatePickerDialog = true
                        },
                        label = {
                            val formatter = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT)
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            Text(text = formatter.format(dateMillis))
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_calendar),
                                contentDescription = null,
                                modifier = Modifier.size(InputChipDefaults.IconSize)
                            )
                        }
                    )
                    InputChip(
                        selected = false,
                        onClick = {
                            onDialogTextFieldChange(TextFieldValue(location))
                            showLocationDialog = true
                        },
                        label = {
                            Text(text = location.ifBlank { stringResource(R.string.location_hint) })
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Place,
                                contentDescription = null,
                                modifier = Modifier.size(InputChipDefaults.IconSize)
                            )
                        }
                    )
                }

                var hasFocus by remember { mutableStateOf(false) }
                BasicTextField(
                    value = notes,
                    onValueChange = onNotesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .padding(
                            bottom = Dimensions.Spacing.sectionContent,
                            end = Dimensions.Spacing.screenEdge
                        )
                        .onFocusChanged { hasFocus = it.hasFocus },
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onBackground
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
                    maxLines = 5,
                    decorationBox = {
                        Row {
                            Icon(
                                imageVector = Icons.Rounded.Edit,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            if (notes.text.isEmpty() && !hasFocus) {
                                Text(text = stringResource(R.string.notes_hint))
                            } else {
                                it()
                            }
                        }
                    }
                )
            }
            Surface(
                shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                modifier = Modifier.padding(start = Dimensions.Spacing.screenEdge)
            ) {

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {

                    stickyHeader {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .width(intrinsicSize = IntrinsicSize.Max)
                                .background(
                                    color = MaterialTheme.colors.primary
                                        .copy(alpha = 0.2f)
                                        .compositeOver(MaterialTheme.colors.surface),
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        bottomStart = 16.dp
                                    )
                                )
                        ) {

                            // Blank space in the corner
                            Box(
                                contentAlignment = Alignment.CenterEnd,
                                modifier = Modifier.size(60.dp)
                            ) {
                                VerticalDivider(color = MaterialTheme.colors.onSurface)
                            }

                            categoryNames.forEach {
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier
                                        .height(IntrinsicSize.Max)
                                        .fillMaxWidth()
                                ) {
                                    Box(
                                        contentAlignment = Alignment.CenterEnd,
                                        modifier = Modifier
                                            .padding(
                                                top = Dimensions.Spacing.sectionContent / 2,
                                                start = Dimensions.Spacing.screenEdge,
                                                bottom = Dimensions.Spacing.sectionContent / 2,
                                                end = Dimensions.Spacing.sectionContent / 2
                                            )
                                            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
                                    ) {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.body1,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    VerticalDivider(color = MaterialTheme.colors.onSurface)
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier
                                    .height(IntrinsicSize.Max)
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterEnd,
                                    modifier = Modifier
                                        .padding(
                                            top = Dimensions.Spacing.sectionContent / 2,
                                            start = Dimensions.Spacing.screenEdge,
                                            bottom = Dimensions.Spacing.sectionContent / 2,
                                            end = Dimensions.Spacing.sectionContent / 2
                                        )
                                        .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
                                ) {
                                    Text(
                                        text = stringResource(R.string.text_total),
                                        style = MaterialTheme.typography.body1,
                                        fontWeight = FontWeight.SemiBold,
                                        fontStyle = FontStyle.Italic
                                    )
                                }
                                VerticalDivider(color = MaterialTheme.colors.onSurface)
                            }
                        }
                    }

                    itemsIndexed(players) { index, player ->
                        ScoreColumn(
                            playerName = player.name,
                            playerRank = player.rank,
                            scores = scoreCard[index],
                            total = totals[index].toShortFormatString(),
                            modifier = Modifier.animateItemPlacement(),
                            onPlayerClick = {
                                showEditPlayerDialog = true
                                onPlayerClick(index)
                            },
                            onCellChange = { value, col -> onCellChange(value, col, index) },
                        )
                    }

                    item {
                        IconButton(
                            onClick = {
                                onDialogTextFieldChange(TextFieldValue())
                                showNewPlayerDialog = true
                            },
                        ) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TextFieldDialog(
    title: String,
    textFieldValue: TextFieldValue,
    onTextFieldChange: (TextFieldValue) -> Unit,
    positiveButtonEnabled: Boolean,
    onDismiss: () -> Unit,
    onPositiveButtonClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }

    Dialog(onDismiss) {
        Surface(shape = MaterialTheme.shapes.large) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(
                        start = Dimensions.Spacing.screenEdge,
                        top = Dimensions.Spacing.screenEdge
                    )
                )
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = onTextFieldChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.Spacing.screenEdge)
                        .focusRequester(focusRequester)
                        .onFocusSelectAll(textFieldValue, onTextFieldChange),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onPositiveButtonClick()
                        }
                    )
                )

                val arrangement = if (onDeleteClick != null) Arrangement.SpaceBetween else Arrangement.End
                Row(
                    horizontalArrangement = arrangement,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = Dimensions.Spacing.screenEdge,
                            end = Dimensions.Spacing.screenEdge,
                            bottom = Dimensions.Spacing.screenEdge
                        )
                ) {
                    if (onDeleteClick != null) {
                        TextButton(onDeleteClick) {
                            Text(
                                text = stringResource(R.string.text_delete),
                                style = MaterialTheme.typography.button,
                                color = MaterialTheme.colors.error
                            )
                        }
                    }
                    Row {
                        TextButton(onDismiss) {
                            Text(text = stringResource(R.string.text_cancel), style = MaterialTheme.typography.button)
                        }
                        TextButton(
                            onClick = onPositiveButtonClick,
                            enabled = positiveButtonEnabled,
                        ) {
                            Text(text = stringResource(R.string.text_ok), style = MaterialTheme.typography.button)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreColumn(
    playerName: String,
    playerRank: Int,
    scores: List<CategoryScoreDomainModel>,
    total: String,
    modifier: Modifier = Modifier,
    onPlayerClick: () -> Unit,
    onCellChange: (TextFieldValue, col: Int) -> Unit
) {
    Column(modifier.width(intrinsicSize = IntrinsicSize.Max)) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .clickable(onClick = onPlayerClick)
                .padding(
                    vertical = Dimensions.Spacing.sectionContent / 2,
                    horizontal = Dimensions.Spacing.sectionContent
                )
                .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
        ) {
            Column {
                Text(
                    text = playerName,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.SemiBold
                )
                if (playerRank > -1) {
                    Text(
                        text = playerRank.plus(1).toRank(),
                        style = MaterialTheme.typography.body2,
                    )
                }
            }
        }

        scores.forEachIndexed { index, score ->
            TextFieldCell(
                value = score.scoreAsTextFieldValue,
                onValueChange = { onCellChange(it, index) },
                isError = score.scoreAsTextFieldValue.text.isNotBlank() && score.scoreAsBigDecimal == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Spacing.sectionContent / 2),
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimensions.Spacing.sectionContent / 2)
                .background(
                    color = MaterialTheme.colors.primary.copy(0.2f),
                    shape = CircleShape
                )
                .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp),
        ) {
            Text(
                text = total,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(horizontal = Dimensions.Spacing.sectionContent)
            )
        }
    }
}

@Composable
fun TextFieldCell(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {

    var hasFocus by remember { mutableStateOf(false) }
    val backgroundColor = when {
        hasFocus -> MaterialTheme.colors.background
        isError -> MaterialTheme.colors.error.copy(alpha = 0.2f)
        else -> Color.Transparent
    }
    val textStyle = if (isError) {
        MaterialTheme.typography.body1.copy(
            color = MaterialTheme.colors.error,
            fontWeight = FontWeight.SemiBold,
            fontStyle = FontStyle.Italic
        )
    } else {
        MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onSurface)
    }
    val backgroundColorAsState by animateColorAsState(targetValue = backgroundColor, label = "")
    val borderAlpha by animateFloatAsState(targetValue = if (hasFocus) 1f else 0f, label = "")

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(color = backgroundColorAsState)
            .border(
                width = 2.dp,
                color = MaterialTheme.colors.primary.copy(alpha = borderAlpha),
                shape = MaterialTheme.shapes.small
            )
            .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
            .padding(horizontal = Dimensions.Spacing.sectionContent)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.onFocusChanged { hasFocus = it.hasFocus },
            textStyle = textStyle,
        )
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SingleMatchScreenPreview() {
    MedianMeepleTheme {
        ScoreCardScreen(
            uiState = NewSingleMatchUiState(
                totals = listOf(
                    BigDecimal(20),
                    BigDecimal(20),
                    BigDecimal(40),
                    BigDecimal(70),
                    BigDecimal(70),
                    BigDecimal(20),
                ),
                game = GameDomainModel(name = "Wingspan".toTextFieldInput()),
                categoryNames = listOf("Red", "Orange", "Yellow", "Green", "Blue"),
                players = listOf("Wayne", "Conor", "Alyssa", "Brock", "Tim", "Benjamin").map {
                    PlayerDomainModel(name = it)
                },
                scoreCard = listOf(
                    (1 until 10 step 2),
                    (0 until 10 step 2),
                    (6..10),
                    (10 until 20 step 2),
                    (11 until 20 step 2),
                    (1 until 10 step 2),
                ).map { row ->
                    row.map { col ->
                        CategoryScoreDomainModel(
                            scoreAsTextFieldValue = TextFieldValue(col.toString()),
                            scoreAsBigDecimal = BigDecimal(col)
                        )
                    }
                },
                dateMillis = 999999999L,
                indexOfMatch = 43,
            ),
            onPlayerClick = {},
            onSaveClick = {},
            onDeleteClick = {},
            onAddPlayer = {},
            onDeletePlayerClick = {},
            onCellChange = {_,_,_->},
            onDialogTextFieldChange = {},
            onDateChange = {},
            onLocationChange = {},
            onNotesChange = {},
            onPlayerChange = {_,_->}
        )
    }
}

@Preview
@Composable
private fun PlayerSectionBelowMaxPlayersPreview() {

}

@Preview
@Composable
private fun PlayerSectionAboveMaxPlayersPreview() {

}

@Preview
@Composable
private fun OtherSectionPreview() {

}
