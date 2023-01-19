package com.twofasapp.network.api

import com.twofasapp.network.body.ApproveLoginRequestBody
import com.twofasapp.network.body.DenyLoginRequestBody
import com.twofasapp.network.body.DeviceRegisterBody
import com.twofasapp.network.body.PairBrowserBody
import com.twofasapp.network.exception.BrowserAlreadyPairedException
import com.twofasapp.network.response.BrowserResponse
import com.twofasapp.network.response.DeviceRegisterResponse
import com.twofasapp.network.response.PairBrowserResponse
import com.twofasapp.network.response.TokenRequestResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class BrowserExtensionApi(
    private val client: HttpClient,
) {

    companion object {
        private const val baseUrl = "https://api2.2fas.com"
    }

    suspend fun registerMobileDevice(body: DeviceRegisterBody): DeviceRegisterResponse {
        return client.post("$baseUrl/mobile/devices") { setBody(body) }.body()
    }

    suspend fun updateMobileDevice(deviceId: String, newName: String) {
        client.put("$baseUrl/mobile/devices/$deviceId") {
            setBody(buildJsonObject { put("name", JsonPrimitive(newName)) })
        }.body<Unit>()
    }

    suspend fun pairBrowser(deviceId: String, body: PairBrowserBody): PairBrowserResponse {
        return try {
            client.post("$baseUrl/mobile/devices/$deviceId/browser_extensions") { setBody(body) }.body()
        } catch (e: Exception) {
            throw when {
                e is ClientRequestException && e.response.status.value == 409 -> BrowserAlreadyPairedException()
                else -> e
            }
        }
    }

    suspend fun updateBrowserName(extensionId: String, newName: String) {
        client.put("$baseUrl/browser_extensions/$extensionId") {
            setBody(buildJsonObject { put("name", JsonPrimitive(newName)) })
        }.body<Unit>()
    }

    suspend fun deletePairedBrowser(deviceId: String, extensionId: String) {
        return client.delete("$baseUrl/mobile/devices/$deviceId/browser_extensions/${extensionId}").body()
    }

    suspend fun getBrowser(deviceId: String, extensionId: String): BrowserResponse {
        return client.get("$baseUrl/mobile/devices/$deviceId/browser_extensions/${extensionId}").body()
    }

    suspend fun getBrowsers(deviceId: String): List<BrowserResponse> {
        return client.get("$baseUrl/mobile/devices/$deviceId/browser_extensions").body()
    }

    suspend fun acceptLoginRequest(deviceId: String, body: ApproveLoginRequestBody) {
        client.post("$baseUrl/mobile/devices/$deviceId/commands/send_2fa_token") { setBody(body) }.body<Unit>()
    }

    suspend fun denyLoginRequest(extensionId: String, tokenRequestId: String) {
        client.post("$baseUrl/browser_extensions/$extensionId/2fa_requests/$tokenRequestId/commands/close_2fa_request") { setBody(DenyLoginRequestBody()) }
            .body<Unit>()
    }

    suspend fun fetchTokenRequests(deviceId: String): List<TokenRequestResponse> {
        return client.get("$baseUrl/mobile/devices/$deviceId/browser_extensions/2fa_requests").body()
    }
}
