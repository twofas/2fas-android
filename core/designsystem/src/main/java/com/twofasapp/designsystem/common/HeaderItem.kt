package com.twofasapp.designsystem.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HeaderItem(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD81F26),
            fontSize = 14.sp,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 64.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = 12.dp,
            )
    )
}