package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.enums.MatchSortingMode

@Composable
fun SearchActionBar(
    searchString: String,
    themeColor: Color,
    onSearchStringChanged: (String) -> Unit,
    onCloseTap: () -> Unit
) {
    var searchBarFocused: Boolean by rememberSaveable { mutableStateOf(false) }
    val textSelectionColors = TextSelectionColors(
        handleColor = themeColor,
        backgroundColor = themeColor.copy(0.3f)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = themeColor,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        CompositionLocalProvider(
            LocalTextSelectionColors.provides(textSelectionColors)
        ) {
            BasicTextField(
                value = searchString.ifEmpty {
                    if (!searchBarFocused) {
                        stringResource(R.string.search_placeholder_match)
                    } else ""
                },
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSurface,
                ),
                singleLine = true,
                cursorBrush = SolidColor(themeColor),
                onValueChange = { onSearchStringChanged(it) },
                keyboardActions = KeyboardActions(
                    onDone = { onCloseTap() }
                ),
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { searchBarFocused = it.hasFocus }
            )
        }

        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .clickable { onCloseTap() }
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                tint = themeColor,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun SortingMenuOption(
    menuOption: MatchSortingMode,
    themeColor: Color,
    isSelected: Boolean,
    onSelected: (MatchSortingMode) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onSelected(menuOption) }
            .fillMaxWidth()
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSelected(menuOption) },
            colors = RadioButtonDefaults.colors(
                selectedColor = themeColor,
                unselectedColor = MaterialTheme.colors.onSurface
            )
        )
        Text(
            text = stringResource(menuOption.label),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.SemiBold,
        )
    }
}