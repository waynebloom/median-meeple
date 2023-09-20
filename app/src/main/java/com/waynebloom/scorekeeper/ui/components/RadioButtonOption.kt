package com.waynebloom.scorekeeper.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.waynebloom.scorekeeper.enums.MenuOption

@Composable
fun RadioButtonOption(
    menuOption: MenuOption,
    isSelected: Boolean,
    onSelected: (MenuOption) -> Unit
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
                selectedColor = MaterialTheme.colors.primary
            )
        )

        Text(text = stringResource(menuOption.label))
    }
}
