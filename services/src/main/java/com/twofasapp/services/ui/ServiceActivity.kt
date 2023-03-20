package com.twofasapp.services.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.compose.rememberNavController
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.core.log.FileLogger
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.design.theme.ThemeState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceActivity : BaseComponentActivity() {

    companion object {
        const val REQUEST_KEY_ADD_SERVICE = 46851
        const val REQUEST_KEY_AUTH_SERVICE = 7895
        const val ARG_SERVICE = "service"
        const val ARG_SERVICE_ID = "serviceId"
        const val ARG_SHOW_ICON_PICKER = "showIconPicker"
        const val RESULT_SERVICE = "service"
    }

    private val viewModel: ServiceViewModel by viewModel()
    private val externalNavigator: ServiceExternalNavigator by inject()
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)
        super.onCreate(savedInstanceState)
        val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

        val serviceId = intent.getLongExtra(ARG_SERVICE_ID, 0L)

        FileLogger.logScreen(if (serviceId == 0L) "AddServiceManually" else "EditService")

//        viewModel.init(serviceId)

        setContent {
            val navController = rememberNavController()


        }
    }
}
