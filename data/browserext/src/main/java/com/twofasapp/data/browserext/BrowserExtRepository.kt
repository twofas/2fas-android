package com.twofasapp.data.browserext

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.domain.TokenRequest
import kotlinx.coroutines.flow.Flow

interface BrowserExtRepository {
    fun observeMobileDevice(): Flow<MobileDevice>
    fun observePairedBrowsers(): Flow<List<PairedBrowser>>
    fun observeTokenRequests(): Flow<List<TokenRequest>>
    suspend fun updateMobileDevice(mobileDevice: MobileDevice)
    suspend fun registerMobileDevice(deviceName: String, devicePublicKey: String, fcmToken: String): MobileDevice
    suspend fun pairBrowser(deviceId: String, extensionId: String, deviceName: String, devicePublicKey: String): PairedBrowser
    suspend fun updatePairedBrowser(extensionId: String, newName: String)
    suspend fun deletePairedBrowser(deviceId: String, extensionId: String)
    suspend fun fetchPairedBrowsers()
    suspend fun fetchTokenRequests()
    suspend fun deleteTokenRequest(requestId: String)
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