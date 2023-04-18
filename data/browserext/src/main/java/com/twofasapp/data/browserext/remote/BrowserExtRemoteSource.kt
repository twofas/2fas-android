package com.twofasapp.data.browserext.remote

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.remote.exception.BrowserAlreadyPairedException
import com.twofasapp.data.browserext.remote.model.ApproveLoginRequestBody
import com.twofasapp.data.browserext.remote.model.BrowserJson
import com.twofasapp.data.browserext.remote.model.DenyLoginRequestBody
import com.twofasapp.data.browserext.remote.model.PairBrowserBody
import com.twofasapp.data.browserext.remote.model.PairBrowserJson
import com.twofasapp.data.browserext.remote.model.RegisterDeviceBody
import com.twofasapp.data.browserext.remote.model.RegisterDeviceJson
import com.twofasapp.data.browserext.remote.model.TokenRequestJson
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import java.time.Instant

internal class BrowserExtRemoteSource(
    private val client: HttpClient,
) {

    suspend fun registerMobileDevice(devicePublicKey: String, body: RegisterDeviceBody): MobileDevice {
        val response: RegisterDeviceJson = client.post("/mobile/devices") { setBody(body) }.body()

        return MobileDevice(
            id = response.id,
            name = response.name,
            fcmToken = body.fcm_token,
            platform = response.platform,
            publicKey = devicePublicKey,
        )
    }

    suspend fun updateMobileDevice(deviceId: String, newName: String) {
        client.put("/mobile/devices/$deviceId") {
            setBody(buildJsonObject { put("name", JsonPrimitive(newName)) })
        }.body<Unit>()
    }

    suspend fun pairBrowser(deviceId: String, body: PairBrowserBody): PairedBrowser {
        return try {
            val pairResponse: PairBrowserJson = client.post("/mobile/devices/$deviceId/browser_extensions") { setBody(body) }.body()

            val browserResponse = getBrowser(deviceId = deviceId, extensionId = body.extension_id)

            PairedBrowser(
                id = browserResponse.id,
                name = browserResponse.name,
                pairedAt = Instant.parse(browserResponse.paired_at),
                extensionPublicKey = pairResponse.extension_public_key
            )
        } catch (e: Exception) {
            throw when {
                e is ClientRequestException && e.response.status.value == 409 -> BrowserAlreadyPairedException()
                else -> e
            }
        }
    }

    suspend fun updateBrowserName(extensionId: String, newName: String) {
        client.put("/browser_extensions/$extensionId") {
            setBody(buildJsonObject { put("name", JsonPrimitive(newName)) })
        }.body<Unit>()
    }

    suspend fun deletePairedBrowser(deviceId: String, extensionId: String) {
        return client.delete("/mobile/devices/$deviceId/browser_extensions/${extensionId}").body()
    }

    suspend fun getBrowser(deviceId: String, extensionId: String): BrowserJson {
        return client.get("/mobile/devices/$deviceId/browser_extensions/${extensionId}").body()
    }

    suspend fun getBrowsers(deviceId: String): List<BrowserJson> {
        return client.get("/mobile/devices/$deviceId/browser_extensions").body()
    }

    suspend fun acceptLoginRequest(deviceId: String, body: ApproveLoginRequestBody) {
        client.post("/mobile/devices/$deviceId/commands/send_2fa_token") { setBody(body) }.body<Unit>()
    }

    suspend fun denyLoginRequest(extensionId: String, tokenRequestId: String) {
        client.post("/browser_extensions/$extensionId/2fa_requests/$tokenRequestId/commands/close_2fa_request") { setBody(DenyLoginRequestBody()) }
            .body<Unit>()
    }

    suspend fun fetchTokenRequests(deviceId: String): List<TokenRequestJson> {
        return client.get("/mobile/devices/$deviceId/browser_extensions/2fa_requests").body<List<TokenRequestJson>>()
            .filter { it.status.equals("pending", true) }
    }

}