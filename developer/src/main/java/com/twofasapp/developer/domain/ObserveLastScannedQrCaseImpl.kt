package com.twofasapp.developer.domain

import com.twofasapp.developer.domain.model.LastScannedQr
import com.twofasapp.developer.domain.repository.DeveloperRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveLastScannedQrCaseImpl(
    private val developerRepository: DeveloperRepository,
) : ObserveLastScannedQrCase {

    override operator fun invoke(): Flow<LastScannedQr> {
        return developerRepository.observeLastScannedQr()
    }
}