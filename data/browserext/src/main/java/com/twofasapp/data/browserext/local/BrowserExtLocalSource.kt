package com.twofasapp.data.browserext.local

import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.data.browserext.local.model.MobileDeviceEntity
import com.twofasapp.data.browserext.local.model.PairedBrowserEntity
import com.twofasapp.data.browserext.mapper.asDomain
import com.twofasapp.data.browserext.mapper.asEntity
import com.twofasapp.storage.PlainPreferences
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

internal class BrowserExtLocalSource(
    private val json: Json,
    private val dao: PairedBrowserDao,
    private val preferences: PlainPreferences,
) {
    companion object {
        private const val KeyMobileDevice = "mobileDevice"
    }

    private val mobileDeviceFlow: MutableSharedFlow<MobileDevice> = MutableSharedFlow(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    private val tokenRequestsFlow: MutableStateFlow<List<TokenRequest>> = MutableStateFlow(emptyList())

    fun observeMobileDevice(): Flow<MobileDevice> {
        return mobileDeviceFlow.onSubscription {
            val device = preferences.getString(KeyMobileDevice)?.let {
                json.decodeFromString<MobileDeviceEntity>(it)
            } ?: MobileDeviceEntity(id = "", name = "", fcmToken = "", platform = "", publicKey = "")

            emit(device.asDomain())
        }
    }

    fun observePairedBrowsers(): Flow<List<PairedBrowser>> {
        return dao.observe()
            .map { list -> list.map { it.toDomain() } }
    }

    fun observeTokenRequests(): Flow<List<TokenRequest>> {
        return tokenRequestsFlow
    }

    suspend fun updateTokenRequests(requests: List<TokenRequest>) {
        tokenRequestsFlow.emit(requests)
    }

    suspend fun deleteTokenRequest(requestId: String) {
        tokenRequestsFlow.emit(tokenRequestsFlow.value.filterNot { it.requestId == requestId })
    }

    suspend fun saveMobileDevice(mobileDevice: MobileDevice) {
        preferences.putString(KeyMobileDevice, json.encodeToString(mobileDevice.asEntity()))
        mobileDeviceFlow.emit(mobileDevice)
    }

    suspend fun savePairedBrowser(pairedBrowser: PairedBrowser) {
        dao.insertOrUpdate(pairedBrowser.toEntity())
    }

    suspend fun updatePairedBrowsers(pairedBrowsers: List<PairedBrowser>) {
        dao.updateAll(pairedBrowsers.map { it.toEntity() })
    }

    private fun PairedBrowser.toEntity() =
        PairedBrowserEntity(
            id = id,
            name = name,
            extensionPublicKey = extensionPublicKey,
            pairedAt = pairedAt.toEpochMilli(),
        )

    private fun PairedBrowserEntity.toDomain() =
        PairedBrowser(
            id = id,
            name = name,
            pairedAt = Instant.ofEpochMilli(pairedAt),
            extensionPublicKey = extensionPublicKey,
        )
}