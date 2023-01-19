package com.twofasapp.security.domain

interface EditInvalidPinStatusCase {
    suspend fun incrementAttempt()
    suspend fun reset()
}