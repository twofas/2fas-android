package com.twofasapp.browserextension.data

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.browserextension.domain.model.TokenRequest

internal interface BrowserExtensionRemoteData {
    suspend fun registerMobileDevice(
        deviceName: String,
        devicePublicKey: String,
        fcmToken: String,
        platform: String,
    ): MobileDevice

    suspend fun updateMobileDevice(
        deviceId: String,
        newName: String
    )

    suspend fun pairBrowser(
        deviceId: String,
        extensionId: String,
        deviceName: String,
        devicePublicKey: String,
    ): PairedBrowser

    suspend fun updatePairedBrowser(
        extensionId: String,
        newName: String
    )

    suspend fun deletePairedBrowser(
        deviceId: String,
        extensionId: String
    )

    suspend fun getBrowsers(
        deviceId: String,
    ): List<PairedBrowser>

    suspend fun acceptLoginRequest(
        deviceId: String,
        extensionId: String,
        requestId: String,
        code: String
    )

    suspend fun denyLoginRequest(
        extensionId: String,
        requestId: String,
    )

    suspend fun fetchTokenRequests(
        extensionId: String,
    ): List<TokenRequest>
}