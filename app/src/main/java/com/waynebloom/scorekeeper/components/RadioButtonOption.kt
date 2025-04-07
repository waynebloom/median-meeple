package com.waynebloom.scorekeeper.components

import android.R
import android.R.attr.fontStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.waynebloom.scorekeeper.enums.MenuOption

@Composable
fun <T : MenuOption> RadioButtonOption(
	modifier: Modifier = Modifier,
	menuOption: T,
	isSelected: Boolean,
	onSelected: (T) -> Unit,
	unselectedColor: Color = MaterialTheme.colorScheme.onSurface,
) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier
			.clip(MaterialTheme.shapes.medium)
			.clickable { onSelected(menuOption) }
			.fillMaxWidth()
	) {
		RadioButton(
			selected = isSelected,
			onClick = { onSelected(menuOption) },
		)
		Text(
			text = stringResource(menuOption.label),
			style = MaterialTheme.typography.labelLarge,
			color = unselectedColor,
			fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
		)
	}
}
