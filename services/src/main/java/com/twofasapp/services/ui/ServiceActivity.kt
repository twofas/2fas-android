package com.twofasapp.services.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.core.log.FileLogger
import com.twofasapp.design.theme.AppThemeLegacy
import com.twofasapp.navigation.ServiceRouter
import com.twofasapp.navigation.base.RouterNavHost
import org.koin.androidx.compose.get
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

        val serviceId = intent.getLongExtra(ARG_SERVICE_ID, 0L)

        FileLogger.logScreen(if (serviceId == 0L) "AddServiceManually" else "EditService")

        viewModel.init(serviceId)

        setContent {
            AppThemeLegacy {
                Surface {
                    RouterNavHost(router = get<ServiceRouter>(), viewModelStoreOwner.current)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_KEY_AUTH_SERVICE && resultCode == Activity.RESULT_OK) {
            viewModel.secretAuthenticated()
            return
        }
    }
}
