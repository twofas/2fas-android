package com.twofasapp.data.browserext

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.data.browserext.local.BrowserExtLocalSource
import com.twofasapp.data.browserext.mapper.asDomain
import com.twofasapp.data.browserext.remote.BrowserExtRemoteSource
import com.twofasapp.data.browserext.remote.model.ApproveLoginRequestBody
import com.twofasapp.data.browserext.remote.model.PairBrowserBody
import com.twofasapp.data.browserext.remote.model.RegisterDeviceBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class BrowserExtRepositoryImpl(
    private val localSource: BrowserExtLocalSource,
    private val remoteSource: BrowserExtRemoteSource,
) : BrowserExtRepository {

    companion object {
        private const val PLATFORM = "android"
    }

    override fun observeMobileDevice(): Flow<MobileDevice> {
        return localSource.observeMobileDevice()
    }

    override fun observePairedBrowsers(): Flow<List<PairedBrowser>> {
        return localSource.observePairedBrowsers()
    }

    override suspend fun updateMobileDevice(mobileDevice: MobileDevice) {
        remoteSource.updateMobileDevice(mobileDevice.id, mobileDevice.name)
        localSource.saveMobileDevice(mobileDevice)
    }

    override suspend fun registerMobileDevice(deviceName: String, devicePublicKey: String, fcmToken: String): MobileDevice {
        val mobileDevice = remoteSource.registerMobileDevice(
            devicePublicKey = devicePublicKey,
            body = RegisterDeviceBody(
                name = deviceName,
                fcm_token = fcmToken,
                platform = PLATFORM,
            )
        )

        localSource.saveMobileDevice(mobileDevice)
        return mobileDevice
    }

    override suspend fun pairBrowser(deviceId: String, extensionId: String, deviceName: String, devicePublicKey: String): PairedBrowser {
        val browser = remoteSource.pairBrowser(
            deviceId = deviceId,
            body = PairBrowserBody(
                extension_id = extensionId,
                device_name = deviceName,
                device_public_key = devicePublicKey,
            )
        )
        localSource.savePairedBrowser(browser)
        return browser
    }

    override suspend fun updatePairedBrowser(extensionId: String, newName: String) {
        remoteSource.updateBrowserName(extensionId = extensionId, newName = newName)
        fetchPairedBrowsers()
    }

    override suspend fun fetchPairedBrowsers() {
        val id = localSource.observeMobileDevice().first().id

        if (id.isNotBlank()) {
            localSource.updatePairedBrowsers(remoteSource.getBrowsers(id).map { it.asDomain() })
        }
    }

    override suspend fun fetchTokenRequests(deviceId: String): List<TokenRequest> {
        return remoteSource.fetchTokenRequests(deviceId).map { it.asDomain() }
    }

    override suspend fun deletePairedBrowser(deviceId: String, extensionId: String) {
        remoteSource.deletePairedBrowser(deviceId, extensionId)
        fetchPairedBrowsers()
    }

    override suspend fun acceptLoginRequest(deviceId: String, extensionId: String, requestId: String, code: String) {
        return remoteSource.acceptLoginRequest(
            deviceId,
            body = ApproveLoginRequestBody(
                extension_id = extensionId,
                token_request_id = requestId,
                token = code,
            )
        )
    }

    override suspend fun denyLoginRequest(extensionId: String, requestId: String) {
        return remoteSource.denyLoginRequest(extensionId, requestId)
    }
}