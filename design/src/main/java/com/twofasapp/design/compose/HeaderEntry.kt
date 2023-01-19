package com.twofasapp.design.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderEntry(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD81F26),
            fontSize = 14.sp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 72.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 12.dp,
            )
    )
}

@Preview
@Composable
internal fun HeaderEntryItemPreview() {
    HeaderEntry(text = "Test")
}