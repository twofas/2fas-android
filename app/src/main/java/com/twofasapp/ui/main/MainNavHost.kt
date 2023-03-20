package com.twofasapp.ui.main

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.twofasapp.common.ktx.clearGraphBackStack
import com.twofasapp.common.navigation.withArg
import com.twofasapp.extensions.startActivity
import com.twofasapp.feature.about.navigation.AboutGraph
import com.twofasapp.feature.about.navigation.aboutNavigation
import com.twofasapp.feature.appsettings.navigation.AppSettingsGraph
import com.twofasapp.feature.appsettings.navigation.appSettingsNavigation
import com.twofasapp.feature.browserext.notification.BrowserExtGraph
import com.twofasapp.feature.browserext.notification.browserExtNavigation
import com.twofasapp.feature.externalimport.navigation.ExternalImportGraph
import com.twofasapp.feature.externalimport.navigation.externalImportNavigation
import com.twofasapp.feature.home.navigation.HomeGraph
import com.twofasapp.feature.home.navigation.HomeNavigationListener
import com.twofasapp.feature.home.navigation.homeNavigation
import com.twofasapp.feature.startup.navigation.startupNavigation
import com.twofasapp.feature.trash.navigation.TrashGraph
import com.twofasapp.feature.trash.navigation.trashNavigation
import com.twofasapp.features.addserviceqr.AddServiceQrActivity
import com.twofasapp.features.backup.BackupActivity
import com.twofasapp.security.navigation.SecurityGraph
import com.twofasapp.security.navigation.securityNavigation
import com.twofasapp.services.navigation.ServiceGraph
import com.twofasapp.services.navigation.ServiceNavArg
import com.twofasapp.services.navigation.serviceNavigation

@Composable
internal fun MainNavHost(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(navController = navController, startDestination = startDestination) {

        startupNavigation(
            openHome = { navController.navigate(HomeGraph.route) { popUpTo(0) } }
        )

        homeNavigation(
            navController = navController,
            listener = object : HomeNavigationListener {
                override fun openAddManuallyService(activity: Activity) {
                    navController.navigate(ServiceGraph.route.withArg(ServiceNavArg.ServiceId, 0L))
                }

                override fun openAddQrService(activity: Activity) {
                    activity.startActivity<AddServiceQrActivity>()
                }

                override fun openService(activity: Activity, serviceId: Long) {
                    navController.navigate(ServiceGraph.route.withArg(ServiceNavArg.ServiceId, serviceId))
                }

                override fun openExternalImport() {
                    navController.navigate(ExternalImportGraph.route)
                }

                override fun openBrowserExt() {
                    navController.navigate(BrowserExtGraph.route)
                }

                override fun openSecurity(activity: Activity) {
                    navController.navigate(SecurityGraph.route)
                }

                override fun openBackup(activity: Activity) {
                    activity.startActivity<BackupActivity>()
                }

                override fun openAppSettings() {
                    navController.navigate(AppSettingsGraph.route)
                }

                override fun openTrash() {
                    navController.navigate(TrashGraph.route)
                }

                override fun openAbout() {
                    navController.navigate(AboutGraph.route)
                }
            }
        )

        externalImportNavigation(
            navController = navController,
            onFinish = { navController.clearGraphBackStack() }
        )

        appSettingsNavigation()
        serviceNavigation(navController = navController)
        trashNavigation(navController = navController)
        aboutNavigation(navController = navController)
        browserExtNavigation(navController = navController)
        securityNavigation(navController = navController)
    }
}