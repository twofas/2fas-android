package com.twofasapp.developer.domain.repository

import com.twofasapp.developer.domain.model.LastPush
import com.twofasapp.developer.domain.model.LastScannedQr
import com.twofasapp.prefs.usecase.LastPushesPreference
import com.twofasapp.prefs.usecase.LastScannedQrPreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DeveloperRepositoryImpl(
    private val lastPushesPreference: LastPushesPreference,
    private val lastScannedQrPreference: LastScannedQrPreference,
) : DeveloperRepository {

    override fun observeLastPushes(): Flow<List<LastPush>> {
        return lastPushesPreference.flow()
            .map { entity ->
                entity.pushes.map {
                    LastPush(
                        timestamp = it.timestamp,
                        data = it.data,
                        notificationTitle = it.notificationTitle,
                        notificationBody = it.notificationBody,
                    )
                }
            }
    }

    override fun observeLastScannedQr(): Flow<LastScannedQr> {
        return lastScannedQrPreference.flow().map { LastScannedQr(it) }
    }
}