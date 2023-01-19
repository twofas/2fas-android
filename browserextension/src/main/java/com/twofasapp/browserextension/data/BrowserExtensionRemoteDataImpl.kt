package com.twofasapp.browserextension.data

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.browserextension.domain.model.TokenRequest
import com.twofasapp.network.api.BrowserExtensionApi
import com.twofasapp.network.body.ApproveLoginRequestBody
import com.twofasapp.network.body.DeviceRegisterBody
import com.twofasapp.network.body.PairBrowserBody
import java.time.Instant

internal class BrowserExtensionRemoteDataImpl(
    private val api: BrowserExtensionApi,
) : BrowserExtensionRemoteData {

    override suspend fun registerMobileDevice(deviceName: String, devicePublicKey: String, fcmToken: String, platform: String): MobileDevice {
        val response = api.registerMobileDevice(
            DeviceRegisterBody(
                name = deviceName,
                fcm_token = fcmToken,
                platform = platform,
            )
        )

        return MobileDevice(
            id = response.id,
            name = response.name,
            fcmToken = fcmToken,
            platform = response.platform,
            publicKey = devicePublicKey,
        )
    }

    override suspend fun updateMobileDevice(deviceId: String, newName: String) {
        api.updateMobileDevice(deviceId, newName)
    }

    override suspend fun pairBrowser(deviceId: String, extensionId: String, deviceName: String, devicePublicKey: String): PairedBrowser {
        val pairResponse = api.pairBrowser(
            deviceId = deviceId,
            body = PairBrowserBody(
                extension_id = extensionId,
                device_name = deviceName,
                device_public_key = devicePublicKey,
            )
        )

        val browserResponse = api.getBrowser(deviceId = deviceId, extensionId = extensionId)

        return PairedBrowser(
            id = browserResponse.id,
            name = browserResponse.name,
            pairedAt = Instant.parse(browserResponse.paired_at),
            extensionPublicKey = pairResponse.extension_public_key
        )
    }

    override suspend fun updatePairedBrowser(extensionId: String, newName: String) {
        api.updateBrowserName(extensionId, newName)
    }

    override suspend fun deletePairedBrowser(deviceId: String, extensionId: String) {
        api.deletePairedBrowser(deviceId, extensionId)
    }

    override suspend fun getBrowsers(deviceId: String): List<PairedBrowser> {
        return api.getBrowsers(deviceId).map {
            PairedBrowser(
                id = it.id,
                name = it.name,
                pairedAt = Instant.parse(it.paired_at),
                extensionPublicKey = "",
            )
        }
    }

    override suspend fun acceptLoginRequest(deviceId: String, extensionId: String, requestId: String, code: String) {
        api.acceptLoginRequest(
            deviceId = deviceId,
            body = ApproveLoginRequestBody(
                extension_id = extensionId,
                token_request_id = requestId,
                token = code,
            )
        )
    }

    override suspend fun denyLoginRequest(extensionId: String, requestId: String) {
        api.denyLoginRequest(
            extensionId = extensionId,
            tokenRequestId = requestId,
        )
    }

    override suspend fun fetchTokenRequests(deviceId: String): List<TokenRequest> {
        return api.fetchTokenRequests(deviceId)
            .filter { it.status.equals("pending", true) }
            .map {
                TokenRequest(
                    domain = it.domain,
                    requestId = it.token_request_id,
                    extensionId = it.extension_id,
                )
            }
    }
}