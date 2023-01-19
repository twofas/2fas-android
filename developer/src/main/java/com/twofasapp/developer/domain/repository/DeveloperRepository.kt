package com.twofasapp.developer.domain.repository

import com.twofasapp.developer.domain.model.LastPush
import com.twofasapp.developer.domain.model.LastScannedQr
import kotlinx.coroutines.flow.Flow

internal interface DeveloperRepository {
    fun observeLastPushes(): Flow<List<LastPush>>
    fun observeLastScannedQr(): Flow<LastScannedQr>
}