package com.twofasapp.data.session.local

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.longPref
import com.twofasapp.common.storage.longPrefNullable
import kotlinx.coroutines.flow.Flow
import java.time.Instant

internal class SessionLocalSource(
    dataStoreOwner: DataStoreOwner,
) : DataStoreOwner by dataStoreOwner {

    private val appInstallTimestamp by longPrefNullable(
        name = "appInstallTimestamp",
    )

    private val noCompanionAppFromTimestamp by longPrefNullable(
        name = "noCompanionAppFromTimestamp",
    )

    private val passBannerDismissTimestamp by longPref(
        name = "passBannerDismissTimestamp",
        default = 0L,
    )

    private val appReviewPromptedTimestamp by longPref(
        name = "appReviewPromptedTimestamp",
        default = 0L,
    )

    suspend fun getAppInstallTimestamp(): Long {
        return appInstallTimestamp.get() ?: Instant.now().toEpochMilli()
    }

    suspend fun markAppInstalled() {
        if (appInstallTimestamp.get() == null) {
            appInstallTimestamp.set(Instant.now().toEpochMilli())
        }
    }

    suspend fun getNoCompanionAppFromTimestamp(): Long? {
        return noCompanionAppFromTimestamp.get()
    }

    suspend fun setNoCompanionAppFromTimestamp(millis: Long?) {
        noCompanionAppFromTimestamp.set(millis)
    }

    fun observePassBannerDismissTimestamp(): Flow<Long> {
        return passBannerDismissTimestamp.asFlow()
    }

    suspend fun setPassBannerDismissTimestamp(millis: Long) {
        passBannerDismissTimestamp.set(millis)
    }

    fun observeAppReviewPromptedTimestamp(): Flow<Long> {
        return appReviewPromptedTimestamp.asFlow()
    }

    suspend fun setAppReviewPromptedTimestamp(millis: Long) {
        appReviewPromptedTimestamp.set(millis)
    }
}