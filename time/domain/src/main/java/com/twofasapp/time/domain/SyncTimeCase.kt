package com.twofasapp.time.domain

interface SyncTimeCase {
    suspend operator fun invoke()
}