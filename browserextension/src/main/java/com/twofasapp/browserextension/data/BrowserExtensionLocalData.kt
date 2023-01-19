package com.twofasapp.browserextension.data

import com.twofasapp.browserextension.domain.model.MobileDevice
import com.twofasapp.browserextension.domain.model.PairedBrowser
import kotlinx.coroutines.flow.Flow

internal interface BrowserExtensionLocalData {
    fun observeMobileDevice(): Flow<MobileDevice>
    fun observePairedBrowsers(): Flow<List<PairedBrowser>>
    suspend fun saveMobileDevice(mobileDevice: MobileDevice)
    suspend fun savePairedBrowser(pairedBrowser: PairedBrowser)
    suspend fun updatePairedBrowsers(pairedBrowsers: List<PairedBrowser>)
}