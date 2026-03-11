package com.twofasapp.designsystem.common

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun richText(text: String) = buildAnnotatedString {
    var currentIndex = 0
    val regex = Regex("\\*\\*(.*?)\\*\\*")

    regex.findAll(text).forEach { match ->
        val start = match.range.first
        val end = match.range.last + 1
        val boldText = match.groupValues[1]

        if (currentIndex < start) {
            append(text.substring(currentIndex, start))
        }

        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(boldText)
        }

        currentIndex = end
    }

    if (currentIndex < text.length) {
        append(text.substring(currentIndex))
    }
}