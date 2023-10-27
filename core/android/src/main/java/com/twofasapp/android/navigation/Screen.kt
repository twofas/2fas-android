package com.twofasapp.android.navigation

import androidx.navigation.NamedNavArgument

sealed class Screen(val route: String) {

    fun routeWithArgs(vararg args: Pair<NamedNavArgument, Any?>): String {
        return route.replaceArgsInRoute(*args)
    }

    data object Startup : Screen("startup")
    data object Services : Screen("services")
    data object Settings : Screen("settings")
    data object EditService : Screen("services/{${NavArg.ServiceId.name}}")

    data object AppSettings : Screen("appsettings")
    data object About : Screen("about")
    data object AboutLicenses : Screen("about/licenses")
    data object Notifications : Screen("notifications")
    data object Trash : Screen("trash")
    data object Dispose : Screen("dispose/{${NavArg.ServiceId.name}}")

    data object BrowserExt : Screen("browserext")
    data object BrowserExtPermission : Screen("browserext/permission")
    data object BrowserExtScan : Screen("browserext/scan")
    data object BrowserExtPairing : Screen("browserext/pairing?extensionId={${NavArg.ExtensionId.name}}")
    data object BrowserExtDetails : Screen("browserext/details?extensionId={${NavArg.ExtensionId.name}}")

    data object ExternalImportSelector : Screen("externalimport/selector")
    data object ExternalImport : Screen("externalimport?importType={${NavArg.ImportType.name}}")
    data object ExternalImportScan : Screen("externalimport/scan?importType={${NavArg.ImportType.name}}")
    data object ExternalImportResult :
        Screen("externalimport/result?importType={${NavArg.ImportType.name}}&importFileUri={${NavArg.ImportFileUri.name}}&importFileContent={${NavArg.ImportFileContent.name}}")

    data object Backup : Screen("backup?turnOnBackup={${NavArg.TurnOnBackup.name}}")
    data object BackupSettings : Screen("backup/settings")
    data object BackupExport : Screen("backup/export")
    data object BackupImport : Screen("backup/import?{${NavArg.ImportFileUri.name}}")

    data object Security : Screen("security")
    data object SetupPin : Screen("pin/setup")
    data object DisablePin : Screen("pin/disable")
    data object ChangePin : Screen("pin/change")

    data object Guides : Screen("guides")
    data object GuideInit : Screen("guides/init?guide={${NavArg.Guide.name}}")
    data object GuidePager : Screen("guides/pager?guide={${NavArg.Guide.name}}&variant={${NavArg.GuideVariantIndex.name}}")
}