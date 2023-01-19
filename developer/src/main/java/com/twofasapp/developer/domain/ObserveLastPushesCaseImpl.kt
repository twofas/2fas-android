package com.twofasapp.developer.domain

import com.twofasapp.developer.domain.model.LastPush
import com.twofasapp.developer.domain.repository.DeveloperRepository
import kotlinx.coroutines.flow.Flow

internal class ObserveLastPushesCaseImpl(
    private val developerRepository: DeveloperRepository,
) : ObserveLastPushesCase {

    override operator fun invoke(): Flow<List<LastPush>> {
        return developerRepository.observeLastPushes()
    }
}