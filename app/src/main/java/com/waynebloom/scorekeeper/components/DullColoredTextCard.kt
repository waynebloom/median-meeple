package com.waynebloom.scorekeeper.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.waynebloom.scorekeeper.ui.theme.rust300

@Composable
fun DullColoredTextCard(
    text: String = "",
    color: Color = MaterialTheme.colors.primary,
    content: @Composable (Color, String) -> Unit = { mColor, mText ->
        Text(
            text = mText,
            color = mColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
) {
    val lowEmphasisColor = color.copy(alpha = 0.75f)
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .border(
                width = 1.dp,
                color = lowEmphasisColor,
                shape = MaterialTheme.shapes.small
            )
            .fillMaxWidth()
    ) {
        content(lowEmphasisColor, text)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF333333)
@Composable
fun DullColoredTextCardPreview() {
    DullColoredTextCard(
        text = "No content was found.",
        color = rust300
    ) { color, text ->
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}