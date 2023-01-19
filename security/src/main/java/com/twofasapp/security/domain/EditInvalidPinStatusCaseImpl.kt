package com.twofasapp.security.domain

import com.twofasapp.security.data.SecurityRepository
import com.twofasapp.security.domain.model.InvalidPinStatus
import com.twofasapp.time.domain.TimeProvider
import kotlinx.coroutines.flow.first

internal class EditInvalidPinStatusCaseImpl(
    private val securityRepository: SecurityRepository,
    private val timeProvider: TimeProvider,
) : EditInvalidPinStatusCase {

    override suspend fun incrementAttempt() {
        with(securityRepository.observeInvalidPinStatus().first()) {
            securityRepository.editInvalidPinStatus(
                copy(
                    attempts = attempts + 1,
                    lastAttemptSinceBootMs = timeProvider.systemElapsedTime()
                )
            )
        }
    }

    override suspend fun reset() {
        securityRepository.editInvalidPinStatus(InvalidPinStatus.Default)
    }
}
