package com.twofasapp.common.time

interface TimeProvider {
    fun systemCurrentTime(): Long
    fun systemElapsedTime(): Long
    fun realCurrentTime(): Long
    fun realTimeDelta(): Long
}