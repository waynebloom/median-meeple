package com.waynebloom.scorekeeper.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.R
import com.waynebloom.scorekeeper.constants.Dimensions.Spacing
import com.waynebloom.scorekeeper.theme.MedianMeepleTheme

@Composable
fun HelperBox(
    message: String,
    type: HelperBoxType,
    modifier: Modifier = Modifier,
    maxLines: Int = 10,
) {
    val icon = when(type) {
        HelperBoxType.Info -> painterResource(id = R.drawable.ic_info_circle)
        HelperBoxType.Error -> painterResource(id = R.drawable.ic_error_circle)
        HelperBoxType.Missing -> painterResource(id = R.drawable.ic_help_circle)
    }
    val backgroundColor = when(type) {
        HelperBoxType.Error -> MaterialTheme.colors.error
        HelperBoxType.Missing -> MaterialTheme.colors.surface
        else -> Color.Transparent
    }
    val foregroundColor = when(type) {
        HelperBoxType.Error -> MaterialTheme.colors.onError
        HelperBoxType.Missing -> MaterialTheme.colors.onSurface
        else -> MaterialTheme.colors.onBackground
    }
    val borderColor = if (type != HelperBoxType.Info) backgroundColor else foregroundColor

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(color = backgroundColor)
            .fillMaxWidth()
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sectionContent),
            modifier = Modifier.padding(Spacing.sectionContent),
        ) {

            Icon(
                painter = icon,
                contentDescription = null,
                tint = foregroundColor,
                modifier = Modifier.align(Alignment.Top))

            Text(
                text = message,
                color = foregroundColor,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

enum class HelperBoxType {
    Info,
    Error,
    Missing;
}

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Short")
@Composable
fun HelperBoxShortPreview() {
    MedianMeepleTheme {
        
        Surface(color = MaterialTheme.colors.background) {
            HelperBox(message = "This is a test message.", type = HelperBoxType.Info)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Long")
@Composable
fun HelperBoxLongPreview() {
    MedianMeepleTheme {

        Surface(color = MaterialTheme.colors.background) {
            HelperBox(
                message = "This is a long test message. It should span more than one line.",
                type = HelperBoxType.Info
            )
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Missing")
@Composable
fun HelperBoxMissingPreview() {
    MedianMeepleTheme {

        Surface(color = MaterialTheme.colors.background) {
            HelperBox(message = "This is a test message.", type = HelperBoxType.Missing)
        }
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES, name = "Error")
@Composable
fun HelperBoxErrorPreview() {
    MedianMeepleTheme {

        Surface(color = MaterialTheme.colors.background) {
            HelperBox(message = "This is a test message.", type = HelperBoxType.Error)
        }
    }
}
