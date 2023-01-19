package com.twofasapp.browserextension.domain.repository

import com.twofasapp.browserextension.data.BrowserExtensionLocalData
import com.twofasapp.browserextension.data.BrowserExtensionRemoteData
import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.browserextension.domain.model.TokenRequest
import com.twofasapp.extensions.ifNotBlank
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class BrowserExtensionRepositoryImpl(
    private val localData: BrowserExtensionLocalData,
    private val remoteData: BrowserExtensionRemoteData,
) : BrowserExtensionRepository {

    companion object {
        private const val PLATFORM = "android"
    }

    override fun observeMobileDevice(): Flow<MobileDevice> {
        return localData.observeMobileDevice()
    }

    override fun observePairedBrowsers(): Flow<List<PairedBrowser>> {
        return localData.observePairedBrowsers()
    }

    override suspend fun updateMobileDevice(mobileDevice: MobileDevice) {
        remoteData.updateMobileDevice(mobileDevice.id, mobileDevice.name)
        localData.saveMobileDevice(mobileDevice)
    }

    override suspend fun registerMobileDevice(deviceName: String, devicePublicKey: String, fcmToken: String): MobileDevice {
        val mobileDevice = remoteData.registerMobileDevice(
            deviceName = deviceName,
            devicePublicKey = devicePublicKey,
            fcmToken = fcmToken,
            platform = PLATFORM,
        )

        localData.saveMobileDevice(mobileDevice)
        return mobileDevice
    }

    override suspend fun pairBrowser(deviceId: String, extensionId: String, deviceName: String, devicePublicKey: String): PairedBrowser {
        val browser = remoteData.pairBrowser(
            deviceId = deviceId,
            extensionId = extensionId,
            deviceName = deviceName,
            devicePublicKey = devicePublicKey
        )
        localData.savePairedBrowser(browser)
        return browser
    }

    override suspend fun updatePairedBrowser(extensionId: String, newName: String) {
        remoteData.updatePairedBrowser(extensionId = extensionId, newName = newName)
        fetchPairedBrowsers()
    }

    override suspend fun fetchPairedBrowsers() {
        localData.observeMobileDevice().first().id.ifNotBlank { id ->
            localData.updatePairedBrowsers(remoteData.getBrowsers(id))
        }
    }

    override suspend fun fetchTokenRequests(deviceId: String): List<TokenRequest> {
        return remoteData.fetchTokenRequests(deviceId)
    }

    override suspend fun deletePairedBrowser(deviceId: String, extensionId: String) {
        remoteData.deletePairedBrowser(deviceId, extensionId)
        fetchPairedBrowsers()
    }

    override suspend fun acceptLoginRequest(deviceId: String, extensionId: String, requestId: String, code: String) {
        return remoteData.acceptLoginRequest(deviceId, extensionId, requestId, code)
    }

    override suspend fun denyLoginRequest(extensionId: String, requestId: String) {
        return remoteData.denyLoginRequest(extensionId, requestId)
    }
}