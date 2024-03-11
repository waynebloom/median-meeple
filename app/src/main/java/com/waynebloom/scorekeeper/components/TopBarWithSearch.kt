package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions

@Composable
fun TopBarWithSearch(
    searchInput: TextFieldValue,
    onClearClick: () -> Unit,
    onSearchInputChanged: (TextFieldValue) -> Unit,
    onCloseClick: () -> Unit
) {
    var isSearchFieldFocused by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isSearchFieldFocused) {
        focusRequester.requestFocus()
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
            tint = MaterialTheme.colors.primary,
            modifier = Modifier.padding(end = 16.dp),
        )

        Box(Modifier.weight(1f)) {
            BasicTextField(
                value = searchInput,
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onBackground,
                ),
                cursorBrush = SolidColor(MaterialTheme.colors.onBackground),
                singleLine = true,
                onValueChange = { onSearchInputChanged(it) },
                keyboardActions = KeyboardActions(
                    onDone = { onCloseClick() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { isSearchFieldFocused = it.hasFocus }
            )

            if (!isSearchFieldFocused) {
                Text(text = stringResource(R.string.search_placeholder_match))
            }
        }

        IconButton(
            painter = painterResource(id = R.drawable.ic_search_off),
            backgroundColor = Color.Transparent,
            foregroundColor = MaterialTheme.colors.primary,
            onClick = onClearClick
        )

        IconButton(
            imageVector = Icons.Rounded.Close,
            backgroundColor = Color.Transparent,
            foregroundColor = MaterialTheme.colors.primary,
            onClick = onCloseClick
        )
    }
}
