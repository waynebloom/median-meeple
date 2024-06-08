package com.waynebloom.scorekeeper.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.base.LocalCustomThemeColors
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun GameCard(
    name: String,
    color: Color,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
) {
    val adjustedColor = color
        .copy(alpha = 0.2f)
        .compositeOver(MaterialTheme.colorScheme.surfaceVariant)

    Surface(
        color = adjustedColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = name.ifEmpty { stringResource(id = R.string.text_no_game_name) },
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .clickable(onClick = onClick)
                .minimumInteractiveComponentSize()
                .padding(horizontal = 12.dp),
        )
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun GameCardNewPreview() {
    MedianMeepleTheme {
        Surface {
            Column {
                LocalCustomThemeColors.current.getColorsAsKeyList().forEach { key ->
                    GameCard(
                        name = "Wingspan",
                        color = LocalCustomThemeColors.current.getColorByKey(key),
                        onClick = {},
                    )
                }
            }
        }
    }
}
