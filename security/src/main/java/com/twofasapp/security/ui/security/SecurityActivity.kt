package com.twofasapp.security.ui.security

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelStoreOwner
import com.twofasapp.base.BaseComponentActivity
import com.twofasapp.data.session.SettingsRepository
import com.twofasapp.design.theme.ThemeState
import com.twofasapp.designsystem.MainAppTheme
import com.twofasapp.navigation.SecurityRouter
import com.twofasapp.navigation.base.RouterNavHost
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.get
import org.koin.androidx.viewmodel.ext.android.viewModel

class SecurityActivity : BaseComponentActivity() {

    private val viewModel: SecurityViewModel by viewModel()
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeState.applyTheme(settingsRepository.getAppSettings().selectedTheme)

        super.onCreate(savedInstanceState)

        val viewModelStoreOwner = compositionLocalOf<ViewModelStoreOwner> { this }

        viewModel.init()

        setContent {
            MainAppTheme {
                RouterNavHost(router = get<SecurityRouter>(), viewModelStoreOwner.current)
            }
        }
    }
}