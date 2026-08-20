package com.twofasapp.android.navigation

import androidx.navigation.NamedNavArgument

sealed class LegacyScreen(val route: String) {

    fun routeWithArgs(vararg args: Pair<NamedNavArgument, Any?>): String {
        return route.replaceArgsInRoute(*args)
    }

    data object Startup : LegacyScreen("startup")
    data object Services : LegacyScreen("services")
    data object Settings : LegacyScreen("settings")
    data object EditService : LegacyScreen("services/{${NavArg.ServiceId.name}}")

    data object AppSettings : LegacyScreen("appsettings")
    data object About : LegacyScreen("about")
    data object Developer : LegacyScreen("developer")
    data object AboutLicenses : LegacyScreen("about/licenses")
    data object Notifications : LegacyScreen("notifications")
    data object Trash : LegacyScreen("trash")
    data object Dispose : LegacyScreen("dispose/{${NavArg.ServiceId.name}}")

    data object BrowserExt : LegacyScreen("browserext")
    data object BrowserExtPermission : LegacyScreen("browserext/permission")
    data object BrowserExtScan : LegacyScreen("browserext/scan")
    data object BrowserExtPairing : LegacyScreen("browserext/pairing?extensionId={${NavArg.ExtensionId.name}}")
    data object BrowserExtDetails : LegacyScreen("browserext/details?extensionId={${NavArg.ExtensionId.name}}")

    data object ExternalImportSelector : LegacyScreen("externalimport/selector")
    data object ExternalImport : LegacyScreen("externalimport?importType={${NavArg.ImportType.name}}")
    data object ExternalImportScan : LegacyScreen("externalimport/scan?importType={${NavArg.ImportType.name}}")
    data object ExternalImportResult :
        LegacyScreen("externalimport/result?importType={${NavArg.ImportType.name}}&importFileUri={${NavArg.ImportFileUri.name}}&importFileContent={${NavArg.ImportFileContent.name}}")

    data object Backup : LegacyScreen("backup?turnOnBackup={${NavArg.TurnOnBackup.name}}")
    data object BackupSettings : LegacyScreen("backup/settings")
    data object BackupExport : LegacyScreen("backup/export")
    data object BackupImport : LegacyScreen("backup/import?{${NavArg.ImportFileUri.name}}")

    data object Security : LegacyScreen("security")
    data object SetupPin : LegacyScreen("pin/setup")
    data object DisablePin : LegacyScreen("pin/disable")
    data object ChangePin : LegacyScreen("pin/change")

    data object Guides : LegacyScreen("guides")
    data object GuideInit : LegacyScreen("guides/init?guide={${NavArg.Guide.name}}")
    data object GuidePager : LegacyScreen("guides/pager?guide={${NavArg.Guide.name}}&variant={${NavArg.GuideVariantIndex.name}}")
}