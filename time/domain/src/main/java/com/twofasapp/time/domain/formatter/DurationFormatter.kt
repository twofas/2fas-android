package com.twofasapp.time.domain.formatter

interface DurationFormatter {
    fun formatFromNow(): String
    fun format(millis: Long): String
}