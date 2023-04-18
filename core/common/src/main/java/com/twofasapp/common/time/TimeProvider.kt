package com.twofasapp.common.time

import java.time.OffsetDateTime

interface TimeProvider {
    fun currentDateTimeUtc(): OffsetDateTime
    fun systemCurrentTime(): Long
    fun systemElapsedTime(): Long
}