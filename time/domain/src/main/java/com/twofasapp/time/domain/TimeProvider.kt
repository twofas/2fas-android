package com.twofasapp.time.domain

import java.time.OffsetDateTime

interface TimeProvider {
    fun systemCurrentTime(): Long
    fun systemElapsedTime(): Long
    fun realCurrentTime(): Long
    fun realTimeDelta(): Long

    fun currentDateTimeUtc(): OffsetDateTime
}