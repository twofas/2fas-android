package com.twofasapp.navigation

import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.twofasapp.externalimport.ui.aegis.AegisScreenFactory
import com.twofasapp.externalimport.ui.googleauthenticator.GoogleAuthenticatorScreenFactory
import com.twofasapp.externalimport.ui.main.ExternalImportScreenFactory
import com.twofasapp.externalimport.ui.raivo.RaivoScreenFactory
import com.twofasapp.externalimport.ui.result.ImportResultScreenFactory
import com.twofasapp.externalimport.ui.scan.ImportScanScreenFactory
import timber.log.Timber

class ExternalImportRouterImpl(
    private val externalImportScreenFactory: ExternalImportScreenFactory,
    private val importScanScreenFactory: ImportScanScreenFactory,
    private val importResultScreenFactory: ImportResultScreenFactory,
    private val googleAuthenticatorScreenFactory: GoogleAuthenticatorScreenFactory,
    private val aegisScreenFactory: AegisScreenFactory,
    private val raivoScreenFactory: RaivoScreenFactory,
) : ExternalImportRouter() {

    companion object {
        private const val ARG_CONTENT = "ARG_CONTENT"
        private const val ARG_TYPE = "ARG_TYPE"
        private const val ARG_START_GALLERY = "ARG_START_GALLERY"

        private const val MAIN = "external_import"
        private const val IMPORT_SCAN = "import_scan/{$ARG_START_GALLERY}"
        private const val IMPORT_RESULT = "import_result/{$ARG_TYPE}/{$ARG_CONTENT}"
        private const val GOOGLE_AUTH = "import_google_authenticator"
        private const val AEGIS = "import_aegis"
        private const val RAIVO = "import_raivo"
    }

    override fun buildNavGraph(builder: NavGraphBuilder, viewModelStoreOwner: ViewModelStoreOwner?) {
        builder.composable(route = MAIN, content = { externalImportScreenFactory.create() })
        builder.composable(
            route = IMPORT_SCAN,
            content = { importScanScreenFactory.create(it.arguments?.getString(ARG_START_GALLERY).toBoolean()) })
        builder.composable(
            route = IMPORT_RESULT,
            content = {
                importResultScreenFactory.create(
                    type = it.arguments?.getString(ARG_TYPE).orEmpty(),
                    content = it.arguments?.getString(ARG_CONTENT).orEmpty()
                )
            }
        )
        builder.composable(route = GOOGLE_AUTH, content = { googleAuthenticatorScreenFactory.create() })
        builder.composable(route = AEGIS, content = { aegisScreenFactory.create() })
        builder.composable(route = RAIVO, content = { raivoScreenFactory.create() })
    }

    override fun navigate(navController: NavHostController, direction: ExternalImportDirections) {
        Timber.d("$direction")

        when (direction) {
            ExternalImportDirections.GoBack -> navController.popBackStack()
            ExternalImportDirections.Main -> navController.navigate(MAIN)
            is ExternalImportDirections.ImportScan -> {
                navController.navigate(
                    IMPORT_SCAN.replace("{${ARG_START_GALLERY}}", direction.startWithGallery.toString())
                )
            }
            is ExternalImportDirections.ImportResult -> {
                navController.navigate(
                    IMPORT_RESULT
                        .replace("{${ARG_TYPE}}", direction.type.name)
                        .replace("{${ARG_CONTENT}}", direction.content)
                ) { popUpTo(MAIN) }
            }
            ExternalImportDirections.GoogleAuthenticator -> navController.navigate(GOOGLE_AUTH)
            ExternalImportDirections.Aegis -> navController.navigate(AEGIS)
            ExternalImportDirections.Raivo -> navController.navigate(RAIVO)
        }
    }

    override fun navigateBack() {
        navigate(ExternalImportDirections.GoBack)
    }

    override fun startDirection(): String = MAIN
}