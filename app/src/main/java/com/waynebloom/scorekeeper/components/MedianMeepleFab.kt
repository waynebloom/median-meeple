package com.waynebloom.scorekeeper.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MedianMeepleFab(
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    onClick: () -> Unit
) {

    FloatingActionButton(
        containerColor = containerColor,
        contentColor = contentColor,
        onClick = { onClick() },
        content = { Icon(imageVector = Icons.Rounded.Add, contentDescription = null) }
    )
}
