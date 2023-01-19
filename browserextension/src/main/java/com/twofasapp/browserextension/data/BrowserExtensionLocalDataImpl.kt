package com.twofasapp.browserextension.data

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import com.twofasapp.persistence.dao.PairedBrowserDao
import com.twofasapp.persistence.model.PairedBrowserEntity
import com.twofasapp.prefs.model.MobileDeviceEntity
import com.twofasapp.prefs.usecase.MobileDevicePreference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

internal class BrowserExtensionLocalDataImpl(
    private val mobileDevicePreference: MobileDevicePreference,
    private val dao: PairedBrowserDao,
) : BrowserExtensionLocalData {

    override fun observeMobileDevice(): Flow<MobileDevice> {
        return mobileDevicePreference.flow().map { it.toDomain() }
    }

    override fun observePairedBrowsers(): Flow<List<PairedBrowser>> {
        return dao.observe()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveMobileDevice(mobileDevice: MobileDevice) {
        mobileDevicePreference.put(mobileDevice.toEntity())
    }

    override suspend fun savePairedBrowser(pairedBrowser: PairedBrowser) {
        dao.insertOrUpdate(pairedBrowser.toEntity())
    }

    override suspend fun updatePairedBrowsers(pairedBrowsers: List<PairedBrowser>) {
        dao.updateAll(pairedBrowsers.map { it.toEntity() })
    }

    private fun MobileDevice.toEntity() =
        MobileDeviceEntity(
            id = id,
            name = name,
            fcmToken = fcmToken,
            platform = platform,
            publicKey = publicKey,
        )

    private fun MobileDeviceEntity.toDomain() =
        MobileDevice(
            id = id,
            name = name,
            fcmToken = fcmToken,
            platform = platform,
            publicKey = publicKey,
        )

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