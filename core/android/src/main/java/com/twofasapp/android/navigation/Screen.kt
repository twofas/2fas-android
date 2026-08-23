package com.twofasapp.android.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {

    @Serializable
    data object Startup : Screen

    @Serializable
    data object Developer : Screen

    @Serializable
    data object Services : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data class EditService(val serviceId: Long) : Screen

    @Serializable
    data class Dispose(val serviceId: Long) : Screen

    @Serializable
    data object Guides : Screen

    @Serializable
    data class GuideInit(val guide: String) : Screen

    @Serializable
    data class GuidePager(val guide: String, val guideVariantIndex: Int) : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object Customization : Screen

    @Serializable
    data object BrowserExt : Screen

    @Serializable
    data object BrowserExtPermission : Screen

    @Serializable
    data object BrowserExtScan : Screen

    @Serializable
    data class BrowserExtPairing(val extensionId: String) : Screen

    @Serializable
    data class BrowserExtDetails(val extensionId: String) : Screen

    @Serializable
    data object ExternalImportSelector : Screen

    @Serializable
    data class ExternalImport(val importType: String) : Screen

    @Serializable
    data class ExternalImportScan(val importType: String) : Screen

    @Serializable
    data class ExternalImportResult(
        val importType: String,
        val importFileUri: String? = null,
        val importFileContent: String? = null,
    ) : Screen

    @Serializable
    data object Security : Screen

    @Serializable
    data object SetupPin : Screen

    @Serializable
    data object DisablePin : Screen

    @Serializable
    data object ChangePin : Screen

    @Serializable
    data object Trash : Screen

    @Serializable
    data object Backup : Screen

    @Serializable
    data object BackupSettings : Screen

    @Serializable
    data object BackupExport : Screen

    @Serializable
    data class BackupImport(val importFileUri: String? = null) : Screen

    @Serializable
    data object About : Screen

    @Serializable
    data object AboutLicenses : Screen
}