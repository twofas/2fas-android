package com.twofasapp.time.domain

interface RecalculateTimeDeltaCase {
    suspend operator fun invoke()
}