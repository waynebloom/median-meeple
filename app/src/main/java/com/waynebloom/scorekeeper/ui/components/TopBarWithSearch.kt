package com.waynebloom.scorekeeper.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Alpha
import com.waynebloom.scorekeeper.constants.Dimensions

@Composable
fun TopBarWithSearch(
    isSearchBarFocused: Boolean,
    searchString: String,
    themeColor: Color,
    onClearFiltersTap: () -> Unit,
    onSearchBarFocusChanged: (Boolean) -> Unit,
    onSearchStringChanged: (String) -> Unit,
    onCloseTap: () -> Unit
) {
    val textSelectionColors = TextSelectionColors(
        handleColor = themeColor,
        backgroundColor = themeColor.copy(Alpha.textSelectionBackground)
    )
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchBarFocused) {
        if (isSearchBarFocused) {
            focusRequester.requestFocus()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.Size.topBarHeight),
    ) {

        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = themeColor,
            modifier = Modifier.padding(end = 16.dp),
        )

        CompositionLocalProvider(LocalTextSelectionColors.provides(textSelectionColors)) {

            BasicTextField(
                value = searchString.ifEmpty {
                    if (!isSearchBarFocused) {
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
                    .focusRequester(focusRequester)
                    .onFocusChanged { onSearchBarFocusChanged(it.hasFocus) }
            )
        }

        IconButton(
            painter = painterResource(id = R.drawable.ic_search_off),
            backgroundColor = Color.Transparent,
            foregroundColor = themeColor,
            onClick = onClearFiltersTap
        )

        IconButton(
            imageVector = Icons.Rounded.Close,
            backgroundColor = Color.Transparent,
            foregroundColor = themeColor,
            onClick = onCloseTap
        )
    }
}
