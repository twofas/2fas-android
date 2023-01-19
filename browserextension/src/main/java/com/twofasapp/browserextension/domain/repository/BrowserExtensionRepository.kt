package com.twofasapp.browserextension.domain.repository

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.browserextension.domain.model.TokenRequest
import kotlinx.coroutines.flow.Flow

internal interface BrowserExtensionRepository {

    fun observeMobileDevice(): Flow<MobileDevice>

    fun observePairedBrowsers(): Flow<List<PairedBrowser>>

    suspend fun updateMobileDevice(mobileDevice: MobileDevice)

    suspend fun registerMobileDevice(deviceName: String, devicePublicKey: String, fcmToken: String): MobileDevice

    suspend fun pairBrowser(deviceId: String, extensionId: String, deviceName: String, devicePublicKey: String): PairedBrowser

    suspend fun updatePairedBrowser(extensionId: String, newName: String)

    suspend fun deletePairedBrowser(deviceId: String, extensionId: String)

    suspend fun fetchPairedBrowsers()

    suspend fun fetchTokenRequests(deviceId: String): List<TokenRequest>

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
}