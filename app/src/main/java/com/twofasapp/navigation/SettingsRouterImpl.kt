package com.twofasapp.navigation

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.browserextension.ui.browser.BrowserDetailsScreenFactory
import com.twofasapp.browserextension.ui.main.BrowserExtensionScreenFactory
import com.twofasapp.browserextension.ui.pairing.progress.PairingProgressScreenFactory
import com.twofasapp.browserextension.ui.pairing.scan.PairingScanScreenFactory
import com.twofasapp.settings.ui.main.SettingsMainScreenFactory
import com.twofasapp.settings.ui.theme.ThemeScreenFactory
import timber.log.Timber

class SettingsRouterImpl(
    private val settingsMainScreenFactory: SettingsMainScreenFactory,
    private val themeScreenFactory: ThemeScreenFactory,
    private val browserExtensionScreenFactory: BrowserExtensionScreenFactory,
    private val pairingProgressScreenFactory: PairingProgressScreenFactory,
    private val pairingScanScreenFactory: PairingScanScreenFactory,
    private val browserDetailsScreenFactory: BrowserDetailsScreenFactory,
) : SettingsRouter() {

    companion object {
        private const val ARG_EXTENSION_ID = "extensionId"

        private const val MAIN = "settings_main"
        private const val THEME = "theme"
        private const val BROWSER_EXTENSION = "browser_extension"
        private const val BROWSER_DETAILS = "browser_details/{$ARG_EXTENSION_ID}"
        private const val PAIRING_SCAN = "pairing_scan"
        private const val PAIRING_PROGRESS = "pairing_progress/{$ARG_EXTENSION_ID}"
    }

    override fun buildNavGraph(builder: NavGraphBuilder, viewModelStoreOwner: ViewModelStoreOwner?) {
        builder.composable(route = MAIN, content = { settingsMainScreenFactory.create() })
        builder.composable(route = THEME, content = { themeScreenFactory.create() })
        builder.composable(route = BROWSER_EXTENSION, content = { browserExtensionScreenFactory.create() })
        builder.composable(route = BROWSER_DETAILS, content = {
            browserDetailsScreenFactory.create(it.arguments?.getString(ARG_EXTENSION_ID).orEmpty())
        })
        builder.composable(route = PAIRING_SCAN, content = { pairingScanScreenFactory.create() })
        builder.composable(route = PAIRING_PROGRESS, content = {
            pairingProgressScreenFactory.create(it.arguments?.getString(ARG_EXTENSION_ID).orEmpty())
        })
    }

    override fun startDirection(): String = MAIN

    override fun navigate(
        navController: NavHostController,
        direction: SettingsDirections,
    ) {
        Timber.d("$direction")

        when (direction) {
            SettingsDirections.GoBack -> navController.popBackStack()
            SettingsDirections.Main -> navController.navigate(MAIN)
            SettingsDirections.Theme -> navController.navigate(THEME)
            SettingsDirections.BrowserExtension -> navController.navigate(BROWSER_EXTENSION)
            SettingsDirections.PairingScan -> navController.navigate(PAIRING_SCAN) { popUpTo(BROWSER_EXTENSION) }

            is SettingsDirections.PairingProgress -> navController.navigate(
                PAIRING_PROGRESS.replace("{$ARG_EXTENSION_ID}", direction.extensionId)
            ) { popUpTo(BROWSER_EXTENSION) }

            is SettingsDirections.BrowserDetails -> navController.navigate(
                BROWSER_DETAILS.replace("{$ARG_EXTENSION_ID}", direction.extensionId)
            )
        }
    }

    override fun navigateBack() {
        navigate(SettingsDirections.GoBack)
    }
}