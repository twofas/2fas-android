package com.twofasapp.time.domain.formatter

import java.time.format.DateTimeFormatter

object TimeFormatter {
    val fullDateTime: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val fullDate: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
}