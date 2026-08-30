package com.twofasapp.data.browserext.local

import com.twofasapp.common.storage.DataStoreOwner
import com.twofasapp.common.storage.serializedPref
import com.twofasapp.data.browserext.domain.MobileDevice
import com.twofasapp.data.browserext.domain.PairedBrowser
import com.twofasapp.data.browserext.domain.TokenRequest
import com.twofasapp.data.browserext.local.model.MobileDeviceEntity
import com.twofasapp.data.browserext.local.model.PairedBrowserEntity
import com.twofasapp.data.browserext.mapper.asDomain
import com.twofasapp.data.browserext.mapper.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import java.time.Instant

internal class BrowserExtLocalSource(
    dataStoreOwner: DataStoreOwner,
    private val dao: PairedBrowserDao,
) : DataStoreOwner by dataStoreOwner {

    private val mobileDevice by serializedPref(
        name = "mobileDevice",
        default = MobileDeviceEntity(id = "", name = "", fcmToken = "", platform = "", publicKey = ""),
        serializer = MobileDeviceEntity.serializer(),
        encrypted = true,
    )

    private val tokenRequestsFlow: MutableStateFlow<List<TokenRequest>> = MutableStateFlow(emptyList())

    fun observeMobileDevice(): Flow<MobileDevice> {
        return mobileDevice.asFlow().map { it.asDomain() }
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
        this.mobileDevice.set(mobileDevice.asEntity())
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