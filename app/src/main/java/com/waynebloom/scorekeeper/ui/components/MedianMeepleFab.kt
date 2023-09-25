package com.waynebloom.scorekeeper.ui.components

import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MedianMeepleFab(
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary,
    onClick: () -> Unit
) {

    FloatingActionButton(
        shape = MaterialTheme.shapes.large,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        onClick = { onClick() },
        content = { Icon(imageVector = Icons.Rounded.Add, contentDescription = null) }
    )
}
